package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.common.utils.SecurityUtils;
import com.pingan.ams.mapper.ContractChangeHistoryMapper;
import com.pingan.ams.mapper.ContractChangeMapper;
import com.pingan.ams.mapper.ContractMapper;
import com.pingan.ams.mapper.UserMapper;
import com.pingan.ams.model.dto.ContractChangeDTO;
import com.pingan.ams.model.entity.Contract;
import com.pingan.ams.model.entity.ContractChange;
import com.pingan.ams.model.entity.ContractChangeHistory;
import com.pingan.ams.model.entity.User;
import com.pingan.ams.model.enums.ContractStatus;
import com.pingan.ams.model.vo.ContractChangeVO;
import com.pingan.ams.service.ContractChangeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同变更申请 Service 实现
 */
@Service
public class ContractChangeServiceImpl extends ServiceImpl<ContractChangeMapper, ContractChange> implements ContractChangeService {

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ContractChangeHistoryMapper historyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createChange(Long tenantId, Long userId, ContractChangeDTO dto) {
        // 校验合同是否存在且属于当前租户
        Contract contract = contractMapper.selectById(dto.getContractId());
        if (contract == null || !contract.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "合同不存在");
        }

        // 校验合同状态必须是生效中
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BusinessException(ResultCode.CONTRACT_STATUS_ERROR.getCode(), "合同状态不是生效中，无法申请变更");
        }

        // 校验变更类型
        validateChangeType(dto);

        // 生成变更单号
        String changeNo = generateChangeNo();

        // 创建变更申请
        ContractChange change = new ContractChange();
        BeanUtils.copyProperties(dto, change);
        change.setTenantId(tenantId);
        change.setChangeNo(changeNo);
        change.setStatus(0); // 待审核
        change.setApplicantId(userId);

        this.save(change);

        // 记录变更历史
        saveHistory(contract.getId(), change.getId(), "CREATE", "创建合同变更申请", userId);

        return change.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveChange(Long tenantId, Long userId, Long changeId, Boolean approved, String remark) {
        // 校验变更申请是否存在且属于当前租户
        ContractChange change = this.getById(changeId);
        if (change == null || !change.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "变更申请不存在");
        }

        // 校验状态必须是待审核
        if (change.getStatus() != 0) {
            throw new BusinessException(ResultCode.CONTRACT_STATUS_ERROR.getCode(), "变更申请状态不是待审核");
        }

        // 更新审批信息
        change.setApproverId(userId);
        change.setApproveTime(LocalDateTime.now());
        change.setApproveRemark(remark);
        change.setStatus(approved ? 1 : 2); // 1-已通过 2-已驳回

        this.updateById(change);

        // 记录变更历史
        String actionType = approved ? "APPROVE" : "REJECT";
        String actionDesc = approved ? "审批通过" : "审批驳回";
        saveHistory(change.getContractId(), change.getId(), actionType, actionDesc, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeChange(Long tenantId, Long userId, Long changeId) {
        // 校验变更申请是否存在且属于当前租户
        ContractChange change = this.getById(changeId);
        if (change == null || !change.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "变更申请不存在");
        }

        // 校验状态必须是通过
        if (change.getStatus() != 1) {
            throw new BusinessException(ResultCode.CONTRACT_STATUS_ERROR.getCode(), "变更申请未通过审批");
        }

        // 获取原合同
        Contract contract = contractMapper.selectById(change.getContractId());
        if (contract == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "原合同不存在");
        }

        // 根据变更类型执行不同的逻辑
        switch (change.getChangeType()) {
            case 1: // 续约
                executeRenewal(contract, change);
                break;
            case 2: // 转租
                executeTransfer(contract, change);
                break;
            case 3: // 提前退租
                executeEarlyTermination(contract, change);
                break;
            default:
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不支持的变更类型");
        }

        // 更新变更申请状态为已执行
        change.setStatus(3);
        this.updateById(change);

        // 记录变更历史
        saveHistory(change.getContractId(), change.getId(), "EXECUTE", "执行合同变更", userId);
    }

    @Override
    public ContractChangeVO getChangeDetail(Long tenantId, Long changeId) {
        ContractChange change = this.getById(changeId);
        if (change == null || !change.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "变更申请不存在");
        }
        return convertToVO(change);
    }

    @Override
    public Page<ContractChangeVO> listChanges(Long tenantId, Long contractId, Integer changeType, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<ContractChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractChange::getTenantId, tenantId);

        if (contractId != null) {
            wrapper.eq(ContractChange::getContractId, contractId);
        }
        if (changeType != null) {
            wrapper.eq(ContractChange::getChangeType, changeType);
        }
        if (status != null) {
            wrapper.eq(ContractChange::getStatus, status);
        }

        wrapper.orderByDesc(ContractChange::getCreateTime);

        Page<ContractChange> changePage = this.page(new Page<>(page, size), wrapper);

        Page<ContractChangeVO> voPage = new Page<>(changePage.getCurrent(), changePage.getSize(), changePage.getTotal());
        List<ContractChangeVO> voList = changePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<ContractChangeVO> listChangesByContractId(Long tenantId, Long contractId) {
        LambdaQueryWrapper<ContractChange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractChange::getTenantId, tenantId);
        wrapper.eq(ContractChange::getContractId, contractId);
        wrapper.orderByDesc(ContractChange::getCreateTime);

        List<ContractChange> changes = this.list(wrapper);
        return changes.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 校验变更类型
     */
    private void validateChangeType(ContractChangeDTO dto) {
        switch (dto.getChangeType()) {
            case 1: // 续约
                if (dto.getNewStartDate() == null || dto.getNewEndDate() == null) {
                    throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "续约必须填写新的开始和结束日期");
                }
                break;
            case 2: // 转租
                if (dto.getNewUserId() == null) {
                    throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "转租必须指定新租客");
                }
                break;
            case 3: // 提前退租
                if (dto.getTerminateDate() == null) {
                    throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "提前退租必须填写退租日期");
                }
                break;
            default:
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不支持的变更类型");
        }
    }

    /**
     * 生成变更单号
     */
    private String generateChangeNo() {
        String prefix = "CHANGE";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return prefix + date + random;
    }

    /**
     * 保存变更历史
     */
    private void saveHistory(Long contractId, Long changeId, String actionType, String actionDesc, Long userId) {
        ContractChangeHistory history = new ContractChangeHistory();
        history.setContractId(contractId);
        history.setChangeId(changeId);
        history.setActionType(actionType);
        history.setActionDesc(actionDesc);
        history.setOperatorId(userId);

        // 获取操作人姓名
        User user = userMapper.selectById(userId);
        if (user != null) {
            history.setOperatorName(user.getRealName());
        }

        historyMapper.insert(history);
    }

    /**
     * 执行续约
     */
    private void executeRenewal(Contract contract, ContractChange change) {
        contract.setStartDate(change.getNewStartDate());
        contract.setEndDate(change.getNewEndDate());
        if (change.getNewMonthlyRent() != null) {
            contract.setMonthlyRent(change.getNewMonthlyRent());
        }
        contractMapper.updateById(contract);
    }

    /**
     * 执行转租
     */
    private void executeTransfer(Contract contract, ContractChange change) {
        contract.setUserId(change.getNewUserId());
        contractMapper.updateById(contract);
    }

    /**
     * 执行提前退租
     */
    private void executeEarlyTermination(Contract contract, ContractChange change) {
        contract.setEndDate(change.getTerminateDate());
        contract.setStatus(ContractStatus.TERMINATED);
        contractMapper.updateById(contract);
    }

    /**
     * 转换为VO
     */
    private ContractChangeVO convertToVO(ContractChange change) {
        ContractChangeVO vo = new ContractChangeVO();
        BeanUtils.copyProperties(change, vo);

        // 获取合同编号
        Contract contract = contractMapper.selectById(change.getContractId());
        if (contract != null) {
            vo.setContractNo(contract.getContractNo());
        }

        // 获取申请人姓名
        User applicant = userMapper.selectById(change.getApplicantId());
        if (applicant != null) {
            vo.setApplicantName(applicant.getRealName());
        }

        // 获取审批人姓名
        if (change.getApproverId() != null) {
            User approver = userMapper.selectById(change.getApproverId());
            if (approver != null) {
                vo.setApproverName(approver.getRealName());
            }
        }

        // 获取新租客姓名（转租时）
        if (change.getNewUserId() != null) {
            User newUser = userMapper.selectById(change.getNewUserId());
            if (newUser != null) {
                vo.setNewUserName(newUser.getRealName());
            }
        }

        // 设置变更类型描述
        switch (change.getChangeType()) {
            case 1:
                vo.setChangeTypeDesc("续约");
                break;
            case 2:
                vo.setChangeTypeDesc("转租");
                break;
            case 3:
                vo.setChangeTypeDesc("提前退租");
                break;
        }

        // 设置状态描述
        switch (change.getStatus()) {
            case 0:
                vo.setStatusDesc("待审核");
                break;
            case 1:
                vo.setStatusDesc("已通过");
                break;
            case 2:
                vo.setStatusDesc("已驳回");
                break;
            case 3:
                vo.setStatusDesc("已执行");
                break;
        }

        return vo;
    }
}

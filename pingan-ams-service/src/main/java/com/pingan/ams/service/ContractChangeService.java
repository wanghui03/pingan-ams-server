package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.ContractChangeDTO;
import com.pingan.ams.model.entity.ContractChange;
import com.pingan.ams.model.vo.ContractChangeVO;

import java.util.List;

/**
 * 合同变更申请 Service
 */
public interface ContractChangeService extends IService<ContractChange> {

    /**
     * 创建合同变更申请
     * @param tenantId 租户ID
     * @param userId 当前用户ID
     * @param dto 变更申请信息
     * @return 变更申请ID
     */
    Long createChange(Long tenantId, Long userId, ContractChangeDTO dto);

    /**
     * 审批合同变更申请
     * @param tenantId 租户ID
     * @param userId 当前用户ID
     * @param changeId 变更申请ID
     * @param approved 是否通过
     * @param remark 审批意见
     */
    void approveChange(Long tenantId, Long userId, Long changeId, Boolean approved, String remark);

    /**
     * 执行合同变更
     * @param tenantId 租户ID
     * @param userId 当前用户ID
     * @param changeId 变更申请ID
     */
    void executeChange(Long tenantId, Long userId, Long changeId);

    /**
     * 获取变更申请详情
     * @param tenantId 租户ID
     * @param changeId 变更申请ID
     * @return 变更申请详情
     */
    ContractChangeVO getChangeDetail(Long tenantId, Long changeId);

    /**
     * 分页查询变更申请列表
     * @param tenantId 租户ID
     * @param contractId 合同ID（可选）
     * @param changeType 变更类型（可选）
     * @param status 状态（可选）
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    Page<ContractChangeVO> listChanges(Long tenantId, Long contractId, Integer changeType, Integer status, Integer page, Integer size);

    /**
     * 根据合同ID查询变更申请列表
     * @param tenantId 租户ID
     * @param contractId 合同ID
     * @return 变更申请列表
     */
    List<ContractChangeVO> listChangesByContractId(Long tenantId, Long contractId);
}

package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.ContractDTO;
import com.pingan.ams.model.entity.Contract;
import com.pingan.ams.model.vo.ContractVO;

/**
 * 合同 Service
 */
public interface ContractService extends IService<Contract> {

    /**
     * 创建合同
     */
    Long createContract(Long tenantId, ContractDTO contractDTO);

    /**
     * 更新合同
     */
    void updateContract(Long tenantId, Long contractId, ContractDTO contractDTO);

    /**
     * 获取合同详情
     */
    ContractVO getContractDetail(Long tenantId, Long contractId);

    /**
     * 分页查询合同
     */
    Page<ContractVO> listContracts(Long tenantId, Integer page, Integer size, Integer status, Long userId, Long roomId);

    /**
     * 终止合同
     */
    void terminateContract(Long tenantId, Long contractId, String reason);

    /**
     * 根据房间ID获取生效合同
     */
    Contract getActiveContractByRoomId(Long roomId);

    /**
     * 根据租客ID获取生效合同
     */
    Contract getActiveContractByUserId(Long userId);

    /**
     * 提交审核
     */
    void submitContract(Long tenantId, Long contractId);

    /**
     * 审核通过
     */
    void approveContract(Long tenantId, Long contractId);

    /**
     * 审核驳回
     */
    void rejectContract(Long tenantId, Long contractId, String reason);

    /**
     * 自动处理到期合同
     */
    void processExpiredContracts();

    /**
     * 发送合同到期提醒（定时任务调用）
     * 提前30天提醒生效中合同的租客
     */
    void sendContractExpiryReminders();
}

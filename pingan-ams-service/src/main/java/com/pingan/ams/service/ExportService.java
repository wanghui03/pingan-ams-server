package com.pingan.ams.service;

import com.pingan.ams.model.vo.BillVO;
import com.pingan.ams.model.vo.ContractVO;
import com.pingan.ams.model.vo.WorkOrderVO;

import java.util.List;

/**
 * 数据导出 Service
 */
public interface ExportService {

    /**
     * 导出合同数据
     */
    byte[] exportContracts(Long tenantId, Integer status);

    /**
     * 导出账单数据
     */
    byte[] exportBills(Long tenantId, Integer status, Integer billType);

    /**
     * 导出工单数据
     */
    byte[] exportWorkOrders(Long tenantId, Integer status, Integer orderType);
}

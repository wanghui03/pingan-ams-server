package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pingan.ams.model.entity.Bill;
import com.pingan.ams.model.entity.Contract;
import com.pingan.ams.model.entity.WorkOrder;
import com.pingan.ams.model.enums.BillStatus;
import com.pingan.ams.model.enums.ContractStatus;
import com.pingan.ams.model.enums.WorkOrderStatus;
import com.pingan.ams.service.BillService;
import com.pingan.ams.service.ContractService;
import com.pingan.ams.service.ExportService;
import com.pingan.ams.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 数据导出 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ContractService contractService;
    private final BillService billService;
    private final WorkOrderService workOrderService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public byte[] exportContracts(Long tenantId, Integer status) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("合同列表");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"合同编号", "租客ID", "房间ID", "月租金", "押金", "支付方式", "开始日期", "结束日期", "状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // 查询数据
            LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Contract::getTenantId, tenantId);
            if (status != null) {
                ContractStatus contractStatus = convertToContractStatus(status);
                if (contractStatus != null) {
                    queryWrapper.eq(Contract::getStatus, contractStatus);
                }
            }
            List<Contract> contracts = contractService.list(queryWrapper);

            // 填充数据
            int rowNum = 1;
            for (Contract contract : contracts) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(contract.getContractNo());
                row.createCell(1).setCellValue(contract.getUserId() != null ? contract.getUserId().toString() : "");
                row.createCell(2).setCellValue(contract.getRoomId() != null ? contract.getRoomId().toString() : "");
                row.createCell(3).setCellValue(contract.getMonthlyRent() != null ? contract.getMonthlyRent().doubleValue() : 0);
                row.createCell(4).setCellValue(contract.getDeposit() != null ? contract.getDeposit().doubleValue() : 0);
                row.createCell(5).setCellValue(getPaymentMethodDesc(contract.getPaymentMethod()));
                row.createCell(6).setCellValue(contract.getStartDate() != null ? contract.getStartDate().format(DATE_FORMATTER) : "");
                row.createCell(7).setCellValue(contract.getEndDate() != null ? contract.getEndDate().format(DATE_FORMATTER) : "");
                row.createCell(8).setCellValue(contract.getStatus() != null ? contract.getStatus().getDesc() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            return writeToBytes(workbook);
        } catch (IOException e) {
            log.error("导出合同数据失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    @Override
    public byte[] exportBills(Long tenantId, Integer status, Integer billType) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("账单列表");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"账单编号", "合同ID", "账单类型", "金额", "账单日期", "截止日期", "状态"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // 查询数据
            LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Bill::getTenantId, tenantId);
            if (status != null) {
                BillStatus billStatus = convertToBillStatus(status);
                if (billStatus != null) {
                    queryWrapper.eq(Bill::getStatus, billStatus);
                }
            }
            if (billType != null) {
                queryWrapper.eq(Bill::getBillType, billType);
            }
            List<Bill> bills = billService.list(queryWrapper);

            // 填充数据
            int rowNum = 1;
            for (Bill bill : bills) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(bill.getBillNo());
                row.createCell(1).setCellValue(bill.getContractId() != null ? bill.getContractId().toString() : "");
                row.createCell(2).setCellValue(getBillTypeDesc(bill.getBillType()));
                row.createCell(3).setCellValue(bill.getAmount() != null ? bill.getAmount().doubleValue() : 0);
                row.createCell(4).setCellValue(bill.getBillDate() != null ? bill.getBillDate().format(DATE_FORMATTER) : "");
                row.createCell(5).setCellValue(bill.getDueDate() != null ? bill.getDueDate().format(DATE_FORMATTER) : "");
                row.createCell(6).setCellValue(bill.getStatus() != null ? bill.getStatus().getDesc() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            return writeToBytes(workbook);
        } catch (IOException e) {
            log.error("导出账单数据失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    @Override
    public byte[] exportWorkOrders(Long tenantId, Integer status, Integer orderType) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("工单列表");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"工单编号", "房间ID", "租客ID", "工单类型", "标题", "描述", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // 查询数据
            LambdaQueryWrapper<WorkOrder> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WorkOrder::getTenantId, tenantId);
            if (status != null) {
                WorkOrderStatus workOrderStatus = convertToWorkOrderStatus(status);
                if (workOrderStatus != null) {
                    queryWrapper.eq(WorkOrder::getStatus, workOrderStatus);
                }
            }
            if (orderType != null) {
                queryWrapper.eq(WorkOrder::getOrderType, orderType);
            }
            List<WorkOrder> workOrders = workOrderService.list(queryWrapper);

            // 填充数据
            int rowNum = 1;
            for (WorkOrder workOrder : workOrders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(workOrder.getOrderNo());
                row.createCell(1).setCellValue(workOrder.getRoomId() != null ? workOrder.getRoomId().toString() : "");
                row.createCell(2).setCellValue(workOrder.getUserId() != null ? workOrder.getUserId().toString() : "");
                row.createCell(3).setCellValue(getOrderTypeDesc(workOrder.getOrderType()));
                row.createCell(4).setCellValue(workOrder.getTitle() != null ? workOrder.getTitle() : "");
                row.createCell(5).setCellValue(workOrder.getDescription() != null ? workOrder.getDescription() : "");
                row.createCell(6).setCellValue(workOrder.getStatus() != null ? workOrder.getStatus().getDesc() : "");
                row.createCell(7).setCellValue(workOrder.getCreateTime() != null ? workOrder.getCreateTime().format(DATE_FORMATTER) : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            return writeToBytes(workbook);
        } catch (IOException e) {
            log.error("导出工单数据失败", e);
            throw new RuntimeException("导出失败");
        }
    }

    private byte[] writeToBytes(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }

    private ContractStatus convertToContractStatus(Integer code) {
        if (code == null) return null;
        for (ContractStatus status : ContractStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    private BillStatus convertToBillStatus(Integer code) {
        if (code == null) return null;
        for (BillStatus status : BillStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    private WorkOrderStatus convertToWorkOrderStatus(Integer code) {
        if (code == null) return null;
        for (WorkOrderStatus status : WorkOrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    private String getPaymentMethodDesc(Integer paymentMethod) {
        if (paymentMethod == null) return "未知";
        return switch (paymentMethod) {
            case 1 -> "月付";
            case 2 -> "季付";
            case 3 -> "半年付";
            case 4 -> "年付";
            default -> "未知";
        };
    }

    private String getBillTypeDesc(Integer billType) {
        if (billType == null) return "未知";
        return switch (billType) {
            case 1 -> "租金";
            case 2 -> "水电费";
            case 3 -> "押金";
            case 4 -> "其他";
            default -> "未知";
        };
    }

    private String getOrderTypeDesc(Integer orderType) {
        if (orderType == null) return "未知";
        return switch (orderType) {
            case 1 -> "报修";
            case 2 -> "投诉";
            case 3 -> "咨询";
            case 4 -> "其他";
            default -> "未知";
        };
    }
}

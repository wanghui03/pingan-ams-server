package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pingan.ams.mapper.*;
import com.pingan.ams.model.entity.*;
import com.pingan.ams.model.enums.BillStatus;
import com.pingan.ams.model.enums.ContractStatus;
import com.pingan.ams.model.enums.RoomStatus;
import com.pingan.ams.model.enums.WorkOrderStatus;
import com.pingan.ams.model.vo.DashboardStatsVO;
import com.pingan.ams.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 首页统计 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final ContractMapper contractMapper;
    private final BillMapper billMapper;
    private final WorkOrderMapper workOrderMapper;

    @Override
    public DashboardStatsVO getStats(Long tenantId) {
        DashboardStatsVO stats = new DashboardStatsVO();

        // 1. 楼栋统计
        LambdaQueryWrapper<Building> buildingWrapper = new LambdaQueryWrapper<Building>()
                .eq(Building::getDeleted, 0);
        if (tenantId != null) {
            buildingWrapper.eq(Building::getTenantId, tenantId);
        }
        Long buildingCount = buildingMapper.selectCount(buildingWrapper);
        stats.setBuildingCount(buildingCount);

        // 2. 房间统计
        LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<Room>()
                .eq(Room::getDeleted, 0);
        if (tenantId != null) {
            roomWrapper.eq(Room::getTenantId, tenantId);
        }
        Long roomCount = roomMapper.selectCount(roomWrapper);
        stats.setRoomCount(roomCount);

        LambdaQueryWrapper<Room> occupiedWrapper = new LambdaQueryWrapper<Room>()
                .eq(Room::getStatus, RoomStatus.OCCUPIED.getCode())
                .eq(Room::getDeleted, 0);
        if (tenantId != null) {
            occupiedWrapper.eq(Room::getTenantId, tenantId);
        }
        Long occupiedRoomCount = roomMapper.selectCount(occupiedWrapper);
        stats.setOccupiedRoomCount(occupiedRoomCount);

        LambdaQueryWrapper<Room> vacantWrapper = new LambdaQueryWrapper<Room>()
                .eq(Room::getStatus, RoomStatus.VACANT.getCode())
                .eq(Room::getDeleted, 0);
        if (tenantId != null) {
            vacantWrapper.eq(Room::getTenantId, tenantId);
        }
        Long vacantRoomCount = roomMapper.selectCount(vacantWrapper);
        stats.setVacantRoomCount(vacantRoomCount);

        // 计算入住率
        if (roomCount > 0) {
            double rate = (double) occupiedRoomCount / roomCount * 100;
            stats.setOccupancyRate(String.format("%.1f%%", rate));
        }

        // 3. 合同统计
        LambdaQueryWrapper<Contract> contractWrapper = new LambdaQueryWrapper<Contract>()
                .eq(Contract::getDeleted, 0);
        if (tenantId != null) {
            contractWrapper.eq(Contract::getTenantId, tenantId);
        }
        Long contractCount = contractMapper.selectCount(contractWrapper);
        stats.setContractCount(contractCount);

        LambdaQueryWrapper<Contract> activeContractWrapper = new LambdaQueryWrapper<Contract>()
                .eq(Contract::getStatus, ContractStatus.ACTIVE.getCode())
                .eq(Contract::getDeleted, 0);
        if (tenantId != null) {
            activeContractWrapper.eq(Contract::getTenantId, tenantId);
        }
        Long activeContractCount = contractMapper.selectCount(activeContractWrapper);
        stats.setActiveContractCount(activeContractCount);

        // 4. 账单统计
        LambdaQueryWrapper<Bill> billWrapper = new LambdaQueryWrapper<Bill>()
                .eq(Bill::getDeleted, 0);
        if (tenantId != null) {
            billWrapper.eq(Bill::getTenantId, tenantId);
        }
        List<Bill> bills = billMapper.selectList(billWrapper);

        BigDecimal unpaidAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        Long overdueBillCount = 0L;

        for (Bill bill : bills) {
            if (bill.getStatus() == BillStatus.PAID) {
                paidAmount = paidAmount.add(bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO);
            } else if (bill.getStatus() == BillStatus.UNPAID || bill.getStatus() == BillStatus.OVERDUE) {
                BigDecimal remaining = bill.getAmount().subtract(bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO);
                unpaidAmount = unpaidAmount.add(remaining);
            }

            if (bill.getStatus() == BillStatus.OVERDUE) {
                overdueBillCount++;
            }
        }

        stats.setUnpaidAmount(unpaidAmount.setScale(2, RoundingMode.HALF_UP));
        stats.setPaidAmount(paidAmount.setScale(2, RoundingMode.HALF_UP));
        stats.setOverdueBillCount(overdueBillCount);

        // 5. 工单统计
        LambdaQueryWrapper<WorkOrder> woWrapper = new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getStatus, WorkOrderStatus.PENDING.getCode())
                .eq(WorkOrder::getDeleted, 0);
        if (tenantId != null) {
            woWrapper.eq(WorkOrder::getTenantId, tenantId);
        }
        Long pendingWorkOrderCount = workOrderMapper.selectCount(woWrapper);
        stats.setPendingWorkOrderCount(pendingWorkOrderCount);

        return stats;
    }
}

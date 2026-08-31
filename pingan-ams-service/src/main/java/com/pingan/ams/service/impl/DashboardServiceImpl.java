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
        Long buildingCount = buildingMapper.selectCount(new LambdaQueryWrapper<Building>()
                .eq(Building::getTenantId, tenantId)
                .eq(Building::getDeleted, 0));
        stats.setBuildingCount(buildingCount);

        // 2. 房间统计
        Long roomCount = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getTenantId, tenantId)
                .eq(Room::getDeleted, 0));
        stats.setRoomCount(roomCount);

        Long occupiedRoomCount = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getTenantId, tenantId)
                .eq(Room::getStatus, RoomStatus.OCCUPIED.getCode())
                .eq(Room::getDeleted, 0));
        stats.setOccupiedRoomCount(occupiedRoomCount);

        Long vacantRoomCount = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getTenantId, tenantId)
                .eq(Room::getStatus, RoomStatus.VACANT.getCode())
                .eq(Room::getDeleted, 0));
        stats.setVacantRoomCount(vacantRoomCount);

        // 计算入住率
        if (roomCount > 0) {
            double rate = (double) occupiedRoomCount / roomCount * 100;
            stats.setOccupancyRate(String.format("%.1f%%", rate));
        }

        // 3. 合同统计
        Long contractCount = contractMapper.selectCount(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getTenantId, tenantId)
                .eq(Contract::getDeleted, 0));
        stats.setContractCount(contractCount);

        Long activeContractCount = contractMapper.selectCount(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getTenantId, tenantId)
                .eq(Contract::getStatus, ContractStatus.ACTIVE.getCode())
                .eq(Contract::getDeleted, 0));
        stats.setActiveContractCount(activeContractCount);

        // 4. 账单统计
        List<Bill> bills = billMapper.selectList(new LambdaQueryWrapper<Bill>()
                .eq(Bill::getTenantId, tenantId)
                .eq(Bill::getDeleted, 0));

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
        Long pendingWorkOrderCount = workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getTenantId, tenantId)
                .eq(WorkOrder::getStatus, WorkOrderStatus.PENDING.getCode())
                .eq(WorkOrder::getDeleted, 0));
        stats.setPendingWorkOrderCount(pendingWorkOrderCount);

        return stats;
    }
}

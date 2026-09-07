package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.*;
import com.pingan.ams.model.dto.BillDTO;
import com.pingan.ams.model.dto.MeterReadingDTO;
import com.pingan.ams.model.entity.*;
import com.pingan.ams.model.enums.BillStatus;
import com.pingan.ams.model.vo.MeterReadingVO;
import com.pingan.ams.service.BillService;
import com.pingan.ams.service.MeterReadingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抄表记录 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl extends ServiceImpl<MeterReadingMapper, MeterReading> implements MeterReadingService {

    private final RoomMapper roomMapper;
    private final BuildingMapper buildingMapper;
    private final BillService billService;
    private final ContractMapper contractMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addMeterReading(Long tenantId, MeterReadingDTO dto) {
        // 获取房间信息
        Room room = roomMapper.selectById(dto.getRoomId());
        if (room == null || !room.getTenantId().equals(tenantId)) {
            throw new BusinessException("房间不存在");
        }

        // 检查房间是否配置了单价
        if (dto.getMeterType() == 1 && (room.getWaterPrice() == null || room.getWaterPrice().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new BusinessException("房间未配置水费单价");
        }
        if (dto.getMeterType() == 2 && (room.getElectricityPrice() == null || room.getElectricityPrice().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new BusinessException("房间未配置电费单价");
        }

        // 获取上次读数
        BigDecimal previousReading = getPreviousReading(room, dto.getMeterType());

        // 检查本次读数是否大于上次读数
        if (dto.getCurrentReading().compareTo(previousReading) < 0) {
            throw new BusinessException("本次读数不能小于上次读数");
        }

        // 计算用量和费用
        BigDecimal meterUsage = dto.getCurrentReading().subtract(previousReading);
        BigDecimal unitPrice = dto.getMeterType() == 1 ? room.getWaterPrice() : room.getElectricityPrice();
        BigDecimal amount = meterUsage.multiply(unitPrice);

        // 创建抄表记录
        MeterReading reading = new MeterReading();
        BeanUtils.copyProperties(dto, reading);
        reading.setTenantId(tenantId);
        reading.setPreviousReading(previousReading);
        reading.setMeterUsage(meterUsage);
        reading.setUnitPrice(unitPrice);
        reading.setAmount(amount);
        reading.setBillGenerated(0);

        this.save(reading);

        // 更新房间的上次读数
        updateRoomReading(room, dto.getMeterType(), dto.getCurrentReading());

        // 如果需要生成账单，则生成
        if (dto.getGenerateBill() != null && dto.getGenerateBill() == 1) {
            generateBillForReading(reading, room);
        }

        log.info("新增抄表记录：房间 {}，类型 {}，用量 {}，费用 {}", room.getRoomNo(), 
                dto.getMeterType() == 1 ? "水表" : "电表", meterUsage, amount);

        return reading.getId();
    }

    @Override
    public MeterReadingVO getMeterReadingDetail(Long tenantId, Long id) {
        MeterReading reading = this.getById(id);
        if (reading == null || !reading.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToVO(reading);
    }

    @Override
    public Page<MeterReadingVO> listMeterReadings(Long tenantId, Integer page, Integer size, Long roomId, Integer meterType) {
        Page<MeterReading> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<MeterReading> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(MeterReading::getTenantId, tenantId);
        }
        if (roomId != null) {
            wrapper.eq(MeterReading::getRoomId, roomId);
        }
        if (meterType != null) {
            wrapper.eq(MeterReading::getMeterType, meterType);
        }

        wrapper.orderByDesc(MeterReading::getReadingDate);

        Page<MeterReading> readingPage = this.page(pageParam, wrapper);

        Page<MeterReadingVO> voPage = new Page<>(readingPage.getCurrent(), readingPage.getSize(), readingPage.getTotal());
        List<MeterReadingVO> voList = readingPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateBill(Long tenantId, Long id) {
        MeterReading reading = this.getById(id);
        if (reading == null || !reading.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (reading.getBillGenerated() == 1) {
            throw new BusinessException("该记录已生成账单");
        }

        Room room = roomMapper.selectById(reading.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }

        return generateBillForReading(reading, room);
    }

    /**
     * 为抄表记录生成账单
     */
    private Long generateBillForReading(MeterReading reading, Room room) {
        // 查找房间的当前有效合同
        Contract contract = contractMapper.selectOne(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getRoomId, room.getId())
                .eq(Contract::getStatus, com.pingan.ams.model.enums.ContractStatus.ACTIVE)
                .last("LIMIT 1"));

        if (contract == null) {
            throw new BusinessException("该房间没有生效中的合同，无法生成账单");
        }

        // 创建账单
        BillDTO billDTO = new BillDTO();
        billDTO.setContractId(contract.getId());
        billDTO.setBillType(reading.getMeterType() == 1 ? 2 : 5); // 2-水费 5-电费
        billDTO.setAmount(reading.getAmount());
        billDTO.setStartDate(reading.getReadingDate());
        billDTO.setEndDate(reading.getReadingDate().plusMonths(1));
        billDTO.setDueDate(reading.getReadingDate().plusDays(10)); // 10天内支付

        Long billId = billService.createBill(reading.getTenantId(), billDTO);

        // 更新抄表记录
        reading.setBillGenerated(1);
        reading.setBillId(billId);
        this.updateById(reading);

        log.info("抄表记录 {} 已生成账单 {}", reading.getId(), billId);

        return billId;
    }

    /**
     * 获取上次读数
     */
    private BigDecimal getPreviousReading(Room room, Integer meterType) {
        if (meterType == 1) {
            return room.getLastWaterReading() != null ? room.getLastWaterReading() : BigDecimal.ZERO;
        } else {
            return room.getLastElectricityReading() != null ? room.getLastElectricityReading() : BigDecimal.ZERO;
        }
    }

    /**
     * 更新房间的上次读数
     */
    private void updateRoomReading(Room room, Integer meterType, BigDecimal currentReading) {
        if (meterType == 1) {
            room.setLastWaterReading(currentReading);
        } else {
            room.setLastElectricityReading(currentReading);
        }
        roomMapper.updateById(room);
    }

    /**
     * 转换为VO
     */
    private MeterReadingVO convertToVO(MeterReading reading) {
        MeterReadingVO vo = new MeterReadingVO();
        BeanUtils.copyProperties(reading, vo);

        // 设置抄表类型描述
        vo.setMeterTypeDesc(reading.getMeterType() == 1 ? "水表" : "电表");

        // 获取房间信息
        Room room = roomMapper.selectById(reading.getRoomId());
        if (room != null) {
            vo.setRoomNo(room.getRoomNo());
            Building building = buildingMapper.selectById(room.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }

        return vo;
    }
}

package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.BuildingMapper;
import com.pingan.ams.mapper.RoomMapper;
import com.pingan.ams.model.dto.RoomDTO;
import com.pingan.ams.model.entity.Building;
import com.pingan.ams.model.entity.Room;
import com.pingan.ams.model.enums.RoomStatus;
import com.pingan.ams.model.vo.RoomVO;
import com.pingan.ams.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 房间 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    private final BuildingMapper buildingMapper;

    @Override
    public Long createRoom(Long tenantId, RoomDTO roomDTO) {
        Room room = new Room();
        BeanUtils.copyProperties(roomDTO, room);
        room.setTenantId(tenantId);
        room.setStatus(RoomStatus.VACANT);
        
        this.save(room);
        return room.getId();
    }

    @Override
    public void updateRoom(Long tenantId, Long roomId, RoomDTO roomDTO) {
        Room room = this.getRoomByIdAndTenantId(roomId, tenantId);
        BeanUtils.copyProperties(roomDTO, room);
        this.updateById(room);
    }

    @Override
    public RoomVO getRoomDetail(Long tenantId, Long roomId) {
        Room room = this.getRoomByIdAndTenantId(roomId, tenantId);
        return convertToVO(room);
    }

    @Override
    public Page<RoomVO> listRooms(Long tenantId, Integer page, Integer size, Integer status, Integer roomType) {
        Page<Room> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getTenantId, tenantId);
        
        if (status != null) {
            wrapper.eq(Room::getStatus, status);
        }
        if (roomType != null) {
            wrapper.eq(Room::getRoomType, roomType);
        }
        
        wrapper.orderByDesc(Room::getCreateTime);
        
        Page<Room> roomPage = this.page(pageParam, wrapper);
        
        // 转换为VO
        Page<RoomVO> voPage = new Page<>(roomPage.getCurrent(), roomPage.getSize(), roomPage.getTotal());
        List<RoomVO> voList = roomPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public void deleteRoom(Long tenantId, Long roomId) {
        Room room = this.getRoomByIdAndTenantId(roomId, tenantId);
        
        // 检查房间状态
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new BusinessException("房间已入住，无法删除");
        }
        
        this.removeById(roomId);
    }

    /**
     * 根据ID和租户ID获取房间
     */
    private Room getRoomByIdAndTenantId(Long roomId, Long tenantId) {
        Room room = this.getById(roomId);
        if (room == null || !room.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return room;
    }

    /**
     * 转换为VO
     */
    private RoomVO convertToVO(Room room) {
        RoomVO vo = new RoomVO();
        BeanUtils.copyProperties(room, vo);
        
        // 查询楼栋名称
        if (room.getBuildingId() != null) {
            Building building = buildingMapper.selectById(room.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getName());
            }
        }
        
        // 设置枚举描述
        if (room.getRoomType() != null) {
            vo.setRoomTypeDesc(room.getRoomType().getDesc());
        }
        if (room.getStatus() != null) {
            vo.setStatusDesc(room.getStatus().getDesc());
        }
        
        return vo;
    }
}

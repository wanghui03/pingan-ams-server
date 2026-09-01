package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.BuildingMapper;
import com.pingan.ams.mapper.RoomMapper;
import com.pingan.ams.model.dto.BuildingDTO;
import com.pingan.ams.model.entity.Building;
import com.pingan.ams.model.entity.Room;
import com.pingan.ams.model.enums.RoomStatus;
import com.pingan.ams.model.vo.BuildingVO;
import com.pingan.ams.service.BuildingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 楼栋 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    private final RoomMapper roomMapper;

    @Override
    public Long createBuilding(Long tenantId, BuildingDTO buildingDTO) {
        Building building = new Building();
        BeanUtils.copyProperties(buildingDTO, building);
        building.setTenantId(tenantId);
        building.setStatus(1);
        
        this.save(building);
        return building.getId();
    }

    @Override
    public void updateBuilding(Long tenantId, Long buildingId, BuildingDTO buildingDTO) {
        Building building = this.getBuildingByIdAndTenantId(buildingId, tenantId);
        BeanUtils.copyProperties(buildingDTO, building);
        this.updateById(building);
    }

    @Override
    public BuildingVO getBuildingDetail(Long tenantId, Long buildingId) {
        Building building = this.getBuildingByIdAndTenantId(buildingId, tenantId);
        return convertToVO(building);
    }

    @Override
    public Page<BuildingVO> listBuildings(Long tenantId, Integer page, Integer size) {
        Page<Building> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(Building::getTenantId, tenantId);
        }
        wrapper.orderByDesc(Building::getCreateTime);
        
        Page<Building> buildingPage = this.page(pageParam, wrapper);
        
        Page<BuildingVO> voPage = new Page<>(buildingPage.getCurrent(), buildingPage.getSize(), buildingPage.getTotal());
        List<BuildingVO> voList = buildingPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public List<BuildingVO> getAllBuildings(Long tenantId) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) {
            wrapper.eq(Building::getTenantId, tenantId);
        }
        wrapper.eq(Building::getStatus, 1);
        wrapper.orderByAsc(Building::getName);
        
        List<Building> buildings = this.list(wrapper);
        return buildings.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBuilding(Long tenantId, Long buildingId) {
        Building building = this.getBuildingByIdAndTenantId(buildingId, tenantId);
        
        // 检查楼栋下是否有房间
        Long roomCount = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getBuildingId, buildingId));
        if (roomCount > 0) {
            throw new BusinessException("该楼栋下还有房间，无法删除");
        }
        
        this.removeById(buildingId);
    }

    private Building getBuildingByIdAndTenantId(Long buildingId, Long tenantId) {
        Building building = this.getById(buildingId);
        if (building == null || !building.getTenantId().equals(tenantId)) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return building;
    }

    private BuildingVO convertToVO(Building building) {
        BuildingVO vo = new BuildingVO();
        BeanUtils.copyProperties(building, vo);
        
        // 统计房间数量
        Long totalRooms = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getBuildingId, building.getId()));
        Long vacantRooms = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getBuildingId, building.getId())
                .eq(Room::getStatus, RoomStatus.VACANT));
        
        vo.setRoomCount(totalRooms.intValue());
        vo.setVacantCount(vacantRooms.intValue());
        
        return vo;
    }
}

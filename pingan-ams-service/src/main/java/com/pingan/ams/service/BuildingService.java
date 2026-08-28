package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.BuildingDTO;
import com.pingan.ams.model.entity.Building;
import com.pingan.ams.model.vo.BuildingVO;

import java.util.List;

/**
 * 楼栋 Service
 */
public interface BuildingService extends IService<Building> {

    /**
     * 创建楼栋
     */
    Long createBuilding(Long tenantId, BuildingDTO buildingDTO);

    /**
     * 更新楼栋
     */
    void updateBuilding(Long tenantId, Long buildingId, BuildingDTO buildingDTO);

    /**
     * 获取楼栋详情
     */
    BuildingVO getBuildingDetail(Long tenantId, Long buildingId);

    /**
     * 分页查询楼栋
     */
    Page<BuildingVO> listBuildings(Long tenantId, Integer page, Integer size);

    /**
     * 获取所有楼栋（下拉选择用）
     */
    List<BuildingVO> getAllBuildings(Long tenantId);

    /**
     * 删除楼栋
     */
    void deleteBuilding(Long tenantId, Long buildingId);
}

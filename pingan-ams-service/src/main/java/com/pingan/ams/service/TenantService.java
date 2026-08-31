package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.TenantDTO;
import com.pingan.ams.model.entity.Tenant;
import com.pingan.ams.model.vo.TenantVO;

import java.util.List;

/**
 * 租户管理 Service
 */
public interface TenantService extends IService<Tenant> {

    /**
     * 创建租户
     */
    Long createTenant(TenantDTO tenantDTO);

    /**
     * 更新租户
     */
    void updateTenant(Long tenantId, TenantDTO tenantDTO);

    /**
     * 删除租户
     */
    void deleteTenant(Long tenantId);

    /**
     * 获取租户详情
     */
    TenantVO getTenantDetail(Long tenantId);

    /**
     * 分页查询租户列表
     */
    Page<TenantVO> listTenants(Integer page, Integer size, String keyword);

    /**
     * 获取所有租户（下拉选择用）
     */
    List<TenantVO> getAllTenants();

    /**
     * 启用/禁用租户
     */
    void updateTenantStatus(Long tenantId, Integer status);
}

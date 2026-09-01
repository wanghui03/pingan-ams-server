package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.common.utils.PasswordUtils;
import com.pingan.ams.mapper.TenantMapper;
import com.pingan.ams.model.dto.TenantDTO;
import com.pingan.ams.model.entity.Room;
import com.pingan.ams.model.entity.Tenant;
import com.pingan.ams.model.entity.User;
import com.pingan.ams.model.vo.TenantVO;
import com.pingan.ams.mapper.UserMapper;
import com.pingan.ams.service.RoomService;
import com.pingan.ams.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户管理 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    private final RoomService roomService;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTenant(TenantDTO tenantDTO) {
        // 检查租户名称是否已存在
        Tenant existing = this.getOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getName, tenantDTO.getName()));
        if (existing != null) {
            throw new BusinessException("租户名称已存在");
        }

        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(tenantDTO, tenant);
        if (tenant.getStatus() == null) {
            tenant.setStatus(1); // 默认启用
        }
        this.save(tenant);

        log.info("创建租户成功，租户ID: {}, 名称: {}", tenant.getId(), tenant.getName());

        // 创建管理员账号
        if (tenantDTO.getAdminUsername() != null && !tenantDTO.getAdminUsername().trim().isEmpty()) {
            // 检查用户名是否已存在
            User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, tenantDTO.getAdminUsername()));
            if (existingUser != null) {
                throw new BusinessException("管理员账号已存在");
            }

            User adminUser = new User();
            adminUser.setTenantId(tenant.getId());
            adminUser.setUsername(tenantDTO.getAdminUsername());
            adminUser.setPassword(PasswordUtils.encode(tenantDTO.getAdminPassword() != null ? tenantDTO.getAdminPassword() : "admin123"));
            adminUser.setRealName(tenantDTO.getAdminRealName());
            adminUser.setPhone(tenantDTO.getContactPhone());
            adminUser.setUserType(3); // 租户管理员
            adminUser.setStatus(1);
            adminUser.setAuthStatus(1);
            userMapper.insert(adminUser);

            log.info("创建租户管理员成功，租户ID: {}, 管理员账号: {}", tenant.getId(), tenantDTO.getAdminUsername());
        }

        return tenant.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenant(Long tenantId, TenantDTO tenantDTO) {
        Tenant tenant = this.getById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查名称是否与其他租户重复
        Tenant existing = this.getOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getName, tenantDTO.getName())
                .ne(Tenant::getId, tenantId));
        if (existing != null) {
            throw new BusinessException("租户名称已存在");
        }

        BeanUtils.copyProperties(tenantDTO, tenant);
        this.updateById(tenant);

        log.info("更新租户成功，租户ID: {}", tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenant(Long tenantId) {
        Tenant tenant = this.getById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查是否有房间数据
        long roomCount = roomService.count(new LambdaQueryWrapper<Room>()
                .eq(Room::getTenantId, tenantId));
        if (roomCount > 0) {
            throw new BusinessException("该租户下还有房间数据，无法删除");
        }

        this.removeById(tenantId);
        log.info("删除租户成功，租户ID: {}", tenantId);
    }

    @Override
    public TenantVO getTenantDetail(Long tenantId) {
        Tenant tenant = this.getById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToVO(tenant);
    }

    @Override
    public Page<TenantVO> listTenants(Integer page, Integer size, String keyword) {
        Page<Tenant> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Tenant::getName, keyword)
                    .or().like(Tenant::getContactPerson, keyword)
                    .or().like(Tenant::getContactPhone, keyword));
        }
        wrapper.orderByDesc(Tenant::getCreateTime);

        Page<Tenant> tenantPage = this.page(pageParam, wrapper);

        Page<TenantVO> voPage = new Page<>(tenantPage.getCurrent(), tenantPage.getSize(), tenantPage.getTotal());
        voPage.setRecords(tenantPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public List<TenantVO> getAllTenants() {
        List<Tenant> tenants = this.list(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getStatus, 1)
                .orderByAsc(Tenant::getName));
        return tenants.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantStatus(Long tenantId, Integer status) {
        Tenant tenant = this.getById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        tenant.setStatus(status);
        this.updateById(tenant);
        log.info("更新租户状态成功，租户ID: {}, 状态: {}", tenantId, status);
    }

    /**
     * 转换为VO
     */
    private TenantVO convertToVO(Tenant tenant) {
        TenantVO vo = new TenantVO();
        BeanUtils.copyProperties(tenant, vo);

        // 设置类型描述
        if (tenant.getType() != null) {
            vo.setTypeDesc(tenant.getType() == 1 ? "企业" : "个人");
        }

        // 设置状态描述
        if (tenant.getStatus() != null) {
            vo.setStatusDesc(tenant.getStatus() == 1 ? "启用" : "禁用");
        }

        // 统计房间数和入住率
        long totalRooms = roomService.count(new LambdaQueryWrapper<Room>()
                .eq(Room::getTenantId, tenant.getId()));
        long occupiedRooms = roomService.count(new LambdaQueryWrapper<Room>()
                .eq(Room::getTenantId, tenant.getId())
                .eq(Room::getStatus, 2)); // 2-已入住

        vo.setRoomCount((int) totalRooms);
        if (totalRooms > 0) {
            double rate = (double) occupiedRooms / totalRooms * 100;
            vo.setOccupancyRate(String.format("%.1f%%", rate));
        } else {
            vo.setOccupancyRate("0%");
        }

        return vo;
    }
}

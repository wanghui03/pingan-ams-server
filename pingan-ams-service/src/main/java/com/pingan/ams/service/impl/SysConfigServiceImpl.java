package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.SysConfigMapper;
import com.pingan.ams.mapper.TenantMapper;
import com.pingan.ams.model.dto.SysConfigDTO;
import com.pingan.ams.model.entity.SysConfig;
import com.pingan.ams.model.entity.Tenant;
import com.pingan.ams.model.vo.SysConfigVO;
import com.pingan.ams.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private final TenantMapper tenantMapper;

    @Override
    public List<SysConfigVO> listConfigs(Long tenantId) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        
        // 查询全局配置和当前租户配置
        wrapper.and(w -> w.isNull(SysConfig::getTenantId)
                .or()
                .eq(tenantId != null, SysConfig::getTenantId, tenantId));
        
        wrapper.orderByAsc(SysConfig::getConfigKey);

        List<SysConfig> configs = this.list(wrapper);
        
        return configs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public String getConfigValue(String configKey, Long tenantId) {
        // 先查租户级配置
        if (tenantId != null) {
            LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysConfig::getConfigKey, configKey)
                    .eq(SysConfig::getTenantId, tenantId);
            SysConfig config = this.getOne(wrapper);
            if (config != null) {
                return config.getConfigValue();
            }
        }
        
        // 再查全局配置
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey)
                .isNull(SysConfig::getTenantId);
        SysConfig config = this.getOne(wrapper);
        
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateConfig(Long tenantId, SysConfigDTO dto) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, dto.getConfigKey());
        
        if (tenantId != null) {
            wrapper.eq(SysConfig::getTenantId, tenantId);
        } else {
            wrapper.isNull(SysConfig::getTenantId);
        }

        SysConfig existing = this.getOne(wrapper);
        
        if (existing != null) {
            existing.setConfigValue(dto.getConfigValue());
            if (dto.getDescription() != null) {
                existing.setDescription(dto.getDescription());
            }
            this.updateById(existing);
        } else {
            SysConfig config = new SysConfig();
            config.setTenantId(tenantId);
            config.setConfigKey(dto.getConfigKey());
            config.setConfigValue(dto.getConfigValue());
            config.setDescription(dto.getDescription());
            this.save(config);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        SysConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        this.removeById(id);
    }

    @Override
    public boolean getBooleanConfig(String configKey, Long tenantId, boolean defaultValue) {
        String value = getConfigValue(configKey, tenantId);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }

    private SysConfigVO convertToVO(SysConfig config) {
        SysConfigVO vo = new SysConfigVO();
        BeanUtils.copyProperties(config, vo);
        
        if (config.getTenantId() != null) {
            Tenant tenant = tenantMapper.selectById(config.getTenantId());
            if (tenant != null) {
                vo.setTenantName(tenant.getName());
            }
        }
        
        return vo;
    }
}

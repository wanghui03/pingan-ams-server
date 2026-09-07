package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.SysConfigMapper;
import com.pingan.ams.model.dto.SysConfigDTO;
import com.pingan.ams.model.entity.SysConfig;
import com.pingan.ams.model.vo.SysConfigVO;
import com.pingan.ams.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置 Service 实现
 */
@Slf4j
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Lazy
    @Autowired
    private SysConfigService service;

    @Override
    public List<SysConfigVO> listConfigs() {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysConfig::getConfigKey);

        List<SysConfig> configs = this.list(wrapper);

        return configs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "sysConfig", key = "#configKey")
    public String getConfigValue(String configKey) {
        log.debug("查询配置 {}，未命中缓存", configKey);
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        SysConfig config = this.getOne(wrapper);

        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysConfig", allEntries = true)
    public void saveOrUpdateConfig(SysConfigDTO dto) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, dto.getConfigKey());

        SysConfig existing = this.getOne(wrapper);

        if (existing != null) {
            existing.setConfigValue(dto.getConfigValue());
            if (dto.getDescription() != null) {
                existing.setDescription(dto.getDescription());
            }
            this.updateById(existing);
        } else {
            SysConfig config = new SysConfig();
            config.setConfigKey(dto.getConfigKey());
            config.setConfigValue(dto.getConfigValue());
            config.setDescription(dto.getDescription());
            this.save(config);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysConfig", allEntries = true)
    public void deleteConfig(Long id) {
        SysConfig config = this.getById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        this.removeById(id);
    }

    @Override
    public boolean getBooleanConfig(String configKey, boolean defaultValue) {
        String value = service.getConfigValue(configKey);
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }

    private SysConfigVO convertToVO(SysConfig config) {
        SysConfigVO vo = new SysConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }
}

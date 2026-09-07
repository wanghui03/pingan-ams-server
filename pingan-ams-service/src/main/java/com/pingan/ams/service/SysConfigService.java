package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.SysConfigDTO;
import com.pingan.ams.model.entity.SysConfig;
import com.pingan.ams.model.vo.SysConfigVO;

import java.util.List;

/**
 * 系统配置 Service
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 获取配置列表
     */
    List<SysConfigVO> listConfigs();

    /**
     * 获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 保存或更新配置
     */
    void saveOrUpdateConfig(SysConfigDTO dto);

    /**
     * 删除配置
     */
    void deleteConfig(Long id);

    /**
     * 获取布尔配置
     */
    boolean getBooleanConfig(String configKey, boolean defaultValue);
}

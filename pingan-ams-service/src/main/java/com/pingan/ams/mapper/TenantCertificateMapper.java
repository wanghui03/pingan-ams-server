package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.TenantCertificate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户证件 Mapper
 */
@Mapper
public interface TenantCertificateMapper extends BaseMapper<TenantCertificate> {
}

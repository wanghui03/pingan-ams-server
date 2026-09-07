package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.Certificate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 证件 Mapper
 */
@Mapper
public interface CertificateMapper extends BaseMapper<Certificate> {
}

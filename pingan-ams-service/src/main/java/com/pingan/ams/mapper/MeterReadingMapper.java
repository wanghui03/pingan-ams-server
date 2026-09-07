package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.MeterReading;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抄表记录 Mapper
 */
@Mapper
public interface MeterReadingMapper extends BaseMapper<MeterReading> {
}

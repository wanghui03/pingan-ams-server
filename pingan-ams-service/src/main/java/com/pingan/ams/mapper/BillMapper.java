package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.Bill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单 Mapper
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {
}

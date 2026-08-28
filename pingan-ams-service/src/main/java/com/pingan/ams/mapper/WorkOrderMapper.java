package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单 Mapper
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {
}

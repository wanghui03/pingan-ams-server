package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.MeterReadingDTO;
import com.pingan.ams.model.entity.MeterReading;
import com.pingan.ams.model.vo.MeterReadingVO;

/**
 * 抄表记录 Service
 */
public interface MeterReadingService extends IService<MeterReading> {

    /**
     * 新增抄表记录
     * @param tenantId 租户ID
     * @param dto 抄表信息
     * @return 记录ID
     */
    Long addMeterReading(Long tenantId, MeterReadingDTO dto);

    /**
     * 获取抄表记录详情
     * @param tenantId 租户ID
     * @param id 记录ID
     * @return 详情
     */
    MeterReadingVO getMeterReadingDetail(Long tenantId, Long id);

    /**
     * 分页查询抄表记录
     * @param tenantId 租户ID
     * @param page 页码
     * @param size 每页数量
     * @param roomId 房间ID
     * @param meterType 抄表类型
     * @return 分页结果
     */
    Page<MeterReadingVO> listMeterReadings(Long tenantId, Integer page, Integer size, Long roomId, Integer meterType);

    /**
     * 为抄表记录生成账单
     * @param tenantId 租户ID
     * @param id 记录ID
     * @return 账单ID
     */
    Long generateBill(Long tenantId, Long id);
}

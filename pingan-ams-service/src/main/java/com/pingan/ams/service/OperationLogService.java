package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.entity.OperationLog;
import com.pingan.ams.model.vo.OperationLogVO;

/**
 * 操作日志 Service
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 记录操作日志
     */
    void log(OperationLog log);

    /**
     * 分页查询操作日志
     */
    Page<OperationLogVO> listLogs(Integer page, Integer size, String username, String module, Integer status);

    /**
     * 清空日志
     */
    void clearLogs();
}

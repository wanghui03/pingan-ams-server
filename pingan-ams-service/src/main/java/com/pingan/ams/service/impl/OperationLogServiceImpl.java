package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.mapper.OperationLogMapper;
import com.pingan.ams.mapper.TenantMapper;
import com.pingan.ams.model.entity.OperationLog;
import com.pingan.ams.model.entity.Tenant;
import com.pingan.ams.model.vo.OperationLogVO;
import com.pingan.ams.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    private final TenantMapper tenantMapper;

    @Async
    @Override
    public void log(OperationLog operationLog) {
        try {
            this.save(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }

    @Override
    public Page<OperationLogVO> listLogs(Integer page, Integer size, String username, String module, Integer status) {
        Page<OperationLog> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(OperationLog::getUsername, username);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (status != null) {
            wrapper.eq(OperationLog::getStatus, status);
        }

        wrapper.orderByDesc(OperationLog::getOperateTime);

        Page<OperationLog> logPage = this.page(pageParam, wrapper);

        Page<OperationLogVO> voPage = new Page<>(logPage.getCurrent(), logPage.getSize(), logPage.getTotal());
        List<OperationLogVO> voList = logPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public void clearLogs() {
        this.remove(new LambdaQueryWrapper<>());
    }

    private OperationLogVO convertToVO(OperationLog operationLog) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(operationLog, vo);

        if (operationLog.getTenantId() != null) {
            Tenant tenant = tenantMapper.selectById(operationLog.getTenantId());
            if (tenant != null) {
                vo.setTenantName(tenant.getName());
            }
        }

        vo.setStatusDesc(operationLog.getStatus() != null && operationLog.getStatus() == 1 ? "成功" : "失败");

        return vo;
    }
}

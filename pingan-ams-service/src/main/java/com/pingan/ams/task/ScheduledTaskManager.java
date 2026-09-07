package com.pingan.ams.task;

import com.pingan.ams.service.BillService;
import com.pingan.ams.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务调度器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTaskManager {

    private final ContractService contractService;
    private final BillService billService;

    /**
     * 每天凌晨0点执行：处理到期合同
     * 将 endDate < today 的生效中合同标记为已到期
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processExpiredContracts() {
        log.info("开始执行定时任务：处理到期合同");
        try {
            contractService.processExpiredContracts();
            log.info("定时任务完成：处理到期合同");
        } catch (Exception e) {
            log.error("定时任务执行失败：处理到期合同", e);
        }
    }

    /**
     * 每天凌晨1点执行：自动生成账单
     * 根据合同的支付方式，在账单日期当天自动生成租金账单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoGenerateBills() {
        log.info("开始执行定时任务：自动生成账单");
        try {
            billService.autoGenerateBills();
            log.info("定时任务完成：自动生成账单");
        } catch (Exception e) {
            log.error("定时任务执行失败：自动生成账单", e);
        }
    }

    /**
     * 每天凌晨2点执行：处理逾期账单
     * 将 dueDate < today 的待支付账单标记为已逾期
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void processOverdueBills() {
        log.info("开始执行定时任务：处理逾期账单");
        try {
            billService.processOverdueBills();
            log.info("定时任务完成：处理逾期账单");
        } catch (Exception e) {
            log.error("定时任务执行失败：处理逾期账单", e);
        }
    }

    /**
     * 每天上午9点执行：发送账单到期提醒
     * 提前3天提醒待支付账单的租客
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendBillDueReminders() {
        log.info("开始执行定时任务：发送账单到期提醒");
        try {
            billService.sendBillDueReminders();
            log.info("定时任务完成：发送账单到期提醒");
        } catch (Exception e) {
            log.error("定时任务执行失败：发送账单到期提醒", e);
        }
    }

    /**
     * 每天上午10点执行：发送合同到期提醒
     * 提前30天提醒生效中合同的租客
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendContractExpiryReminders() {
        log.info("开始执行定时任务：发送合同到期提醒");
        try {
            contractService.sendContractExpiryReminders();
            log.info("定时任务完成：发送合同到期提醒");
        } catch (Exception e) {
            log.error("定时任务执行失败：发送合同到期提醒", e);
        }
    }
}

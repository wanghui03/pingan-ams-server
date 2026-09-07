package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.TenantCertificateDTO;
import com.pingan.ams.model.entity.TenantCertificate;
import com.pingan.ams.model.vo.TenantCertificateVO;

/**
 * 租户证件 Service
 */
public interface TenantCertificateService extends IService<TenantCertificate> {

    /**
     * 保存或更新租户证件
     */
    Long saveOrUpdateCertificate(Long tenantId, TenantCertificateDTO dto);

    /**
     * 获取租户证件信息
     */
    TenantCertificateVO getCertificate(Long tenantId);

    /**
     * 删除租户证件
     */
    void deleteCertificate(Long tenantId);
}

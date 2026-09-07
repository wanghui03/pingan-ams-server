package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.CertificateDTO;
import com.pingan.ams.model.entity.Certificate;
import com.pingan.ams.model.vo.CertificateVO;

/**
 * 证件 Service
 */
public interface CertificateService extends IService<Certificate> {

    /**
     * 保存或更新证件
     */
    Long saveOrUpdateCertificate(CertificateDTO dto);

    /**
     * 获取证件信息
     */
    CertificateVO getCertificate(Integer ownerType, Long ownerId);

    /**
     * 删除证件
     */
    void deleteCertificate(Integer ownerType, Long ownerId);
}

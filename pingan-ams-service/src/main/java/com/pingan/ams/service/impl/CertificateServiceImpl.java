package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.CertificateMapper;
import com.pingan.ams.model.dto.CertificateDTO;
import com.pingan.ams.model.entity.Certificate;
import com.pingan.ams.model.vo.CertificateVO;
import com.pingan.ams.service.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 证件 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements CertificateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateCertificate(CertificateDTO dto) {
        // 查询是否已有证件
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Certificate::getOwnerType, dto.getOwnerType())
                .eq(Certificate::getOwnerId, dto.getOwnerId());
        Certificate existing = this.getOne(wrapper);

        Certificate certificate;
        if (existing != null) {
            certificate = existing;
        } else {
            certificate = new Certificate();
            certificate.setOwnerType(dto.getOwnerType());
            certificate.setOwnerId(dto.getOwnerId());
        }

        // 设置证件信息
        BeanUtils.copyProperties(dto, certificate);

        this.saveOrUpdate(certificate);
        return certificate.getId();
    }

    @Override
    public CertificateVO getCertificate(Integer ownerType, Long ownerId) {
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Certificate::getOwnerType, ownerType)
                .eq(Certificate::getOwnerId, ownerId);
        Certificate certificate = this.getOne(wrapper);

        if (certificate == null) {
            return null;
        }

        return convertToVO(certificate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCertificate(Integer ownerType, Long ownerId) {
        LambdaQueryWrapper<Certificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Certificate::getOwnerType, ownerType)
                .eq(Certificate::getOwnerId, ownerId);
        this.remove(wrapper);
    }

    private CertificateVO convertToVO(Certificate certificate) {
        CertificateVO vo = new CertificateVO();
        BeanUtils.copyProperties(certificate, vo);

        // 设置所有者类型描述
        if (certificate.getOwnerType() == 1) {
            vo.setOwnerTypeDesc("租户");
        } else if (certificate.getOwnerType() == 2) {
            vo.setOwnerTypeDesc("员工");
        } else if (certificate.getOwnerType() == 3) {
            vo.setOwnerTypeDesc("租客");
        }

        // 设置证件类型描述
        if (certificate.getCertType() == 1) {
            vo.setCertTypeDesc("营业执照");
        } else if (certificate.getCertType() == 2) {
            vo.setCertTypeDesc("身份证");
        } else if (certificate.getCertType() == 3) {
            vo.setCertTypeDesc("护照");
        } else {
            vo.setCertTypeDesc("其他");
        }

        return vo;
    }
}

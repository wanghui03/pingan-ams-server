package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.TenantCertificateMapper;
import com.pingan.ams.model.dto.TenantCertificateDTO;
import com.pingan.ams.model.entity.TenantCertificate;
import com.pingan.ams.model.vo.TenantCertificateVO;
import com.pingan.ams.service.TenantCertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户证件 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantCertificateServiceImpl extends ServiceImpl<TenantCertificateMapper, TenantCertificate> implements TenantCertificateService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateCertificate(Long tenantId, TenantCertificateDTO dto) {
        // 查询是否已有证件
        LambdaQueryWrapper<TenantCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenantCertificate::getTenantId, tenantId);
        TenantCertificate existing = this.getOne(wrapper);

        TenantCertificate certificate;
        if (existing != null) {
            certificate = existing;
        } else {
            certificate = new TenantCertificate();
            certificate.setTenantId(tenantId);
        }

        // 设置证件信息
        certificate.setCertType(dto.getCertType());
        certificate.setCertImage(dto.getCertImage());

        // 根据证件类型设置不同字段
        if (dto.getCertType() == 1) {
            // 营业执照
            certificate.setCompanyName(dto.getCompanyName());
            certificate.setCreditCode(dto.getCreditCode());
            certificate.setIdCardName(null);
            certificate.setIdCardNo(null);
        } else if (dto.getCertType() == 2) {
            // 身份证
            certificate.setIdCardName(dto.getIdCardName());
            certificate.setIdCardNo(dto.getIdCardNo());
            certificate.setCompanyName(null);
            certificate.setCreditCode(null);
        }

        this.saveOrUpdate(certificate);
        return certificate.getId();
    }

    @Override
    public TenantCertificateVO getCertificate(Long tenantId) {
        LambdaQueryWrapper<TenantCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenantCertificate::getTenantId, tenantId);
        TenantCertificate certificate = this.getOne(wrapper);

        if (certificate == null) {
            return null;
        }

        return convertToVO(certificate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCertificate(Long tenantId) {
        LambdaQueryWrapper<TenantCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TenantCertificate::getTenantId, tenantId);
        this.remove(wrapper);
    }

    private TenantCertificateVO convertToVO(TenantCertificate certificate) {
        TenantCertificateVO vo = new TenantCertificateVO();
        BeanUtils.copyProperties(certificate, vo);

        // 设置证件类型描述
        if (certificate.getCertType() == 1) {
            vo.setCertTypeDesc("营业执照");
        } else if (certificate.getCertType() == 2) {
            vo.setCertTypeDesc("身份证");
        }

        return vo;
    }
}

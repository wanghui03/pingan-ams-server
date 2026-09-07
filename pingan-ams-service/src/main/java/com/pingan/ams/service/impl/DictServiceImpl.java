package com.pingan.ams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.ams.common.exception.BusinessException;
import com.pingan.ams.common.result.ResultCode;
import com.pingan.ams.mapper.DictMapper;
import com.pingan.ams.model.dto.DictDTO;
import com.pingan.ams.model.entity.Dict;
import com.pingan.ams.model.vo.DictVO;
import com.pingan.ams.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public Long createDict(DictDTO dto) {
        // 检查字典类型和编码是否已存在
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictType, dto.getDictType())
                .eq(Dict::getDictCode, dto.getDictCode());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("字典类型和编码已存在");
        }

        Dict dict = new Dict();
        BeanUtils.copyProperties(dto, dict);
        if (dict.getStatus() == null) {
            dict.setStatus(1);
        }
        if (dict.getSortOrder() == null) {
            dict.setSortOrder(0);
        }

        this.save(dict);
        return dict.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public void updateDict(Long id, DictDTO dto) {
        Dict dict = this.getById(id);
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查字典类型和编码是否已被其他记录使用
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictType, dto.getDictType())
                .eq(Dict::getDictCode, dto.getDictCode())
                .ne(Dict::getId, id);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("字典类型和编码已存在");
        }

        BeanUtils.copyProperties(dto, dict);
        this.updateById(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", allEntries = true)
    public void deleteDict(Long id) {
        Dict dict = this.getById(id);
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        this.removeById(id);
    }

    @Override
    public DictVO getDictDetail(Long id) {
        Dict dict = this.getById(id);
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToVO(dict);
    }

    @Override
    public Page<DictVO> listDicts(Integer page, Integer size, String dictType, String keyword) {
        Page<Dict> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        if (dictType != null && !dictType.isEmpty()) {
            wrapper.eq(Dict::getDictType, dictType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Dict::getDictName, keyword)
                    .or()
                    .like(Dict::getDictCode, keyword);
        }
        wrapper.orderByAsc(Dict::getDictType, Dict::getSortOrder);

        Page<Dict> dictPage = this.page(pageParam, wrapper);

        Page<DictVO> voPage = new Page<>(dictPage.getCurrent(), dictPage.getSize(), dictPage.getTotal());
        List<DictVO> voList = dictPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Cacheable(value = "dict", key = "#dictType")
    public List<DictVO> getDictsByType(String dictType) {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictType, dictType)
                .eq(Dict::getStatus, 1)
                .orderByAsc(Dict::getSortOrder);

        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public String getDictName(String dictType, String dictCode) {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictType, dictType)
                .eq(Dict::getDictCode, dictCode);
        Dict dict = this.getOne(wrapper);
        return dict != null ? dict.getDictName() : null;
    }

    @Override
    public List<String> getAllDictTypes() {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Dict::getDictType)
                .groupBy(Dict::getDictType)
                .orderByAsc(Dict::getDictType);

        return this.list(wrapper).stream()
                .map(Dict::getDictType)
                .distinct()
                .collect(Collectors.toList());
    }

    private DictVO convertToVO(Dict dict) {
        DictVO vo = new DictVO();
        BeanUtils.copyProperties(dict, vo);
        vo.setStatusDesc(dict.getStatus() == 1 ? "启用" : "禁用");
        return vo;
    }
}

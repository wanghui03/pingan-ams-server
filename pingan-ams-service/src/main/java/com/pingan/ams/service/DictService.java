package com.pingan.ams.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.ams.model.dto.DictDTO;
import com.pingan.ams.model.entity.Dict;
import com.pingan.ams.model.vo.DictVO;

import java.util.List;

/**
 * 字典 Service
 */
public interface DictService extends IService<Dict> {

    /**
     * 创建字典
     */
    Long createDict(DictDTO dto);

    /**
     * 更新字典
     */
    void updateDict(Long id, DictDTO dto);

    /**
     * 删除字典
     */
    void deleteDict(Long id);

    /**
     * 获取字典详情
     */
    DictVO getDictDetail(Long id);

    /**
     * 分页查询字典
     */
    Page<DictVO> listDicts(Integer page, Integer size, String dictType, String keyword);

    /**
     * 根据字典类型获取字典列表
     */
    List<DictVO> getDictsByType(String dictType);

    /**
     * 根据字典类型和编码获取字典名称
     */
    String getDictName(String dictType, String dictCode);

    /**
     * 获取所有字典类型
     */
    List<String> getAllDictTypes();
}

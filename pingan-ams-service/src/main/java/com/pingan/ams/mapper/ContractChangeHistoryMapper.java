package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.ContractChangeHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 合同变更历史 Mapper
 */
@Mapper
public interface ContractChangeHistoryMapper extends BaseMapper<ContractChangeHistory> {

    /**
     * 根据合同ID查询变更历史
     */
    @Select("SELECT * FROM ams_contract_change_history WHERE contract_id = #{contractId} ORDER BY create_time DESC")
    List<ContractChangeHistory> selectByContractId(@Param("contractId") Long contractId);

    /**
     * 根据变更ID查询变更历史
     */
    @Select("SELECT * FROM ams_contract_change_history WHERE change_id = #{changeId} ORDER BY create_time DESC")
    List<ContractChangeHistory> selectByChangeId(@Param("changeId") Long changeId);
}

package com.pingan.ams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pingan.ams.model.entity.ContractChange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 合同变更申请 Mapper
 */
@Mapper
public interface ContractChangeMapper extends BaseMapper<ContractChange> {

    /**
     * 根据合同ID查询变更申请列表
     */
    @Select("SELECT * FROM ams_contract_change WHERE contract_id = #{contractId} AND deleted = 0 ORDER BY create_time DESC")
    List<ContractChange> selectByContractId(@Param("contractId") Long contractId);

    /**
     * 根据变更单号查询
     */
    @Select("SELECT * FROM ams_contract_change WHERE change_no = #{changeNo} AND deleted = 0")
    ContractChange selectByChangeNo(@Param("changeNo") String changeNo);
}

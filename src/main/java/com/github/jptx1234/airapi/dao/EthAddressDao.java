package com.github.jptx1234.airapi.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Transactional(rollbackFor=Exception.class)
public interface EthAddressDao {


	@Select("CALL proc_get_address(#{_parameter, jdbcType=VARCHAR})")
	public String getAddress(String coin);

	@Insert("INSERT INTO eths_used (address, coin) VALUES (#{address, jdbcType=VARCHAR}, #{coin, jdbcType=VARCHAR})")
	public void markAddressUsed(@Param("coin") String coin, @Param("address")String address);
}

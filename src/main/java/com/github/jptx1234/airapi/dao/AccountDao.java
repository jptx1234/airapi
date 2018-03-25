package com.github.jptx1234.airapi.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Transactional(rollbackFor=Exception.class)
public interface AccountDao {

	@Select("CALL proc_get_account(#{coin, jdbcType=VARCHAR}, #{flag, jdbcType=VARCHAR})")
	Map<String, String> queryAccoutByCoinAndFlag(@Param("coin")String coin, @Param("flag")String flag);

	@Insert("INSERT INTO airdrop_accounts (coin, email, pwd, flag) VALUES(#{coin, jdbcType=VARCHAR}, #{email, jdbcType=VARCHAR}, #{pwd, jdbcType=VARCHAR}, #{flag, jdbcType=VARCHAR})")
	void insert(@Param("email") String email,@Param("pwd") String password,@Param("coin") String coin,@Param("flag") String flag);

	@Select("SELECT flag FROM airdrop_accounts a WHERE a.`email` = #{email, jdbcType=VARCHAR} AND a.`coin` = #{coin, jdbcType=VARCHAR} LIMIT 1")
	String getFlag(@Param("email") String email, @Param("coin") String coin);

	@Update("UPDATE airdrop_accounts a SET a.`flag` = #{flag, jdbcType=VARCHAR} WHERE a.`email` = #{email, jdbcType=VARCHAR} AND a.`coin` = #{coin, jdbcType=VARCHAR}")
	void setFlag(@Param("email") String email, @Param("coin") String coin, @Param("flag") String flag);


}

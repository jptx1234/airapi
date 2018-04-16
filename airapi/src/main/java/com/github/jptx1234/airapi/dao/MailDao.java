package com.github.jptx1234.airapi.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Transactional(rollbackFor=Exception.class)
public interface MailDao {
	
	@Insert("INSERT INTO temp_mail (account, from_address, subject, content) VALUES (#{account, jdbcType=VARCHAR}, #{fromAddress, jdbcType=VARCHAR}, #{subject, jdbcType=VARCHAR}, #{content, typeHandler=org.apache.ibatis.type.BlobTypeHandler});")
	public void insertMail(@Param("account")String account,@Param("fromAddress")String fromAddress, @Param("subject")String subject, @Param("content")byte[] content);
	
	@Select("SELECT a.`id` AS id, a.`from_address` AS sender, DATE_FORMAT(a.`receive_time`, '%Y-%m-%d %H:%i:%S') AS receiveTime, a.`subject` AS subject  FROM temp_mail a WHERE a.`account` = #{account, jdbcType=VARCHAR} AND a.`status` = 1 ORDER BY a.`receive_time` DESC")
	public List<Map<String, String>> listMails(@Param("account")String account);
	
	@Select("SELECT a.`id` AS id, a.`from_address` AS sender, DATE_FORMAT(a.`receive_time`, '%Y-%m-%d %H:%i:%S') AS receiveTime, a.`subject` AS subject  FROM temp_mail a WHERE a.`account` = #{account, jdbcType=VARCHAR} AND INSTR(a.`from_address`, #{sender, jdbcType=VARCHAR}) > 0 AND a.`status` = 1 ORDER BY a.`receive_time` DESC")
	public List<Map<String, String>> listMailsBySender(@Param("account")String account, @Param("sender")String sender);

	@Select("SELECT a.`id` AS id, a.`from_address` AS sender, DATE_FORMAT(a.`receive_time`, '%Y-%m-%d %H:%i:%S') AS receiveTime, a.`subject` AS subject  FROM temp_mail a WHERE a.`account` = #{account, jdbcType=VARCHAR} AND INSTR(a.`subject`, #{subject, jdbcType=VARCHAR}) > 0 AND a.`status` = 1 ORDER BY a.`receive_time` DESC")
	public List<Map<String, String>> listMailsBySubject(@Param("account")String account, @Param("subject")String subject);
	
	@Select("SELECT a.`content` FROM temp_mail a WHERE a.`id` = #{id, jdbcType=BIGINT}")
	public Map<String, byte[]> getMailContent(@Param("id")Long id);

	@Update("UPDATE temp_mail a SET a.`status` = 0 WHERE a.`id` = #{id, jdbcType=BIGINT}")
	public long deleteMail(@Param("id")Long id);
	
	@Update("UPDATE temp_mail a SET a.`status` = 0 WHERE a.`account` = #{account, jdbcType=VARCHAR} AND a.`status` = 1")
	public long clearAccount(@Param("account")String account);
	
}
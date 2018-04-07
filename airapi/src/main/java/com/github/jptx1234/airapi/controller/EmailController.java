package com.github.jptx1234.airapi.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.jptx1234.airapi.dao.MailDao;
import com.github.jptx1234.airapi.utils.CompressUtils;
import com.github.jptx1234.airapi.utils.ResultJSON;

@RestController
@RequestMapping(value = "/email", produces = "application/json; charset=UTF-8")
public class EmailController {
	private final static Logger logger = LoggerFactory.getLogger(EmailController.class);

	@Autowired
	private MailDao mailDao;

	@RequestMapping(value = "/list")
	public JSONObject list(@RequestParam("account") String account) {
		logger.info("正在枚举"+account+"的邮件列表");
		ResultJSON resultJson = new ResultJSON();
		try {
			account = account.toLowerCase();
			List<Map<String,String>> listMails = mailDao.listMails(account);
			JSONArray dataArray = new JSONArray();
			if(listMails != null && !listMails.isEmpty()) {
				for (Map<String, String> mailMap : listMails) {
					dataArray.add(mailMap);
				}
			}
			resultJson.setDataObject(dataArray);
		} catch (Exception e) {
			logger.error("获取" + account + "账户的邮件列表出错", e);
			resultJson.setStatus(500).setMsg(e);
		}
		logger.info("枚举结果："+resultJson);

		return resultJson;
	}
	
	@RequestMapping("/findBySender")
	public JSONObject findBySender(@RequestParam("account")String account, @RequestParam("sender")String sender) {
		logger.info("正在查找"+account+"的邮件列表里发送者为"+sender+"的邮件列表");
		ResultJSON resultJson = new ResultJSON();
		try {
			account = account.toLowerCase();
			sender = sender.toLowerCase();
			List<Map<String,String>> listMailsBySender = mailDao.listMailsBySender(account, sender);
			JSONArray dataArray = new JSONArray();
			if(listMailsBySender != null && !listMailsBySender.isEmpty()) {
				for (Map<String, String> mailMap : listMailsBySender) {
					dataArray.add(mailMap);
				}
			}
			resultJson.setDataObject(dataArray);
		} catch (Exception e) {
			logger.error("获取" + account + "账户的邮件列表，根据发送者"+sender+"查询出错", e);
			resultJson.setStatus(500).setMsg(e);
		}
		logger.info("查找结果："+resultJson);

		return resultJson;
	}
	
	
	@RequestMapping("/findBySubject")
	public JSONObject findBySubject(@RequestParam("account")String account, @RequestParam("subject")String subject) {
		logger.info("正在查找"+account+"的邮件列表里标题为"+subject+"的邮件列表");
		ResultJSON resultJson = new ResultJSON();
		try {
			account = account.toLowerCase();
			List<Map<String,String>> listMailsBySubject = mailDao.listMailsBySubject(account, subject);
			JSONArray dataArray = new JSONArray();
			if(listMailsBySubject != null && !listMailsBySubject.isEmpty()) {
				for (Map<String, String> mailMap : listMailsBySubject) {
					dataArray.add(mailMap);
				}
			}
			resultJson.setDataObject(dataArray);
		} catch (Exception e) {
			logger.error("获取" + account + "账户的邮件列表，根据邮件主题"+subject+"查询出错", e);
			resultJson.setStatus(500).setMsg(e);
		}
		logger.info("枚举结果："+resultJson);
		
		return resultJson;
	}
	
	@RequestMapping("/getContent")
	public JSONObject getContent(@RequestParam("id")Long id) {
		logger.info("正在获取"+id+"的邮件内容");
		ResultJSON resultJSON = new ResultJSON();
		try {
			Map<String, byte[]> resultMap = mailDao.getMailContent(id);
			byte[] mailContentBytes = resultMap.get("content");
			String decompress = CompressUtils.decompress(mailContentBytes);
			resultJSON.putData("content", decompress);
		} catch (Exception e) {
			logger.error("获取id为"+id+"的邮件内容失败", e);
			resultJSON.setStatus(500).setMsg(e);
		}
		logger.info("获取到的结果为："+resultJSON);
		
		return resultJSON;
	}
	
	@RequestMapping("/delete")
	public JSONObject deleteMail(@RequestParam("id")Long id) {
		logger.info("正在删除id为"+id+"的邮件");
		ResultJSON resultJSON = new ResultJSON();
		try {
			long rows = mailDao.deleteMail(id);
			resultJSON.putData("rows", rows);
		} catch (Exception e) {
			logger.error("删除id为"+id+"的邮件失败", e);
			resultJSON.setStatus(500).setMsg(e);
		}
		logger.info("返回的结果为："+resultJSON);
		
		return resultJSON;
	}
	
	@RequestMapping("/clearAccount")
	public JSONObject clearAccount(@RequestParam("account")String account) {
		logger.info("正在清空账户"+account+"的邮件");
		ResultJSON resultJSON = new ResultJSON();
		try {
			long rows = mailDao.clearAccount(account);
			resultJSON.putData("rows", rows);
		} catch (Exception e) {
			logger.error("清空账户"+account+"的邮件失败", e);
			resultJSON.setStatus(500).setMsg(e);
		}
		logger.info("返回的结果为："+resultJSON);
		
		return resultJSON;
	}

}

package com.github.jptx1234.airapi.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.jptx1234.airapi.dao.AccountDao;
import com.github.jptx1234.airapi.utils.ResultJSON;

@RestController
@RequestMapping(value = "/account", produces = "application/json; charset=UTF-8")
public class AccountController {
	private final static Logger logger = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private AccountDao accountDao;

	@RequestMapping(value = "/get")
	public JSONObject getAccountByCoinAndFlag(@RequestParam("coin") String coin,
			@RequestParam(value = "flag", required = false) String flag) {
		ResultJSON resultJson = new ResultJSON();
		try {
			Map<String, String> accountMapping = accountDao.queryAccoutByCoinAndFlag(coin, flag);
			if (accountMapping != null && !accountMapping.isEmpty()) {
				resultJson.putData("email", accountMapping.get("email"));
				resultJson.putData("password", accountMapping.get("pwd"));
				logger.info("获取到" + coin + "账户，邮箱" + accountMapping.get("email") + "，密码" + accountMapping.get("pwd"));
			} else {
				logger.info("未获取到" + coin + "账户");
			}
		} catch (Exception e) {
			logger.error("获取" + coin + "账户时出错", e);
			resultJson.setStatus(500).setMsg(e);
		}

		return resultJson;
	}

	@RequestMapping(value = "/add")
	public JSONObject addAccount(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("coin") String coin, @RequestParam(value = "flag", required = false) String flag) {
		ResultJSON resultJson = new ResultJSON();
		try {
			accountDao.insert(email, password, coin, flag);
			logger.info("已记录新的账户，邮箱：" + email + "，密码：" + password + "，币种：" + coin + "，标识：" + flag);
		} catch (Exception e) {
			logger.error("记录账户时出错，邮箱：" + email + "，密码：" + password + "，币种：" + coin + "，标识：" + flag, e);
			resultJson.setStatus(500).setMsg(e);
		}

		return resultJson;
	}
	
	@RequestMapping(value = "getFlag")
	public JSONObject getFlag(@RequestParam("email") String email, @RequestParam("coin") String coin) {
		ResultJSON resultJson = new ResultJSON();
		try {
			String flag = accountDao.getFlag(email, coin);
			if(flag == null) {
				flag = "";
			}
			resultJson.putData("flag", flag);
			logger.info("邮箱"+email+"，币种"+coin+"账户flag为"+flag);
		}catch (Exception e) {
			logger.error("查找邮箱"+email+"，币种"+coin+"的flag时出错", e);
			resultJson.setStatus(500).setMsg(e);
		}
		
		return resultJson;
	}
	
	@RequestMapping(value = "setFlag")
	public JSONObject setFlag(@RequestParam("email") String email, @RequestParam("coin") String coin, @RequestParam("flag") String flag) {
		ResultJSON resultJson = new ResultJSON();
		try {
			accountDao.setFlag(email, coin, flag);
			logger.info("已把"+email+"，币种"+coin+"的flag改为"+flag);
		}catch (Exception e) {
			logger.error("把"+email+"，币种"+coin+"的flag改为"+flag+"时出错", e);
			resultJson.setStatus(500).setMsg(e);
		}
		
		return resultJson;
	}
}

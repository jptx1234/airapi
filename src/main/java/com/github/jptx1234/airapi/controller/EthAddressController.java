package com.github.jptx1234.airapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.jptx1234.airapi.dao.EthAddressDao;
import com.github.jptx1234.airapi.utils.ResultJSON;

@RestController
@RequestMapping(value = "/ethAddr", produces = "application/json; charset=UTF-8")
public class EthAddressController {
	private final static Logger logger = LoggerFactory.getLogger(EthAddressController.class);

	@Autowired
	private EthAddressDao ethAddressDao;

	@RequestMapping(value = "/get")
	public JSONObject getAddr(@RequestParam("coin") String coin) {
		ResultJSON resultJson = new ResultJSON();
		try {
			String address = ethAddressDao.getAddress(coin);
			logger.info("获取到" + coin + "币的地址" + address);
			resultJson.putData("address", address);
		} catch (Exception e) {
			logger.error("获取" + coin + "币的地址出错", e);
			resultJson.setStatus(500).setMsg(e);
		}

		return resultJson;
	}

	@RequestMapping(value = "/markUsed")
	public JSONObject markAddrUsed(@RequestParam("coin") String coin, @RequestParam("address") String address) {
		ResultJSON resultJson = new ResultJSON();
		try {
			ethAddressDao.markAddressUsed(coin, address);
			logger.info("已把地址" + address + "标记为" + coin + "币已使用");
		} catch (Exception e) {
			logger.error("把地址" + address + "标记为" + coin + "币已用时出错", e);
			resultJson.setStatus(500).setMsg(e);
		}

		return resultJson;
	}
}

package com.github.jptx1234.airapi.utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ResultJSON extends JSONObject {

	private static final long serialVersionUID = 1L;

	private JSON dataObject;

	public ResultJSON() {
		this(null);
	}

	public ResultJSON(Map<String, Object> data) {
		setStatus(200);
		if (data == null) {
			dataObject = new JSONObject();
		} else {
			dataObject = new JSONObject(data);
		}
		put("data", dataObject);
	}

	public ResultJSON setStatus(int code) {
		put("status", code);
		if (code == 200) {
			put("msg", "success");
		}
		
		return this;
	}

	public ResultJSON setMsg(String msg) {
		put("msg", msg);
		
		return this;
	}
	
	public ResultJSON setMsg(Throwable e) {
		String exceptionMsg = e.getLocalizedMessage();
		if(StringUtils.isEmpty(exceptionMsg)) {
			exceptionMsg = e.getClass().getSimpleName();
		}
		
		return setMsg(exceptionMsg);
	}

	public JSON getDataObject() {
		return dataObject;
	}
	
	public void setDataObject(JSON data) {
		this.dataObject = data;
		this.put("data", data);
	}
	
	public ResultJSON putData(String key, Object value) {
		((JSONObject) dataObject).put(key, value);
		
		return this;
	}

}

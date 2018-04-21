package com.github.jptx1234.airapi.mail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dumbster.smtp.MailMessage;
import com.github.jptx1234.airapi.dao.MailDao;
import com.github.jptx1234.airapi.utils.CompressUtils;

public class MailDealingThread extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(MailDealingThread.class);

	private LinkedList<MailMessage> messageList;
	private boolean runFlag;
	private long sleepTime = 3L;

	@Autowired
	private MailDao mailDao;

	public MailDealingThread() {
		this.messageList = new LinkedList<>();
		this.runFlag = true;
	}

	public void setSleepTime(long time) {
		this.sleepTime = time;
	}

	public void addMessage(MailMessage message) {
		this.messageList.offer(message);
	}

	public void stopThread() {
		this.runFlag = false;
	}

	@Override
	public void run() {
		while (runFlag) {
			MailMessage message = this.messageList.pollFirst();
			if (message == null) {
				try {
					TimeUnit.SECONDS.sleep(sleepTime);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				doTask(message);
			} catch (Exception e) {
				logger.error("处理邮件时异常", e);
			}

		}
	}
	
	public void doTask(MailMessage message) {
		logger.info("开始处理邮件");
		String fromAddress = message.getFirstHeaderValue("From");
		if (fromAddress == null) {
			fromAddress = message.getFirstHeaderValue("Sender");
		}
		
		try {
			fromAddress = MimeUtility.decodeText(fromAddress);
		} catch (UnsupportedEncodingException e) {
			logger.error("收邮件时解析发件人编码未识别", e);
		}
		
		fromAddress = fromAddress.toLowerCase();
		
		logger.info("邮件发送者：" + fromAddress);
		
		String account = message.getFirstHeaderValue("To").toLowerCase();
		
		try {
			account = MimeUtility.decodeText(account);
		} catch (UnsupportedEncodingException e) {
			logger.error("收邮件时解析收件人编码未识别", e);
		}
		
		logger.info("邮件接收者：" + account);
		
		String subject = "";
		try {
			subject = MimeUtility.decodeText(message.getFirstHeaderValue("Subject"));
		} catch (UnsupportedEncodingException e) {
			logger.error("收邮件时解析邮件主题失败，编码未识别", e);
		}
		
		logger.info("邮件主题：" + subject);
		
		String content = getMessageContent(message);
		
		logger.info("邮件正文压缩前长度：" + content.length() / 1024.0 + "k");
		
		byte[] contentCompressed = CompressUtils.compress(content);
		
		logger.info("邮件正文压缩后长度：" + contentCompressed.length / 1024.0 + "k");
		
		logger.info("邮件正在入库");
		mailDao.insertMail(account, fromAddress, subject, contentCompressed);
		logger.info("邮件入库成功");
		
	}

	public String getMessageContent(MailMessage message) {
		String boundary = null;
		String contentType = message.getFirstHeaderValue("Content-Type");
		if(contentType == null) {
			contentType = message.getFirstHeaderValue("Content-type");
		}
		if (contentType != null) {
			String[] contentTypeValues = contentType.split(";");
			for (String contentTypeValue : contentTypeValues) {
				contentTypeValue = contentTypeValue.trim();
				if (contentTypeValue.startsWith("boundary=")) {
					boundary = contentTypeValue.replaceFirst("boundary=", "");
					if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
						boundary = boundary.substring(1, boundary.length() - 1);
					}
				}
			}
		}

		String body = message.getBody();
		
		if (boundary == null || body == null) {
			return Objects.toString(body, "");
		}
		
		List<String> bodies = new ArrayList<>();
		
		String[] bodyLines = body.split("\n");
		String boundaryBegin = "--" + boundary;
		String boundaryEnd = "--" + boundary + "--";
		StringBuilder bodyBuilder = new StringBuilder();
		boolean contentStart = false;
		for (String bodyLine : bodyLines) {
			if (boundaryBegin.equals(bodyLine)) {
				if(contentStart && bodyBuilder.length() > 0) {
					//把这一部分保存下来，开始第二部分
					bodies.add(bodyBuilder.toString());
					bodyBuilder = new StringBuilder();
				}
				contentStart = true;
				continue;
			}
			if (boundaryEnd.equals(bodyLine)) {
				break;
			}
			if (contentStart) {
				bodyBuilder.append(bodyLine).append("\n");
			}
		}
		if(bodyBuilder.length() > 0) {
			bodies.add(bodyBuilder.toString());
			bodyBuilder = new StringBuilder();
		}
		
		String finalBody = "";
		
		for (String bodyPart : bodies) {
			InputStream is = new ByteArrayInputStream(bodyPart.getBytes(StandardCharsets.UTF_8));
			try {
				MimeBodyPart mimeBody = new MimeBodyPart(is);
				Object content = mimeBody.getContent();
				String partContentType = mimeBody.getContentType();
				if(partContentType == null || partContentType.contains("text/plain")) {
					if("".equals(finalBody)) {
						finalBody = content == null ? "" : content.toString();
					}
				}else if(partContentType.contains("text/html")) {
					finalBody = content == null ? "" : content.toString();
				}
			} catch (Exception e) {
				logger.error("解析邮件正文出错", e);
			}
		}


		return finalBody;
	}

}

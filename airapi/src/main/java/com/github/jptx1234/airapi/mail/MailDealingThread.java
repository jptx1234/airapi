package com.github.jptx1234.airapi.mail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
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
		logger.info("邮件处理线程开始运行");
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

		String fromAddress = getSender(message);

		logger.info("邮件发送者：" + fromAddress);

		String account = getRecever(message);

		logger.info("邮件接收者：" + account);

		String subject = getSubject(message);

		logger.info("邮件主题：" + subject);

		String content = getMessageContent(message);

		logger.info("邮件正文压缩前长度：" + content.length() / 1024.0 + "k");

		byte[] contentCompressed = CompressUtils.compress(content);

		logger.info("邮件正文压缩后长度：" + contentCompressed.length / 1024.0 + "k");

		logger.info("邮件正在入库");
		mailDao.insertMail(account, fromAddress, subject, contentCompressed);
		logger.info("邮件入库成功");

	}

	/**
	 * 获取邮件发送者 RFC822：“From/Resent-From” : 与sender 必须至少存在一个。
	 * 
	 * @param message
	 * @return 发件人
	 */
	public String getSender(MailMessage message) {
		String fromAddress = message.getFirstHeaderValue("From");
		if (fromAddress == null) {
			fromAddress = message.getFirstHeaderValue("Sender");
		}
		if (fromAddress == null) {
			fromAddress = message.getFirstHeaderValue("Resent-From");
		}
		if (fromAddress == null) {
			fromAddress = message.getFirstHeaderValue("Resent-Sender");
		}

		if (fromAddress == null) {
			fromAddress = StringUtils.EMPTY;
		}

		try {
			fromAddress = MimeUtility.decodeText(fromAddress).toLowerCase();
		} catch (UnsupportedEncodingException e) {
			logger.error("收邮件时解析发件人编码未识别", e);
		}

		fromAddress = fromAddress.toLowerCase();

		return fromAddress;
	}

	/**
	 * 获取邮件接收者
	 * 
	 * @param message
	 * @return
	 */
	public String getRecever(MailMessage message) {
		String receiver = message.getFirstHeaderValue("To");
		if (receiver == null) {
			receiver = message.getFirstHeaderValue("Resent-To");
		}
		if (receiver == null) {
			receiver = StringUtils.EMPTY;
		}

		try {
			receiver = MimeUtility.decodeText(receiver).toLowerCase();
		} catch (UnsupportedEncodingException e) {
			logger.error("收邮件时解析收件人编码未识别", e);
		}

		return receiver;
	}

	/**
	 * 获取邮件主题
	 * 
	 * @param message
	 * @return
	 */
	public String getSubject(MailMessage message) {
		String subject = message.getFirstHeaderValue("Subject");

		if (subject == null) {
			subject = StringUtils.EMPTY;
		}

		try {
			subject = MimeUtility.decodeText(subject);
		} catch (UnsupportedEncodingException e) {
			logger.error("收邮件时解析邮件主题失败，编码未识别", e);
		}

		return subject;
	}

	/**
	 * 获取邮件正文
	 * 
	 * @param message
	 * @return 邮件正文
	 */
	public String getMessageContent(MailMessage message) {
		String boundary = null;// 正文不同部分的分割线
		String contentType = message.getFirstHeaderValue("Content-Type");
		if (contentType == null) {
			contentType = message.getFirstHeaderValue("Content-type");
		}
		if (contentType != null) {
			String[] contentTypeValues = contentType.split(";");
			for (String contentTypeValue : contentTypeValues) {
				contentTypeValue = contentTypeValue.trim();
				if (contentTypeValue.startsWith("boundary=")) {
					boundary = contentTypeValue.replaceFirst("boundary=", StringUtils.EMPTY);
					if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
						// 拿到分割线
						boundary = boundary.substring(1, boundary.length() - 1);
					}
				}
			}
		}

		// 邮件正文原文
		String body = message.getBody();

		// 对邮件正文解码
		String contentTransferEncoding = message.getFirstHeaderValue("Content-Transfer-Encoding");

		if (StringUtils.isNotBlank(contentTransferEncoding)) {
			try (InputStream bodyStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
					InputStream decodeStream = MimeUtility.decode(bodyStream, contentTransferEncoding);
					Scanner decodeScanner = new Scanner(decodeStream);) {

				decodeScanner.useDelimiter("\\A");
				body = decodeScanner.hasNext() ? decodeScanner.next() : StringUtils.EMPTY;
			} catch (Exception e) {
				logger.error("对邮件正文解码出错", e);
			}
		}

		// 如果没有分割，或者没body，就直接返回body
		if (boundary == null || body == null) {
			return Objects.toString(body, StringUtils.EMPTY);
		}

		// 邮件正文内容可能有很多部分，全部拆出来
		List<String> bodies = new ArrayList<>();

		String[] bodyLines = body.split(StringUtils.LF);
		String boundaryBegin = "--" + boundary;
		String boundaryEnd = "--" + boundary + "--";
		StringBuilder bodyBuilder = new StringBuilder();
		boolean contentStart = false;
		for (String bodyLine : bodyLines) {
			if (boundaryBegin.equals(bodyLine)) {
				if (contentStart && bodyBuilder.length() > 0) {
					// 把这一部分保存下来，开始第二部分
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
				bodyBuilder.append(bodyLine).append(StringUtils.LF);
			}
		}
		if (bodyBuilder.length() > 0) {
			bodies.add(bodyBuilder.toString());
			bodyBuilder = new StringBuilder();
		}

		String finalBody = StringUtils.EMPTY;

		// 决定最终的邮件正文：如果有text/html，就一定是text/html，如果没有，就选择text/plain
		for (String bodyPart : bodies) {
			try (InputStream is = new ByteArrayInputStream(bodyPart.getBytes(StandardCharsets.UTF_8));) {
				MimeBodyPart mimeBody = new MimeBodyPart(is);
				Object content = mimeBody.getContent();
				String partContentType = mimeBody.getContentType();
				if (partContentType == null || partContentType.contains("text/plain")) {
					if (StringUtils.isBlank(finalBody)) {
						finalBody = content == null ? StringUtils.EMPTY : content.toString();
					}
				} else if (partContentType.contains("text/html")) {
					finalBody = content == null ? StringUtils.EMPTY : content.toString();
				}
			} catch (Exception e) {
				logger.error("解析邮件正文出错", e);
			}
		}

		return finalBody;
	}

}

package com.github.jptx1234.airapi.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.MailStore;

public class AirapiMailStore implements MailStore {
	private final static Logger logger = LoggerFactory.getLogger(AirapiMailStore.class);
	
	@Autowired
	private MailDealingThread mailDealingThread;
	
	int emailCount = 0;
	
	
	@Override
	public int getEmailCount() {
		return emailCount;
	}

	@Override
	public void addMessage(MailMessage message) {
		logger.info("收到邮件");
		emailCount++;
		mailDealingThread.addMessage(message);
	}

	@Override
	public MailMessage[] getMessages() {
		return null;
	}

	@Override
	public MailMessage getMessage(int index) {
		return null;
	}

	@Override
	public void clearMessages() {
		emailCount = 0;
	}

}

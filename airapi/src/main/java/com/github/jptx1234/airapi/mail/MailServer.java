package com.github.jptx1234.airapi.mail;

import com.dumbster.smtp.MailStore;
import com.dumbster.smtp.ServerOptions;
import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.SmtpServerFactory;

public class MailServer {
	private SmtpServer server;
	private ServerOptions serverOptions;

	public MailServer() {
		this.serverOptions = new ServerOptions();
	}
	
	public void setMailStore(MailStore mailStore) {
		this.serverOptions.mailStore = mailStore;
	}
	
	public void startServer() {
		this.server = SmtpServerFactory.startServer(serverOptions);
	}
	
	public void stopServer() {
		this.server.stop();
	}
	
}

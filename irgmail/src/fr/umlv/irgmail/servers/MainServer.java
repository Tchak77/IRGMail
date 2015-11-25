package fr.umlv.irgmail.servers;

import io.vertx.core.Vertx;

import java.io.IOException;

import javax.mail.Folder;
import javax.mail.MessagingException;

import fr.umlv.irgmail.models.MailManager;

public class MainServer {
	private MailCollector collector;
	private MailManager manager;
	private ServerVerticle verticle = new ServerVerticle();
	
	public void start() throws IOException, MessagingException{
		Folder folder = Authenticator.authenticate();
		manager = new MailManager();
		manager.start(folder);
		collector = new MailCollector(manager);
		verticle.setCollector(collector);
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(verticle);
	}
}

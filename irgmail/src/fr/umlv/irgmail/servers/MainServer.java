package fr.umlv.irgmail.servers;

import io.vertx.core.Vertx;

import java.io.IOException;

import javax.mail.Folder;
import javax.mail.MessagingException;

public class MainServer {
		
	public void start() throws IOException, MessagingException {
		/*Get properties*/
		PropertiesHandler propertiesHandler = PropertiesHandler.getInstance();
		propertiesHandler.loadProperties();
		/*Get folder*/
		Folder inbox = propertiesHandler.getInbox();
		/*Start manager*/
		ContextHandler.getSingleton().startManager(inbox);
		/*Start vertx*/
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new ServerVerticle());
	}
}

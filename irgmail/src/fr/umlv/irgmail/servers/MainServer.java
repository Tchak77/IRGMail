package fr.umlv.irgmail.servers;

import io.vertx.core.Vertx;

import java.io.IOException;

import javax.mail.Folder;
import javax.mail.MessagingException;

public class MainServer {
		
	/**
	 * Starts the MainServer by starting the needed classed.
	 * It loads the properties, open the INBOX folder, starts 
	 * the manager on this folder and then run the Vertx server.
	 * @throws IOException if the load of properties failed.
	 * @throws MessagingException if getting the inbox folder failed.
	 */
	public void start() throws IOException, MessagingException {
		/*Get properties*/
		PropertiesHandler propertiesHandler = PropertiesHandler.getSingleton();
		propertiesHandler.loadProperties();
		/*Get folder*/
		Folder inbox = propertiesHandler.getInbox();
		/*Start manager*/
		ContextHandler.getInstance().startManager(inbox);
		/*Start vertx*/
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new ServerVerticle());
	}
}

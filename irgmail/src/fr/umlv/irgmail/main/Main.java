package fr.umlv.irgmail.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;

import fr.umlv.irgmail.MailCollector;
import fr.umlv.irgmail.Server;
import io.vertx.core.Vertx;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException, MessagingException {
		MailCollector collector = new MailCollector();
		collector.start();
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Server());
	}

}

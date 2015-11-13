package fr.umlv.irgmail.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.umlv.irgmail.Server;
import io.vertx.core.Vertx;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Server());
//		Server server = new Server();
//		server.collectMails();
	}

}

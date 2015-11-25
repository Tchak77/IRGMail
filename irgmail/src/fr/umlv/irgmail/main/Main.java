package fr.umlv.irgmail.main;

import java.io.IOException;

import javax.mail.MessagingException;

import fr.umlv.irgmail.servers.MainServer;

public class Main {

	public static void main(String[] args) throws IOException, MessagingException {
		MainServer server = new MainServer();
		server.start();	
	}

}

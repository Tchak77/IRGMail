package fr.umlv.irgmail.main;

import java.io.IOException;

import javax.mail.MessagingException;

import fr.umlv.irgmail.servers.ServerVerticle;

public class Main {

	public static void main(String[] args) throws MessagingException, IOException {
		ServerVerticle server = ServerVerticle.createVerticle();
		server.start();
	}
}

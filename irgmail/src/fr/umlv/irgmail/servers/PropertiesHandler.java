package fr.umlv.irgmail.servers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class PropertiesHandler {

	private final Properties properties;
	
	public PropertiesHandler() {
		properties = new Properties();
	}
	
	public void loadProperties() throws FileNotFoundException, IOException{
		try (InputStream input = new FileInputStream("config.properties")) {
			properties.load(input);
		}
	}
	
	public Folder getInbox() throws MessagingException{
		String user = properties.getProperty("user");
		String passwd = properties.getProperty("password");
		Store store = Session.getInstance(properties).getStore();
		store.connect(user, passwd);		
		return store.getFolder("INBOX");
	}
	
}

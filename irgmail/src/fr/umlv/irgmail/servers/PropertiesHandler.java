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

	private static PropertiesHandler HANDLER = new PropertiesHandler();
	
	private final Properties properties;
	
	public PropertiesHandler() {
		properties = new Properties();
	}
	
	public static PropertiesHandler getInstance(){
		return HANDLER;
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
		try{
			return store.getFolder("INBOX");
		}finally{
			store.close();
		}
	}

	public String getProtocol() {
		return properties.getProperty("mail.store.protocol");
	}
	
}

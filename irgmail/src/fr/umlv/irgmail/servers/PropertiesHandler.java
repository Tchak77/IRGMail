package fr.umlv.irgmail.servers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
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
	
	/**
	 * Loads properties form the "config.properties" file located at the root project.
	 * This method should be used at first on this class.
	 * @throws FileNotFoundException if the file doesn't exist.
	 * @throws IOException	if the opening failed.
	 */
	public void loadProperties() throws FileNotFoundException, IOException{
		try (InputStream input = new FileInputStream("config.properties")) {
			properties.load(input);
		}
	}
	
	/**
	 * Returns the INBOX folder of the distant mail server.
	 * It uses the properties loaded to connect to the store and to return the folder.
	 * @return the INBOX folded opened.
	 * @throws MessagingException if the connexion failed.
	 */
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

	/**
	 * Returns the protocol specified in the properties.
	 * @return the protocol name specified.
	 */
	public String getProtocol() {
		return properties.getProperty("mail.store.protocol").toUpperCase(Locale.ENGLISH);
	}
	
}

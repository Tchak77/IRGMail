package fr.umlv.irgmail;

import static java.util.stream.Collectors.joining;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class Server extends AbstractVerticle {

	private final HashMap<Integer, Mail> mailMap = new HashMap<>();
	private int nextID = 1;

	public void addAMail(Mail mail) {
		Objects.requireNonNull(mail);
		mailMap.put(nextID++, mail);
	}

	private void getAMail(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		String id = routingContext.request().getParam("id");
		int index;
		if (id == null || (index = Integer.parseInt(id)) < 0 || index >= mailMap.size()) {
			response.setStatusCode(404).end();
			return;
		}
		routingContext.response()
					.putHeader("content-type", "application/json")
					.end(mailMap.get(index)
							.mailToString());
	}

	private void getAllMails(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json")
				.end(mailMap.values()
						.stream()
						.map(Mail::headerToString)
						.collect(joining(", ", "[", "]")));
	}
	
	public void collectMails() throws FileNotFoundException, IOException{
		//recuperer et ajouter a la map
		Properties properties = new Properties();
		try(InputStream input = new FileInputStream("config.properties")){
			properties.load(input);
		}
	    Session session = Session.getInstance(properties);
	    Store store = null; 
	    Folder defaultFolder = null; 
	    Folder inbox = null; 
	    try {
	        //store = session.getStore(new URLName("pop3://" + POP_SERVER3));
	    	//store.connect(POP_ACCOUNT3, POP_PASSWORD3); 
	        store = session.getStore();
	        System.out.println("Swag");
	        store.connect(properties.getProperty("mail.imap.user"), properties.getProperty("password"));
	        inbox = store.getFolder("INBOX");
	        inbox.open(1);
	        Message message = inbox.getMessage(1);
	        System.out.println(message.getSubject());
	        //printMessages(inbox);
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    } finally { // Ne pas oublier de fermer tout ça ! 
	        try {
	            if (store != null && store.isConnected()) { 
	                store.close(); 
	            } 
	        } catch (MessagingException e) { 
	            e.printStackTrace(); 
	        } 
	    }
	}
		
	@Override
	public void start() throws FileNotFoundException, IOException {
		collectMails();
		Router router = Router.router(vertx);
		// route to JSON REST APIs
		router.get("/mails").handler(this::getAllMails);
		router.get("/mails/:id").handler(this::getAMail);
		// otherwise serve static pages
		router.route().handler(StaticHandler.create());
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

}

package fr.umlv.irgmail.servers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import javax.mail.Folder;
import javax.mail.MessagingException;

/**
 * This class provides a vert.x server.
 * It allows routing managing through handlers.
 */
public class ServerVerticle extends AbstractVerticle {
	
	/**
	 * The handler of the properties files.
	 */
	private final PropertiesHandler propertiesHandler;
	/**
	 * The handler of the requesting routingContexts.
	 */
	private final ContextHandler contextHandler;
	
	/**
	 * Constructs a ServerVerticle.
	 * @param propertiesHandler the handler in charge of getting properties.
	 * @param contextHandler the handler in charge of handling requests.
	 */
	private ServerVerticle(PropertiesHandler propertiesHandler, ContextHandler contextHandler){
		this.propertiesHandler = Objects.requireNonNull(propertiesHandler);
		this.contextHandler = Objects.requireNonNull(contextHandler);
	}
	
	/**
	 * Creates a ServerVerticle with the correct parameters needed.
	 * @return a new ServerVerticle.
	 * @throws FileNotFoundException if the properties files doesnt exists.
	 * @throws IOException if the properties loading failed.
	 */
	public static ServerVerticle createVerticle() throws FileNotFoundException, IOException{
		PropertiesHandler propertiesHandler = new PropertiesHandler();
		propertiesHandler.loadProperties();
		String protocol = propertiesHandler.getProtocol();
		ContextHandler contextHandler = new ContextHandler(protocol);
		return new ServerVerticle(propertiesHandler, contextHandler);
	}
	
	/**
	 * Starts the server with the start-up code defined.
	 */
	@Override
	public void start() throws MessagingException, IOException {
		Folder inbox = propertiesHandler.getInbox();
		contextHandler.startManager(inbox);
		Vertx vertx = Vertx.vertx();
		Router router = Router.router(vertx);
		// route to JSON REST APIs
		router.get("/mails/page/:page").handler(contextHandler::getAllMails);
		router.get("/mails/:id").handler(contextHandler::getAMail);
		router.get("/mails/search/:search").handler(contextHandler::searchMails);
		// otherwise serve static pages
		router.route().handler(StaticHandler.create());
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

}

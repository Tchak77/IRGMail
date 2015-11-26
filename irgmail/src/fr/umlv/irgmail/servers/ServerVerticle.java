package fr.umlv.irgmail.servers;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.IOException;

import javax.mail.MessagingException;

public class ServerVerticle extends AbstractVerticle {
			
	@Override
	public void start() throws MessagingException, IOException {
		Router router = Router.router(vertx);
		ContextHandler contextHandler = ContextHandler.getSingleton();
		// route to JSON REST APIs
		router.get("/mails/page/:page").handler(contextHandler::getAllMails);
		router.get("/mails/:id").handler(contextHandler::getAMail);
		router.get("/mails/search/:search").handler(contextHandler::searchMails);
		// otherwise serve static pages
		router.route().handler(StaticHandler.create());
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

}

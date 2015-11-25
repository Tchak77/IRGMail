package fr.umlv.irgmail.servers;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.IOException;
import java.util.Objects;

import javax.mail.MessagingException;

public class ServerVerticle extends AbstractVerticle {
		
	private MailCollector collector;
	
	public void setCollector(MailCollector collector){
		this.collector = Objects.requireNonNull(collector);
	}
	
	@Override
	public void start() throws MessagingException, IOException {
		Router router = Router.router(vertx);
		if(collector == null){
			throw new IllegalStateException("collector is null");
		}
		// route to JSON REST APIs
		router.get("/mails/page/:page").handler(collector::getAllMails);
		router.get("/mails/:id").handler(collector::getAMail);
		// otherwise serve static pages
		router.route().handler(StaticHandler.create());
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

}

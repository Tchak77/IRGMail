package fr.umlv.irgmail;

import static java.util.stream.Collectors.joining;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.IOException;

import javax.mail.MessagingException;

public class Server extends AbstractVerticle {

	private final MailBase base = MailBase.getInstance();

	private void getAMail(RoutingContext routingContext) {
		if(routingContext.request().localAddress().host() == "127.0.0.1"){
		HttpServerResponse response = routingContext.response();
		String id = routingContext.request().getParam("id");
		int index;
		if (id == null || (index = Integer.parseInt(id)) < 0
				|| index >= base.size()) {
			response.setStatusCode(404).end();
			return;
		}
		response.putHeader("content-type", "application/json").end(
				base.getAMail(index).mailToString());
		}
	}

	private void getAllMails(RoutingContext routingContext) {
		if(routingContext.request().localAddress().host() == "127.0.0.1"){
		routingContext
				.response()
				.putHeader("content-type", "application/json")
				.end(base.values().stream().map(Mail::headerToString)
						.collect(joining(", ", "[", "]")));
		}
	}

	@Override
	public void start(Future<Void> startFuture) throws MessagingException,
			IOException {
		Router router = Router.router(vertx);
		// route to JSON REST APIs
		router.get("/mails").handler(this::getAllMails);
		router.get("/mails/:id").handler(this::getAMail);
		// otherwise serve static pages
		router.route().handler(StaticHandler.create());
		vertx.createHttpServer()
			 .requestHandler(router::accept)
			 .listen(8080, res -> {
					if (res.succeeded()) {
						startFuture.complete();
					} else {
						startFuture.fail(res.cause());
					}
				});
	}

}

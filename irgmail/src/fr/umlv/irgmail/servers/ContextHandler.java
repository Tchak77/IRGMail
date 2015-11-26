package fr.umlv.irgmail.servers;

import static java.util.stream.Collectors.joining;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Folder;
import javax.mail.MessagingException;

import fr.umlv.irgmail.model.MailManager;
import fr.umlv.irgmail.plugins.Plugable;

public class ContextHandler {

	private static ContextHandler HANDLER = new ContextHandler();

	private final MailManager manager;
	private final ExecutorService executor;

	ContextHandler() {
		this.manager = new MailManager();
		executor = Executors.newFixedThreadPool(10);
	}

	public void startManager(Folder folder) {
		try {
			manager.startOnFolder(folder);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	static ContextHandler getSingleton() {
		return HANDLER;
	}

	private void handleIfFit(RoutingContext routingContext, Handler<RoutingContext> handler) {
		HttpServerResponse response = routingContext.response();
		if (!routingContext.request().localAddress().host().equals(routingContext.request().remoteAddress().host())) {
			response.setStatusCode(403).end();
			return;
		} else {
			handler.handle(routingContext);
		}
	}

	public void getAMail(RoutingContext routingContext) {
		executor.execute(() -> {
			handleIfFit(
					routingContext,
					(r) -> {
						HttpServerResponse response = r.response();
						String id = r.request().getParam("id");
						int index;
						if (id == null || (index = Integer.parseInt(id)) < 0) {
							response.setStatusCode(404).end();
							return;
						}
						try {
							response.putHeader("content-type", "application/json")
									.end(manager.mailToJSON(index));
						} catch (MessagingException | IOException e) {
							response.setStatusCode(503).end();
						}
					});
		});
	}

	public void getAllMails(RoutingContext routingContext) {
		executor.execute(() -> {
			handleIfFit(
					routingContext,
					(r) -> {
						HttpServerResponse response = r.response();
						String page = r.request().getParam("page");
						int page_index;
						if (page == null || (page_index = Integer.parseInt(page)) < 0) {
							response.setStatusCode(404).end();
							return;
						}
						try {
							response.putHeader("content-type", "application/json")
									.end(manager.headersByPage(page_index)
									.collect(joining(", ", "[", "]")));
						} catch (MessagingException e) {
							response.setStatusCode(503).end();
						}
					});
		});
	}

	public void searchMails(RoutingContext routingContext) {
		executor.execute(() -> {
			handleIfFit(
					routingContext,
					(r) -> {
						HttpServerResponse response = r.response();
						String search = r.request().getParam("search");
						if (search == null || search == "") {
							response.setStatusCode(404).end();
							return;
						}
						try {
							response.putHeader("content-type", "application/json")
									.end(manager.headersByKeywords(search)
									.collect(joining(", ", "[", "]")));
						} catch (MessagingException e) {
							response.setStatusCode(503).end();
						}
					});
		});
	}

}

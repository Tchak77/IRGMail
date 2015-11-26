package fr.umlv.irgmail.servers;

import static java.util.stream.Collectors.joining;
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

	private MailManager manager;
	private final ExecutorService executor;
	private final List<Plugable> plugins;

	ContextHandler() {
		this.manager = new MailManager();
		plugins = new ArrayList<Plugable>();
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

	public void getAMail(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		if (!routingContext.request().localAddress().host()
				.equals(routingContext.request().remoteAddress().host())) {
			response.setStatusCode(403).end();
			return;
		}
		String id = routingContext.request().getParam("id");
		int index;
		if (id == null || (index = Integer.parseInt(id)) < 0) {
			response.setStatusCode(404).end();
			return;
		}
		executor.execute(() -> {
			try {
				response.putHeader("content-type", "application/json").end(
						manager.mailToJSON(index));
			} catch (MessagingException | IOException e) {
				response.setStatusCode(503).end();
			}
		});
	}

	public void getAllMails(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		if (!routingContext.request().localAddress().host()
				.equals(routingContext.request().remoteAddress().host())) {
			response.setStatusCode(403).end();
			return;
		}
		String page = routingContext.request().getParam("page");
		int page_index;
		if (page == null || (page_index = Integer.parseInt(page)) < 0) {
			response.setStatusCode(404).end();
			return;
		}
		executor.execute(() -> {
			try {
				response.putHeader("content-type", "application/json").end(
						manager.headersByPage(page_index).collect(
								joining(", ", "[", "]")));
			} catch (Exception e) {
				response.setStatusCode(503).end();
			}
		});
	}

	public void searchMails(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		if (!routingContext.request().localAddress().host()
				.equals(routingContext.request().remoteAddress().host())) {
			response.setStatusCode(403).end();
			return;
		}
		String search = routingContext.request().getParam("search");
		if (search == null || search == "") {
			response.setStatusCode(404).end();
			return;
		}
		executor.execute(() -> {
			try {
				response.putHeader("content-type", "application/json").end(
						manager.headersByKeywords(search).collect(
								joining(", ", "[", "]")));
			} catch (Exception e) {
				response.setStatusCode(503).end();
			}
		});
	}

}

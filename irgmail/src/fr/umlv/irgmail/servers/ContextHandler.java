package fr.umlv.irgmail.servers;

import static java.util.stream.Collectors.joining;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.mail.Folder;
import javax.mail.MessagingException;

import fr.umlv.irgmail.model.MailManager;

public class ContextHandler {

	private static ContextHandler HANDLER = new ContextHandler();
	
	private MailManager manager;
	private final ExecutorService executor;
	private final HashMap<String, Consumer<RoutingContext>> handlers;

	public ContextHandler() {
		this.manager = new MailManager();
		executor = Executors.newFixedThreadPool(10);
		handlers = new HashMap<String, Consumer<RoutingContext>>();
	}
	
	public void startManager(Folder folder){
		try {
			manager.startOnFolder(folder);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	static ContextHandler getSingleton(){
		return HANDLER;
	}
	
	public void addHandler(String param, Consumer<RoutingContext> consumer) {
		Objects.requireNonNull(param);
		Objects.requireNonNull(consumer);
		handlers.put(param, consumer);
	}

	public void getAMail(RoutingContext routingContext) {
		handlers.getOrDefault(
				"id",
				(context) -> {
					HttpServerResponse response = context.response();
					if (!context.request().localAddress().host()
							.equals(context.request().remoteAddress().host())) {
						response.setStatusCode(403).end();
						return;
					}
					String id = context.request().getParam("id");
					int index;
					if (id == null || (index = Integer.parseInt(id)) < 0) {
						response.setStatusCode(404).end();
						return;
					}
					executor.execute(() -> {
							try {
								response.putHeader("content-type", "application/json")
										.end(manager.mailToJSON(index));
							} catch (MessagingException | IOException e) {
								response.setStatusCode(503).end();
							}
					});
				}).accept(routingContext);
	}

	public void getAllMails(RoutingContext routingContext) {
		handlers.getOrDefault(
				"page",
				(context) -> {
					HttpServerResponse response = context.response();
					if (!context.request().localAddress().host()
							.equals(context.request().remoteAddress().host())) {
						response.setStatusCode(403).end();
						return;
					}
					String page = context.request().getParam("page");
					int page_index;
					if (page == null
							|| (page_index = Integer.parseInt(page)) < 0) {
						response.setStatusCode(404).end();
						return;
					}
					executor.execute(() -> {
						try {
							response.putHeader("content-type",
									"application/json").end(
									manager.headers(page_index).collect(
											joining(", ", "[", "]")));
						} catch (Exception e) {
							response.setStatusCode(503).end();
						}
					});
				}).accept(routingContext);
	}

}

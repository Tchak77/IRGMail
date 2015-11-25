package fr.umlv.irgmail.servers;

import static java.util.stream.Collectors.joining;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.umlv.irgmail.models.MailManager;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class MailCollector {

	private MailManager manager;
	private final ExecutorService executor = Executors.newFixedThreadPool(10);

	public MailCollector(MailManager manager) {
		this.manager = Objects.requireNonNull(manager);
	}

	public void getAMail(RoutingContext routingContext) {
		if (routingContext.request().localAddress().host()
				.equals(routingContext.request().remoteAddress().host())) {
			HttpServerResponse response = routingContext.response();
			String id = routingContext.request().getParam("id");
			int index;
			if (id == null || (index = Integer.parseInt(id)) < 0) {
				response.setStatusCode(404).end();
				return;
			}
			executor.execute(() -> {
				response.putHeader("content-type", "application/json").end(
						manager.mailToJSON(index));
			});
		}
	}

	public void getAllMails(RoutingContext routingContext) {
		if (routingContext.request().localAddress().host()
				.equals(routingContext.request().remoteAddress().host())) {
			HttpServerResponse response = routingContext.response();
			String page = routingContext.request().getParam("page");
			int page_index;
			if (page == null || (page_index = Integer.parseInt(page)) < 0) {
				response.setStatusCode(404).end();
				return;
			}
			executor.execute(() -> {
				System.out.println("debut");
				routingContext
						.response()
						.putHeader("content-type", "application/json")
						.end(manager.headers(page_index).collect(
								joining(", ", "[", "]")));
				System.out.println("terminé");
			});
		}
	}

}

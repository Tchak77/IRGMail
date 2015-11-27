package fr.umlv.irgmail.servers;

import static java.util.stream.Collectors.joining;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Folder;
import javax.mail.MessagingException;

import fr.umlv.irgmail.model.MailManager;

/**
 * Handler that handles the requests in a {@link RoutingContext}
 *
 */
public class ContextHandler {

	/**
	 * The mail manager which carries the mails.
	 */
	private final MailManager manager;
	/**
	 * The service that runs threads.
	 */
	private final ExecutorService executor;

	/**
	 * Constructs a ContextHandler using a protocol name
	 * @param protocol the name of the protocol
	 */
	public ContextHandler(String protocol) {
		this.manager = new MailManager(protocol);
		executor = Executors.newFixedThreadPool(10);
	}

	/**
	 * Start the manager on the given Folder to retrieve mails.
	 * @param folder the folder to get information from.
	 * @throws MessagingException if the opening failed
	 */
	public void startManager(Folder folder) throws MessagingException {
			manager.startOnFolder(folder);
	}

	/**
	 * Applies the {@link Handler} to the {@link RoutingContext} if it fits the security
	 * @param routingContext the routingContext requesting.
	 * @param handler the handler that handles this request.
	 */
	private void handleIfFit(RoutingContext routingContext, Handler<RoutingContext> handler) {
		HttpServerResponse response = routingContext.response();
		if (!routingContext.request().localAddress().host().equals(routingContext.request().remoteAddress().host())) {
			response.setStatusCode(403).end();
			return;
		} else {
			handler.handle(routingContext);
		}
	}

	/**
	 * Handles a mail request.
	 * @param routingContext the routingContext requesting.
	 */
	public void getAMail(RoutingContext routingContext) {
		executor.execute(() -> { 
			handleIfFit( routingContext, (r) -> {
						HttpServerResponse response = r.response();
						String id = r.request().getParam("id");
						int index;
						if (id == null || (index = Integer.parseInt(id)) < 0) {
							response.setStatusCode(404).end();
							return;
						}
						answerWithHeader(response, index);
					});
		});
	}

	/**
	 * Put a header in a JSON format to the response.
	 * @param response the response to be told.
	 * @param index the index of the mail.
	 */
	private void answerWithHeader(HttpServerResponse response, int index) {
		try {
			response.putHeader("content-type", "application/json")
					.end(manager.mailToJSON(index));
		} catch (MessagingException | IOException e) {
			response.setStatusCode(503).end();
		}
	}

	/**
	 * Handles a few mails request.
	 * @param routingContext the routingContext requesting.
	 */
	public void getAllMails(RoutingContext routingContext) {
		executor.execute(() -> { handleIfFit(routingContext, (r) -> {
						HttpServerResponse response = r.response();
						String page = r.request().getParam("page");
						int page_index;
						if (page == null || (page_index = Integer.parseInt(page)) < 0) {
							response.setStatusCode(404).end();
							return;
						}
						answerWithHeaders(response, page_index);
					});
		});
	}

	/**
	 * Put the headers in a JSON format to the responses.
	 * @param response the response to be told.
	 * @param page_index the page index of the mails
	 */
	private void answerWithHeaders(HttpServerResponse response, int page_index) {
		try {
			response.putHeader("content-type", "application/json")
					.end(manager.headersByPage(page_index)
					.collect(joining(", ", "[", "]")));
		} catch (MessagingException e) {
			response.setStatusCode(503).end();
		}
	}

	/**
	 * Handles a mail search request.
	 * @param routingContext the routingContext requesting.
	 */
	public void searchMails(RoutingContext routingContext) {
		executor.execute(() -> { handleIfFit( routingContext, (r) -> {
						HttpServerResponse response = r.response();
						String search = r.request().getParam("search");
						if (search == null || search == "") {
							response.setStatusCode(404).end();
							return;
						}
						answerWithSearch(response, search);
					});
		});
	}

	/**
	 * Put the headers of the search in a JSON format to the response.
	 * @param response the response to be told.
	 * @param search the item of the search.
	 */
	private void answerWithSearch(HttpServerResponse response, String search) {
		try {
			response.putHeader("content-type", "application/json")
					.end(manager.headersByKeywords(search)
					.collect(joining(", ", "[", "]")));
		} catch (MessagingException e) {
			response.setStatusCode(503).end();
		}
	}
}

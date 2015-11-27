package fr.umlv.irgmail.model;

import static javax.mail.Flags.Flag.SEEN;
import static javax.mail.Folder.READ_WRITE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Implementation of a mail manager which can convert {@link Message} of a {@link Folder} to a JSON formatable Object.
 * This class supports concurrent modifications.
 *
 */
public class MailManager {

	private static final int PAGE_OFFSET = 10;

	/**
	 * The folder used to get the Messages from
	 */
	private Folder inbox;
	
	/**
	 * The protocol name used to manage mails
	 */
	private final String protocol;
	
	/**
	 * A local copy of the number of Messages in the folder
	 */
	private int mailsCounter;
	
	/**
	 * A Thread that will allows to get the updates of the distant folder
	 */
	private final Thread updater;
	
	/**
	 * List of Header fetched
	 */
	private ConcurrentHashMap<Integer, Header> headers;
	
	/**
	 * List of Content fetched
	 */
	private ConcurrentHashMap<Integer, Content> contents;

	/**
	 * Constructs a manager with the default parameters initialized.
	 * @param protocol the protocol's name.
	 */
	public MailManager(String protocol) {
		mailsCounter = 0;
		this.protocol = Objects.requireNonNull(protocol).toUpperCase();
		updater = new Thread(newUpdater());
		headers = new ConcurrentHashMap<Integer, Header>();
		contents = new ConcurrentHashMap<Integer, Content>();
	}
	
	/**
	 * Returns the updater which updates the maps.
	 * @return the runnable which updates.
	 */
	private Runnable newUpdater(){
		return () -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(10000);
					clearOnUpdate();
				} catch (InterruptedException | MessagingException e) {
					Thread.currentThread().interrupt();
				}
			}
		};
	}

	/**
	 * Clears the maps if the distant server was updated.
	 * @throws MessagingException if the information fetching failed.
	 */
	private void clearOnUpdate() throws MessagingException {
		if (inbox.getMessageCount() != mailsCounter) {
			mailsCounter = inbox.getMessageCount();
			headers.clear();
			contents.clear();
		}
	}

	/**
	 * Collects mails on a Header format depending on a page number 
	 * and add it to the map.
	 * @param page the page asked.
	 * @throws MessagingException if the mails fetching failed.
	 */
	private void collectHeadersByPage(int page) throws MessagingException {
		mailsCounter = inbox.getMessageCount();
		int end = mailsCounter - page * PAGE_OFFSET;
		int start = Math.max(1, mailsCounter - (page + 1) * PAGE_OFFSET);
		Message[] messages = inbox.getMessages(start, end);
		for (int i = 0; i < messages.length; i++) {
			int id = messages[i].getMessageNumber();
			headers.put(id, MessageParser.messageToHead(messages[i], protocol));
		}
	}

	/**
	 * Collects mails on a Header format depending on a search
	 * and add it to the map.
	 * @param page the page asked.
	 * @throws MessagingException if the mails fetching failed.
	 */
	private void collectHeadersByKeyword(String... keywords)
			throws MessagingException {
		Message[] messages = inbox.getMessages();
		for (int i = 0; i < messages.length; i++) {
			Header header = MessageParser.messageToHead(messages[i], protocol);
			if (header.contains(keywords)) {
				int id = messages[i].getMessageNumber();
				headers.put(id, header);
			}
		}
	}

	/**
	 * Collects a mail on a Content format depending on an index 
	 * and add it to the map.
	 * @param page the page asked.
	 * @throws MessagingException if the mails fetching failed.
	 * @throws IOException if the attachments loading failed.
	 */
	private void collectContent(int index) throws MessagingException,
			IOException {
		Message message = inbox.getMessage(index);
		contents.put(index, MessageParser.messageToContent(message));
		if (!headers.get(index).getSeen()) {
			headers.get(index).setSeen();
			message.setFlag(SEEN, true);
		}
	}

	/**
	 * Returns a Stream of JSON formated mail.
	 * Loads the mails if they are not yet loaded.
	 * @param keywords a String array that contains the key words of the search.
	 * @return Stream of strings a Stream of JSON formated mail.
	 * @throws MessagingException if the distant request on Folder didn't work.
	 */
	public Stream<String> headersByKeywords(String... keywords) throws MessagingException {
		collectHeadersByKeyword(keywords);
		ArrayList<Header> list = new ArrayList<Header>();
		for (Header header : headers.values()) {
			if (header.contains(keywords)) {
				list.add(header);
			}
		}
		return list.stream().map(Header::toJSONString);
	}

	/**
	 * Returns a Stream of JSON formated mails.
 	 * Loads the mails if they are not yet loaded.
	 * @param page an int representing the page in the current Folder.
	 * @return Stream of strings a Stream of JSON formated mail.
	 * @throws MessagingException if the distant request on Folder didn't work.
	 */
	public Stream<String> headersByPage(int page) throws MessagingException {
		ArrayList<Header> tmp = new ArrayList<Header>();
		int start = Math.max(0, mailsCounter - (page + 1) * PAGE_OFFSET);
		int end = mailsCounter - page * PAGE_OFFSET;
		if (headers.get(start + 1) == null) {
			collectHeadersByPage(page);
		}
		for (int i = end; i > start; i--) {
			tmp.add(headers.get(i));
		}
		return tmp.stream().map(Header::toJSONString);
	}

	/**
	 * Returns a single mail to a JSON format.
	 * @param index the index of the mail in the current Folder.
	 * @return String a mail in a JSON format.
	 * @throws MessagingException if the distant request on Folder didn't work.
	 * @throws IOException if the attachments collecting failed.
	 */
	public String mailToJSON(int index) throws MessagingException, IOException {
		if (contents.get(index) == null) {
			collectContent(index);
		}
		return headers.get(index).toJSONString().replace("}", ",")
				+ contents.get(index).toJSONString().replace("{", "");
	}

	/**
	 * Starts the mail managing on a given Folder.
	 * This method should be called first after the constructor.
	 * @param folder a Folder to be opened and to retrieve from.
	 * @throws MessagingException if the folder opening failed.
	 */
	public void startOnFolder(Folder folder) throws MessagingException {
		inbox = Objects.requireNonNull(folder);
		inbox.open(READ_WRITE);
		updater.start();
	}
}

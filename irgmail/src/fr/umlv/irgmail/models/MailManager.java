package fr.umlv.irgmail.models;

import static javax.mail.Flags.Flag.SEEN;
import static javax.mail.Folder.READ_WRITE;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class MailManager {

	private static final int PAGE_OFFSET = 10;
	
	private Folder inbox;
	private int mailsCounter;
	private final ConcurrentHashMap<Integer, Header> headers = new ConcurrentHashMap<Integer, Header>();
	private final ConcurrentHashMap<Integer, Content> contents = new ConcurrentHashMap<Integer, Content>();

	private void collectHeadersByPage(int page) throws MessagingException {
		int nb = inbox.getMessageCount();
		int start = Math.max(1, nb-(page+1)*PAGE_OFFSET);
		int end = nb-page*PAGE_OFFSET;
		Message[] messages = inbox.getMessages(start, end);
		for (int i = 0; i < messages.length; i++) {
			int id = messages[i].getMessageNumber();
			headers.put(id, MessageParser.messageToHead(messages[i]));
		}
	}
	
	private void collectHeadersByWord(String keyword){
		
	}

	private void collectContent(int index) throws MessagingException, IOException {
		Message message = inbox.getMessage(index);
		contents.put(index, MessageParser.messageToContent(message));
		if(!headers.get(index).getSeen()){
			headers.get(index).setSeen();
			message.setFlag(SEEN, true);
		}
	}

	public Stream<String> headers(int page) {
		ArrayList<Header> tmp = new ArrayList<Header>();
		int nb, start, end = 0;
		try {
			nb = inbox.getMessageCount();
			start = Math.max(1, nb-(page+1)*PAGE_OFFSET);
			end = nb-page*PAGE_OFFSET;
			if(headers.get(start+1)==null){
				collectHeadersByPage(page);
			}
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		for(int i=end-1;i>=start;i--){
			tmp.add(headers.get(i));
		}
		return tmp.stream().map(Header::toJSONString);
	}

	public String mailToJSON(int index) {
		if (contents.get(index) == null) {
			try {
				collectContent(index);
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return headers.get(index).toJSONString().replace("}", ",")
				+ contents.get(index).toJSONString().replace("{", "");
	}
	
	public void start(Folder folder) throws MessagingException {
		inbox = Objects.requireNonNull(folder);
		inbox.open(READ_WRITE);
	}
}

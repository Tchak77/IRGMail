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

public class MailManager {

	private static final int PAGE_OFFSET = 10;
	
	private Folder inbox;
	private int mailsCounter;
	private ConcurrentHashMap<Integer, Header> headers;
	private ConcurrentHashMap<Integer, Content> contents;
	
	public MailManager() {
		mailsCounter = 0;
		headers = new ConcurrentHashMap<Integer, Header>();
		contents = new ConcurrentHashMap<Integer, Content>();
	}

	private void collectHeadersByPage(int page) throws MessagingException {
		mailsCounter = inbox.getMessageCount();
		int end = mailsCounter-page*PAGE_OFFSET;
		int start = Math.max(1, mailsCounter-(page+1)*PAGE_OFFSET);
		Message[] messages = inbox.getMessages(start, end);
		for (int i = 0; i < messages.length; i++) {
			int id = messages[i].getMessageNumber();
			headers.put(id, MessageParser.messageToHead(messages[i]));
		}
	}

	private void collectContent(int index) throws MessagingException, IOException {
		Message message = inbox.getMessage(index);
		contents.put(index, MessageParser.messageToContent(message));
		if(!headers.get(index).getSeen()){
			headers.get(index).setSeen();
			message.setFlag(SEEN, true);
		}
	}

	public Stream<String> headers(int page) throws MessagingException {
		ArrayList<Header> tmp = new ArrayList<Header>();
		int start, end = 0;
		start = Math.max(1, mailsCounter-(page+1)*PAGE_OFFSET);
		end = mailsCounter-page*PAGE_OFFSET;
		if(headers.get(start+1)==null){
			collectHeadersByPage(page);
		}
		for(int i=end-1;i>=start;i--){
			tmp.add(headers.get(i));
		}
		return tmp.stream().map(Header::toJSONString);
	}

	public String mailToJSON(int index) throws MessagingException, IOException {
		if (contents.get(index) == null) {
			collectContent(index);
		}
		return headers.get(index).toJSONString().replace("}", ",")
				+ contents.get(index).toJSONString().replace("{", "");
	}
	
	public void startOnFolder(Folder folder) throws MessagingException {
		inbox = Objects.requireNonNull(folder);
		inbox.open(READ_WRITE);
	}
}

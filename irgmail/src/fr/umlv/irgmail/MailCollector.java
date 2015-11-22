package fr.umlv.irgmail;

import static javax.mail.Folder.READ_ONLY;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

public class MailCollector {

	private Folder inbox;
	private final MailBase base = MailBase.getInstance();

	private void collectMails(int pageNumber) throws MessagingException,
			IOException {
		if (pageNumber < 0) {
			throw new IllegalArgumentException("page number < 0");
		}
		Message[] messages = inbox.getMessages();
		int start = pageNumber * 30;
		for (int i = start; i < messages.length && i < start+30; i++) {
			base.addAMail(messageToMail(messages[i]));
		}
	}

	private static Mail messageToMail(Message message)
			throws MessagingException, IOException {
		Address[] recipients = message.getAllRecipients();
		Address[] senders = message.getFrom();
		String from = Arrays.toString(senders);
		String to = Arrays.toString(recipients);
		String subject = message.getSubject();
		Date date = message.getReceivedDate();
		String body = bodyParser(message);
		return new Mail(from, to, date, subject, body);
	}

	private static String bodyParser(Part p) throws IOException,
			MessagingException {
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
			return s;
		}
		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = bodyParser(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = bodyParser(bp);
					if (s != null)
						return s;
				} else {
					return bodyParser(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = bodyParser(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}
		return "";
	}

	public void start() throws IOException, MessagingException {
		Properties properties = new Properties();
		try (InputStream input = new FileInputStream("config.properties")) {
			properties.load(input);
		}
		Session session = Session.getInstance(properties);
		Store store = session.getStore();
		store.connect(properties.getProperty("mail.imap.user"),
					  properties.getProperty("password"));
		inbox = store.getFolder("INBOX");
		inbox.open(READ_ONLY);
		collectMails(0); /* Default */
	}
}

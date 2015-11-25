package fr.umlv.irgmail.model;

import static javax.mail.Flags.Flag.SEEN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

class MessageParser {

	/**
	 * Parse the content of the mail and return the message content
	 * 
	 * @param p
	 *            Part of the message
	 * @param files
	 *            List witch will contains the attached files
	 * @param medias
	 *            List witch will containts the picture or other witch are
	 *            inside the message
	 * @return the content of the mail
	 * @throws IOException
	 * @throws MessagingException
	 */
	static String bodyParse(Part p, ArrayList<String> files,
			ArrayList<String> medias) throws IOException, MessagingException {
		if (p.isMimeType("text/*")) { // Contenu texte
			return (String) p.getContent();
		}
		if (p.isMimeType("multipart/alternative")) { // Contenu ensembliste
			return traitementMultipart(p, files, medias);
		} else if (p.isMimeType("multipart/mixed")) { // Mails avec pieces jointes
			return traitementPieceJointe(p, files, medias);
		} else if (p.isMimeType("multipart/*")) {
			return traitementImage(p, files, medias);
		}
		return "";
	}

	private static String traitementMultipart(Part p, ArrayList<String> files,
			ArrayList<String> medias) throws IOException, MessagingException {
		Multipart mp = (Multipart) p.getContent();
		String text = null;
		text = traitementPart(files, medias, mp, text);
		return text;
	}

	private static String traitementPart(ArrayList<String> files,
			ArrayList<String> medias, Multipart mp, String text)
			throws MessagingException, IOException {
		for (int i = 0; i < mp.getCount(); i++) {
			Part bp = mp.getBodyPart(i);
			if (bp.isMimeType("text/plain")) { // texte
				if (text == null)
					text = bodyParse(bp, files, medias);
				continue;
			} else if (bp.isMimeType("text/html")) { // html
				String s = bodyParse(bp, files, medias);
				if (s != null)
					return s;
			}
		}
		return text;
	}

	private static String traitementImage(Part p, ArrayList<String> files,
			ArrayList<String> medias) throws IOException, MessagingException,
			FileNotFoundException {
		Multipart mp = ((Multipart) p.getContent());
		String s = "";
		for (int i = 0; i < mp.getCount(); i++) {
			BodyPart bp = mp.getBodyPart(i);
			if (bp.isMimeType("multipart/*")) {
				s = bodyParse(bp, files, medias);
			} else { // traitement image
				createFichier(medias, bp, "medias");
			}
		}
		return s;
	}

	private static String traitementPieceJointe(Part p,
			ArrayList<String> files, ArrayList<String> medias)
			throws IOException, MessagingException, FileNotFoundException {
		Multipart mp = ((Multipart) p.getContent());
		String s = "";
		for (int i = 0; i < mp.getCount(); i++) {
			BodyPart bp = mp.getBodyPart(i);
			if (bp.isMimeType("multipart/*")) { // Partie texte
				s = bodyParse(bp, files, medias);
			} else { // Traitement piece jointe
				createFichier(files, bp, "piecesJointes");
			}
		}
		return s;
	}

	private static void createFichier(ArrayList<String> files, BodyPart bp,
			String path) throws MessagingException, FileNotFoundException,
			IOException {
		DataHandler dh = bp.getDataHandler();
		String fileName = bp.getFileName();
		File file = new File("./webroot/" + path + "/received_" + fileName);
		files.add(path + "/received_" + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		dh.writeTo(fos);
	}

	static Header messageToHead(Message message) throws MessagingException {
		String from = Arrays.toString(message.getFrom());
		String subject = message.getSubject();
		String date = message.getReceivedDate().toString();
		boolean seen = message.getFlags().contains(SEEN);
		int id = message.getMessageNumber();
		return new Header(id, from, subject, seen, date);
	}

	static Content messageToContent(Message message) throws MessagingException,
			IOException {
		ArrayList<String> files = new ArrayList<String>();
		ArrayList<String> medias = new ArrayList<String>();
		String to = Arrays.toString(message.getAllRecipients());
		String body = bodyParse(message, files, medias);
		return new Content(to, body, files, medias);
	}

}

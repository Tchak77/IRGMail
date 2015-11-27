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
	 * Parses the content of the mail and return the message content
	 * @param p Part of the message
	 * @param files a list witch will contains the attached files
	 * @param medias a list witch will containts the picture or other witch are inside the message
	 * @return the content of the mail
	 * @throws IOException if the attachment loading failed.
	 * @throws MessagingException if the information fetching failed.
	 */
	static String bodyParse(Part p, ArrayList<String> files, ArrayList<String> medias) throws IOException, MessagingException {
		if (p.isMimeType("text/*")) { // Contenu texte
			return (String) p.getContent();
		}
		return parseMultipart(p, files, medias); 
	}

	/**
	 * Parses a {@link Multipart} of a mail.
	 * @param p the {@link Part}
	 * @param files list of attachments
	 * @param medias list of medias
	 * @return the parsed String
	 * @throws IOException
	 * @throws MessagingException
	 * @throws FileNotFoundException
	 */
	private static String parseMultipart(Part p, ArrayList<String> files, ArrayList<String> medias)
			throws IOException, MessagingException, FileNotFoundException {
		Multipart mp = (Multipart)p.getContent(); //Safe cast, on sait que le contenu est un multipart
		if (p.isMimeType("multipart/alternative")) { // Contenu ensembliste
			return traitementPart(files, medias, mp, null);
		} else if (p.isMimeType("multipart/mixed")) { // Mails avec pieces jointes
			return traitementPieceJointe(mp, files, medias);
		} else if (p.isMimeType("multipart/*")) {
			return traitementImage(mp, files, medias);
		}
		return "";
	}

	private static String traitementPart(ArrayList<String> files, ArrayList<String> medias, Multipart mp, String text)
			throws MessagingException, IOException {
		for (int i = 0; i < mp.getCount(); i++) {
			Part bp = mp.getBodyPart(i);
			if (bp.isMimeType("text/plain")) { // texte
				if (text == null)
					text = bodyParse(bp, files, medias);
			} else if (bp.isMimeType("text/html")) { // html
				return bodyParse(bp, files, medias);
			}
		}
		return "";
	}

	private static String traitementImage(Multipart mp, ArrayList<String> files, ArrayList<String> medias) throws IOException, MessagingException, FileNotFoundException {
	
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

	private static String traitementPieceJointe(Multipart mp, ArrayList<String> files, ArrayList<String> medias) throws IOException, MessagingException, FileNotFoundException {

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

	private static void createFichier(ArrayList<String> files, BodyPart bp,String path) throws MessagingException, FileNotFoundException, IOException {
		DataHandler dh = bp.getDataHandler();
		String fileName = bp.getFileName();
		File file = new File("./webroot/" + path + "/received_" + fileName);
		files.add(path + "/received_" + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		dh.writeTo(fos);
		fos.close();
	}

	/**
	 * Converts a Message to a Header format.
	 * @param message the Message to convert.
	 * @param protocol the protocol used to get informations.
	 * @return a new Header.
	 * @throws MessagingException if the message's information loading failed.
	 */
	static Header messageToHead(Message message, String protocol) throws MessagingException {
		String from = Arrays.toString(message.getFrom());
		String subject = message.getSubject();
		String date = "";
		boolean seen = true;
		if(protocol.contains("IMAP")){
			date = message.getReceivedDate().toString();
			seen = message.getFlags().contains(SEEN);
		}
		int id = message.getMessageNumber();
		return new Header(id, from, subject, seen, date);
	}

	/**
	 * Converts a Message to a Content format.
	 * @param message the message to convert.
	 * @return a new Contant.
	 * @throws MessagingException if the message's information loading failed.
	 * @throws IOException if the attachments loading failed.
	 */
	static Content messageToContent(Message message) throws MessagingException,
			IOException {
		ArrayList<String> files = new ArrayList<String>();
		ArrayList<String> medias = new ArrayList<String>();
		String to = Arrays.toString(message.getAllRecipients());
		String body = bodyParse(message, files, medias);
		return new Content(to, body, files, medias);
	}

}

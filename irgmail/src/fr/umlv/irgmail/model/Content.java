package fr.umlv.irgmail.model;

import io.vertx.core.impl.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents the content of a mail.
 * It is composed of the recipients, the body and the attachments.
 */
class Content {

	/**
	 * Recipients.
	 */
	private final String to;
	
	/**
	 * Body.
	 */
	private final String body;
	
	/**
	 * List of medias' URL in the content.
	 */
	private final ArrayList<String> medias;
	
	/**
	 * List of attachments' URL in the content.
	 */
	private final ArrayList<String> files;

	/**
	 * Constructs a Content.
	 * @param to Recipients of the mail.
	 * @param body Content of the mail.
	 * @param files List of attachments' URL of the mail.
	 * @param medias List of medias' URL of the mail.
	 */
	Content(String to, String body, ArrayList<String> files, ArrayList<String> medias) {
		this.to = Objects.requireNonNull(to);
		this.body = Objects.requireNonNull(body);
		this.medias = Objects.requireNonNull(medias);
		this.files = Objects.requireNonNull(files);
	}

	/**
	 * Returns the Content in a JSON format.
	 * @return content in JSON format.
	 */
	String toJSONString() {
		return "{\n" 
				+ "\"to\": \"" + to + "\"," 
				+ "\"body\": \"" + bodySerializer() + "\"" 
				+ "\n}";
	}

	/**
	 * Returns the body of the Content serialized so that files and medias
	 * can be linked with URLs.
	 * @return the content serialized.
	 */
	private String bodySerializer() {
		String tmp = filesLinker(body);
		try {
			return StringEscapeUtils.escapeJava(tmp.replace('"', '\''));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Links the files and medias in the body.
	 * @param body the body without links.
	 * @return the body with files linked.
	 */
	private String filesLinker(String body) {
		StringBuilder builder = new StringBuilder();
		for (int nbMediasUsed = 0; nbMediasUsed < medias.size(); nbMediasUsed++) {
			body = body.replaceFirst("(src=\"cid)+[^\"]+(\")", "src=\"" + medias.get(nbMediasUsed) + "\"");
		}
		for (int nbFilesUsed = 0; nbFilesUsed < files.size(); nbFilesUsed++) {
			builder.append("<a href=\"" + files.get(nbFilesUsed) + "\">Piece jointe " + nbFilesUsed + "</a>");
		}
		body += builder.toString();
		return body;
	}
}

package fr.umlv.irgmail.model;

import io.vertx.core.impl.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Objects;

class Content {

	private final String to;
	private final String body;
	private final ArrayList<String> medias;
	private final ArrayList<String> files;

	Content(String to, String body, ArrayList<String> files, ArrayList<String> medias) {
		this.to = Objects.requireNonNull(to);
		this.body = Objects.requireNonNull(body);
		this.medias = Objects.requireNonNull(medias);
		this.files = Objects.requireNonNull(files);
	}

	String toJSONString() {
		return "{\n" 
				+ "\"to\": \"" + to + "\"," 
				+ "\"body\": \"" + bodySerializer() + "\"" 
				+ "\n}";
	}

	private String bodySerializer() {
		int nbMediasUsed, nbFilesUsed = 0;
		String tmp = body;
		StringBuilder builder = new StringBuilder();
		for (nbMediasUsed = 0; nbMediasUsed < medias.size(); nbMediasUsed++) {
			tmp = tmp.replaceFirst("(src=\"cid)+[^\"]+(\")", "src=\"" + medias.get(nbMediasUsed) + "\"");
		}
		for (nbFilesUsed = 0; nbFilesUsed < files.size(); nbFilesUsed++) {
			builder.append("<a href=\"" + files.get(nbFilesUsed) + "\">Piece jointe " + nbFilesUsed + "</a>");
		}
		tmp += builder.toString();
		try {
			return StringEscapeUtils.escapeJava(tmp.replace('"', '\''));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

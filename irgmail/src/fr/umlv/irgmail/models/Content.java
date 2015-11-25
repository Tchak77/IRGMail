package fr.umlv.irgmail.models;

import io.vertx.core.impl.StringEscapeUtils;

import java.util.ArrayList;

class Content {

	private final String to;
	private final String body;
	private final ArrayList<String> medias;
	private final ArrayList<String> files;
	
	Content(String to, String body, ArrayList<String> files, ArrayList<String> medias) {
		this.to = to;
		this.body = body;
		this.medias = medias;
		this.files = files;
	}
	
	String toJSONString(){
		return "{\n"
				+ "\"to\": \"" + to + "\","
				+ "\"body\": \"" + bodySerializer() + "\""
				+ "\n}";
	}

	private String bodySerializer() {
		int nbMediasUsed;
		int nbFilesUsed;
		String tmp = body;
		StringBuilder builder = new StringBuilder();
		for(nbMediasUsed = 0; nbMediasUsed < medias.size();nbMediasUsed++){
			tmp = tmp.replaceFirst("(src=\"cid)+[^\"]+(\")", "src=\"" + medias.get(nbMediasUsed) + "\"");
		}
/*		for (nbMediasUsed = medias.size() - 1; nbMediasUsed >= 0; nbMediasUsed--) {
			tmp = tmp.replaceFirst("(src=\"cid)+[^\"]+(\")", "src=\"" + medias.get(nbMediasUsed) + "\"");
		}
*/		for (nbFilesUsed = 0; nbFilesUsed < files.size(); nbFilesUsed++) {
			builder.append("<a href=\"" + files.get(nbFilesUsed) + "\">Piece jointe " + nbFilesUsed + "</a>");
		}
		System.out.println(builder.toString());
		tmp += builder.toString();
		try {
			return StringEscapeUtils.escapeJava(tmp.replace('"', '\''));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

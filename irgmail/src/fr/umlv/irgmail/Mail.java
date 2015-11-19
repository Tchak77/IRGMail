package fr.umlv.irgmail;

import java.util.Objects;

public class Mail {

	private final String from;
	private final String to;
	private final String subject;
	private final String body;

	public Mail(String from, String to, String subject, String body) {
		this.from = Objects.requireNonNull(from);
		this.to = Objects.requireNonNull(to);
		this.subject = Objects.requireNonNull(subject);
		this.body = Objects.requireNonNull(body);
	}

	public String headerToString() {
		return "{" 
				+ "\"subject\": \"" + subject + "\", " 
				+ "\"from\": \"" + from + "\"" 
				+ "}";
	}

	public String mailToString() {
		return "{" 
				+ "\"subject\": \"" + subject + "\", " 
				+ "\"from\": \"" + from.replace(">","§").replace("<", "<i>").replace("§", "</i>") + "\", " 
				+ "\"body\": \"" + body + "\"" 
				+ "}";
	}

}

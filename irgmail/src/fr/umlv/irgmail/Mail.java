package fr.umlv.irgmail;

import java.util.Date;
import java.util.Objects;

public class Mail {

	private final String from;
	private final String to;
	private final Date date;
	private final String subject;
	private final String body;

	public Mail(String from, String to, Date date, String subject, String body) {
		this.from = Objects.requireNonNull(from);
		this.to = Objects.requireNonNull(to);
		this.date = Objects.requireNonNull(date);
		this.subject = Objects.requireNonNull(subject);
		this.body = Objects.requireNonNull(body);
	}

	public String headerToString() {
		return "{" 
				+ "\"subject\": \"" + subject + "\", " 
				+ "\"from\": \"" + from + "\""
//				+ "\"date\": \"" + date.toString() + "\""
				+ "}";
	}

	public String mailToString() {
		String content = "{" 
				+ "\"subject\": \"" + subject + "\", " 
				+ "\"to\": \"" + to + "\", " 
				+ "\"date\": \"" + date.toString() + "\", "
				+ "\"from\": \"" + from.replace(">","�").replace("<", "<i>").replace("�", "</i>") + "\", " ;
				content += "\"body\": \"" + body.replace('"', '\''); 
				if(content.charAt(content.length()-1 ) == '\n'){
					return content.substring(0,content.length()-2)+"\"}";
				}
				return content+"\"}";
	}

}

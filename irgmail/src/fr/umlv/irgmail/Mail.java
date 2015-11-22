package fr.umlv.irgmail;

import java.util.Date;
import java.util.Objects;

public class Mail {

	private final String from;
	private final String to;
	private final Date date;
	private final String subject;
	private final String body;
	private boolean seen;

	public Mail(String from, String to, Date date, String subject, String body, boolean seen) {
		this.from = Objects.requireNonNull(from);
		this.to = Objects.requireNonNull(to);
		this.date = Objects.requireNonNull(date);
		this.subject = Objects.requireNonNull(subject);
		this.body = Objects.requireNonNull(body);
		this.seen = seen;
	}

	public String headerToString() {
		return "{" 
				+ "\"seen\": \"" + seen + "\","
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mail other = (Mail) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}
	
}

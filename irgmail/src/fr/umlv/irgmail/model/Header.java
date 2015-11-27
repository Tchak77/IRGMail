package fr.umlv.irgmail.model;

import java.util.Objects;

/**
 * Represents the header of a mail.
 * It is composed of an ID, sender(s) and a subject.
 * For IMAP mails, is it also composed with a date and a seen flag. 
 */
class Header {
	
	/**
	 * ID of the mail.
	 */
	private final int ID;
	/**
	 * Senders.
	 */
	private final String from;
	/**
	 * Subject.
	 */
	private final String subject;
	/**
	 * Received date. (For IMAP protocol)
	 */
	private final String date;
	/**
	 * Seen flag. (For IMAP protocol)
	 */
	private boolean seen;
	
	/**
	 * Constructs a Header.
	 * @param ID ID of the mail.
	 * @param from Senders.
	 * @param subject Subject.
	 * @param seen	Seen flag.
	 * @param date	Received date.
	 */
	Header(int ID, String from, String subject, boolean seen, String date) {
		this.ID = Objects.requireNonNull(ID);
		this.from = Objects.requireNonNull(from);
		this.subject = Objects.requireNonNull(subject);
		this.date = Objects.requireNonNull(date);
		this.seen = Objects.requireNonNull(seen);
	}
	
	/**
	 * Returns the Header in a JSON format.
	 * @return header in JSON format.
	 */
	String toJSONString(){
		return "{\n"
				+ "\"id\": \"" + ID + "\", "
				+ "\"from\": \"" + from + "\", "
				+ "\"subject\": \"" + subject + "\", "
				+ "\"date\": \"" + date + "\", "
				+ "\"seen\": \"" + seen + "\" "
				+"\n}";
	}
	
	/**
	 * Tells if the words are contained in the current Header.
	 * @param keywords the words of the search.
	 * @return True if the header contains the words given.
	 */
	boolean contains(String... keywords){
		for (String string : keywords) {
			if(!from.contains(string) && !subject.contains(string)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Set the seen flag to True.
	 */
	void setSeen(){
		this.seen = true;
	}

	/**
	 * Returns the seen flag.
	 * @return the seen flag.
	 */
	boolean getSeen() {
		return seen;
	}
}

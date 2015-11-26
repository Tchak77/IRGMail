package fr.umlv.irgmail.model;

import java.util.Objects;

class Header {
	
	private final int ID;
	private final String from;
	private final String subject;
	private final String date;
	private boolean seen;
	private final Object monitor = new Object();
	
	Header(int ID, String from, String subject, boolean seen, String date) {
		this.ID = Objects.requireNonNull(ID);
		this.from = Objects.requireNonNull(from);
		this.subject = Objects.requireNonNull(subject);
		this.date = Objects.requireNonNull(date);
		this.seen = Objects.requireNonNull(seen);
	}
	
	String toJSONString(){
		return "{\n"
				+ "\"id\": \"" + ID + "\", "
				+ "\"from\": \"" + from + "\", "
				+ "\"subject\": \"" + subject + "\", "
				+ "\"date\": \"" + date + "\", "
				+ "\"seen\": \"" + seen + "\" "
				+"\n}";
	}
	
	boolean contains(String... keywords){
		for (String string : keywords) {
			if(!from.contains(string) && !subject.contains(string)){
				return false;
			}
		}
		return true;
	}
	
	void setSeen(){
		synchronized (monitor) {
			this.seen = true;
		}
	}

	boolean getSeen() {
		return seen;
	}
}

package fr.umlv.irgmail.models;

class Header {
	
	private final int ID;
	private final String from;
	private final String subject;
	private final String date;
	private boolean seen;
	
	Header(int ID, String from, String subject, boolean seen, String date) {
		this.ID = ID;
		this.from = from;
		this.subject = subject;
		this.date = date;
		this.seen = seen;
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
	
	void setSeen(){
		this.seen = true;
	}

	boolean getSeen() {
		return seen;
	}
}

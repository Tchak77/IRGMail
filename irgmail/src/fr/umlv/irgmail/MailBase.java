package fr.umlv.irgmail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

class MailBase {

	private final static MailBase MAILBASE = new MailBase();
	
	private final HashMap<Integer, Mail> mailMap;
	private int nextID;
	
	private MailBase() {
		mailMap = new HashMap<>();
		nextID = 0;
	}
	
	public void addAMail(Mail mail) {
		Objects.requireNonNull(mail);
		mailMap.put(nextID++, mail);
	}
	
	public static MailBase getInstance(){
		return MAILBASE;
	}

	public int size() {
		return mailMap.size();
	}

	/*Mail non mutable donc ok*/
	public Mail getAMail(int index) {
		return mailMap.get(index);
	}

	public Collection<Mail> values() {
		return Collections.unmodifiableCollection(mailMap.values());
	}
	
}

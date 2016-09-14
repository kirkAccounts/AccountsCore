/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 23, 2016
 */
package com.ianmann.accounts;

import java.util.HashMap;

import com.ianmann.utils.security.Decriptions;
import com.ianmann.utils.security.Encriptions;

/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 23, 2016
 *
 */
public class Account {

	public String type;
	public String usernameEncripted;
	public String encriptedPassword;
	public HashMap<String, String> securityQuestionsEncripted = new HashMap<String, String>();
	
	public Account(String _type, String _username, String _password){
		this.type = _type;
		this.usernameEncripted = Encriptions.encript(_username);
		this.encriptedPassword = Encriptions.encript(_password);
	}
	
	public String getDecriptedUsername() {
		return Decriptions.decript(this.usernameEncripted);
	}
	
	public String getDecriptedPassword() {
		return Decriptions.decript(this.encriptedPassword);
	}
	
	public void encriptSecurityQuestions() {
		for (String question : this.securityQuestionsEncripted.keySet()) {
			this.securityQuestionsEncripted.put(Encriptions.encript(question), Encriptions.encript(this.securityQuestionsEncripted.get(question)));
			this.securityQuestionsEncripted.remove(question);
		}
	}
	
	public HashMap<String, String> getDecriptedSecurityQuestions() {
		HashMap<String, String> decriptedSecurityQuestions = new HashMap<String, String>();
		for (String question : this.securityQuestionsEncripted.keySet()) {
			decriptedSecurityQuestions.put(Decriptions.decript(question), Decriptions.decript(this.securityQuestionsEncripted.get(question)));
		}
		return decriptedSecurityQuestions;
	}
	
	public void addDecriptedSecurityQuestion(String question, String answer) {
		this.securityQuestionsEncripted.put(Encriptions.encript(question), Encriptions.encript(answer));
	}
	
	public void removeQuestion(String question) {
		for (String questionEncripted : this.securityQuestionsEncripted.keySet()) {
			if (Decriptions.decript(questionEncripted).equals(question)) {
				this.securityQuestionsEncripted.remove(questionEncripted);
			}
		}
	}
	
	/**
	 * Meant to be instantiated when an account is null.
	 */
	private Account() {
		this.type = "NO_TYPE";
		this.usernameEncripted = Encriptions.encript("NO_USERNAME");
		this.encriptedPassword = Encriptions.encript("NO_PASSWORD");
	}
	
	public static Account nullAccount() {
		return new Account();
	}
}

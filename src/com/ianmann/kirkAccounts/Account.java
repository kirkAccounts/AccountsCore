/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 23, 2016
 */
package com.ianmann.kirkAccounts;

import java.util.HashMap;

import iansLibrary.security.Decriptions;
import iansLibrary.security.Encriptions;

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

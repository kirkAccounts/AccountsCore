/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 23, 2016
 */
package com.ianmann.kirkAccounts;

import java.util.HashMap;

import com.ianmann.kirkAccounts.security.Decriptions;
import com.ianmann.kirkAccounts.security.Encriptions;

/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 23, 2016
 *
 */
public class Account {
	
	public static final Account NULL_ACCOUNT = new Account();

	public String type;
	public String usernameEncripted;
	public String encriptedPassword;
	public HashMap<String, String> securityQuestionsEncripted = new HashMap<String, String>();
	
	public Account(String _type, String _username, String _password){
		this.type = _type.substring(0, 1).toUpperCase() + _type.substring(1);
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
		this.usernameEncripted = "NO_USERNAME";
		this.encriptedPassword = "NO_USERNAME";
	}
}

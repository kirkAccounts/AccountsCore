/**
 * @TODO: main class file
 *
 * @author Ian
 * Created: May 23, 2016
 */
package com.ianmann.kirkAccounts;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.ianmann.kirkAccounts.files.AccountFile;

/**
 * @TODO: Main class. No instantiation.
 *
 * @author Ian
 * Created: May 23, 2016
 *
 */
public final class KirkAccounts {
	
	/**
	 * Not allowed so this is private.
	 */
	private KirkAccounts() {}
	
	public static final String UNLOCK_KEY = "saline54";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		/*
		 * TODO If a .acct file is passed through args, then
		 * prompt the user for a password and if the password
		 * is valid, display the contents of the file.
		 */

		validate();
		
		AccountFile acctFile = AccountFile.readAccountFile("C:/Users/Ian/Desktop/accounts/test3.acct");
//		acctFile.getAccount().type = "Google";
//		acctFile.getAccount().encriptedPassword = "hahahahaha";
//		acctFile.getAccount().addDecriptedSecurityQuestion("What's your first name", "Ian");
//////		acctFile.getAccount().addDecriptedSecurityQuestion("What's your last name", "Kirkpatrick");
//		acctFile.save();
//		System.out.println(acctFile.getAccount().getDecriptedSecurityQuestions());
		AccountFile acctFile2 = AccountFile.readAccountFile("test.acct");
	}
	
	public static void validate() {
		String usersPassword = JOptionPane.showInputDialog("This editor is password protected.\nPlease enter the correct password.");
		if (usersPassword.equals(UNLOCK_KEY)) {
			return;
		} else {
			JOptionPane.showMessageDialog(null, "Sorry, that is not the correct password.");
			System.exit(-1);
		}
	}

}

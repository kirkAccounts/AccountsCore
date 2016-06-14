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
		
		AccountFile acctFile = AccountFile.readAccountFile(args[0]);
		acctFile.getAccount().type = "Google";
		acctFile.getAccount().encriptedPassword = "hahahahaha";
		acctFile.save();
		AccountFile f = AccountFile.readAccountFile(args[0]);
		System.out.println(f.getAccount().type);
		System.out.println(f.getAccount().usernameEncripted);
		System.out.println(f.getAccount().encriptedPassword);
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

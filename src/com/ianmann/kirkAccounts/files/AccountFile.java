/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 23, 2016
 */
package com.ianmann.kirkAccounts.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;

import com.ianmann.kirkAccounts.Account;
import com.ianmann.kirkAccounts.errors.CorruptFileException;
import com.ianmann.kirkAccounts.errors.WrongFileTypeException;

import iansLibrary.data.structures.ByteList;

/**
 * <p>@TODO: Make functionality to read the file
 * if it exists.</p>
 * 
 * Format of a .acct file:
 * 0x89 a c c t \r \n 0x1a \r
 * 0xaa {account.type.length} | {account.username.length} | {account.password.length} |
 * {amount_bytes_in_this_file} 0xbe 0x1a \r \n
 * 0x90\r\n
 * 0xab{account.type}
 * 0xac{account.username(encripted)}
 * 0xad{account.password(encripted)}
// * 0xae{account.security_question1(encripted)}0xaf{account.security_answer1(encripted)}
// * 0xae{account.security_question2(encripted)}0xaf{account.security_answer2(encripted)}
// * ... (other security questions)
 * 0xba
 * 0xbb\r\n
 * 0xbc{account.type}
 * 0xbd
 * 0x1a
 *
 * @author Ian
 * Created: May 23, 2016
 *
 */
public class AccountFile {
	
	private FileOutputStream outStream;
	private FileInputStream inStream;
	
	private String filePath;
	private ByteList fileBytes;
	
	/**
	 * Formatted as so:
	 * 0x89 a c c t \r \n 0x1a \r
	 */
	private static final byte[] ID_STRING = new byte[]{(byte) 0x89, 0x61, 0x63, 0x63, 0x74, '\r', '\n', 0x1a, '\r'};
	public static final int ID_STRING_LENGTH = ID_STRING.length;
	
	private Account account = Account.nullAccount();

	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public static AccountFile paste(ByteList bytes, String _path) throws WrongFileTypeException, CorruptFileException {
		AccountFile acctFile = new AccountFile(_path);
		acctFile.fileBytes = bytes;
		acctFile.readAccount();
		
		return acctFile;
	}

	private AccountFile(String _path) {
		this.filePath = _path;
	}
	
	private AccountFile() {
		this.fileBytes = new ByteList();
	}
	
	private AccountFile(File _file) {
		this.filePath = _file.getAbsolutePath();
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	
	public File getFile() {
		return new File(this.filePath);
	}
	
	public ByteList copy() {
		return this.fileBytes;
	}
	
	
	
	
	
	
	public static AccountFile readAccountFile(String _path) throws IOException {
		AccountFile acct = new AccountFile(_path);
		acct.readFileBytes();
		
		acct.readAccount();
		
		return acct;
	}
	
	public void readFileBytes() throws IOException {
		this.inStream = new FileInputStream(this.getFile());
		this.fileBytes = new ByteList();
		while (true) {
			int b = this.inStream.read();
			if (b == -1) {
				break;
			} else {
				this.fileBytes.add(b);
			}
		}
		this.inStream.close();
	}
	
	public static final ByteList TYPE_SIGNIFYER = new ByteList(0xaa, 0x21, 0x5c, 0x54, 0x3a);
	public static final ByteList USERNAME_SIGNIFYER = new ByteList(0xab, 0x21, 0x5c, 0x55, 0x3a);
	public static final ByteList PASSWORD_SIGNIFYER = new ByteList(0xac, 0x21, 0x5c, 0x50, 0x3a);
	
	public void readAccount() throws WrongFileTypeException, CorruptFileException {
		int valid = this.checkHeader();
		if (valid == 0) {
			this.account.type = this.getType();
			this.account.usernameEncripted = this.getUsername();
			this.account.encriptedPassword = this.getPassword();
		} else if (valid == 1) {
			throw new WrongFileTypeException();
		} else if (valid == 2) {
			throw new CorruptFileException();
		}
	}
	
	/**
	 * Validates the header of the file and returns a status.<br>
	 * <ul>
	 * 	<li>0 - header is correct</li>
	 * 	<li>1 - id string is incorrect. file is not acct file</li>
	 * 	<li>2 - checksum failed</li>
	 * </ul>
	 * @return
	 */
	public int checkHeader() {
		ByteList header = this.fileBytes.slice(0, new ByteList(0x00, 10));
		if (checkIDString()) {
			/*
			 * This is an acct file. so check th checksum
			 */
			if (this.checkCheckSum()) {
				return 0;
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}
	
	public boolean checkIDString() {
		int indexOfIDString = this.fileBytes.indexOfGroup(new ByteList(ID_STRING));
		if (indexOfIDString == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkCheckSum() {
		ByteList checkSum = this.fileBytes.between(new ByteList(ID_STRING), new ByteList(0x00, 10));
		int lenType = checkSum.get(1);
		int lenUsername = checkSum.get(3);
		int lenPassword = checkSum.get(5);
		if (lenType == this.getType().length()) {
			if (lenUsername == this.getUsername().length()) {
				if (lenPassword == this.getPassword().length()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getType() {
		ByteList typeBytes = this.fileBytes.between(TYPE_SIGNIFYER, USERNAME_SIGNIFYER);
		String type = typeBytes.flippedBytes().stringValues();
		return type;
	}
	
	public String getUsername() {
		ByteList usernameBytes = this.fileBytes.between(USERNAME_SIGNIFYER, PASSWORD_SIGNIFYER);
		String username = usernameBytes.flippedBytes().stringValues();
		return username;
	}
	
	public String getPassword() {
		ByteList passwordBytes = this.fileBytes.between(PASSWORD_SIGNIFYER, new ByteList(0xd3, 0x0d, 0x0a));
		String password = passwordBytes.flippedBytes().stringValues();
		return password;
	}
	
	
	
	
	
	
	
	public static AccountFile newAccount(String _path) throws FileAlreadyExistsException {
		AccountFile acct = new AccountFile(_path);
		if (!acct.getFile().exists()) {
			try {
				acct.getFile().createNewFile();
				acct.save();
				return acct;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			throw new FileAlreadyExistsException(_path);
		}
	}
	
	public void save() throws IOException {
		File unknownFile = new File(this.filePath);
		this.generateFileBytes();
		try {
			this.outStream = new FileOutputStream(unknownFile);
			this.outStream.write(this.fileBytes.toPrimitive());
			this.outStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			if(unknownFile.createNewFile()) {
				this.outStream = new FileOutputStream(unknownFile);
				this.outStream.write(this.fileBytes.toPrimitive());
				this.outStream.close();
			}
		}
	}
	
	public void generateFileBytes() {
		this.fileBytes = new ByteList();
		this.generateHeader();
		this.fileBytes.addRepetition(0x00, 10);
		this.generateBody();
		this.fileBytes.addRepetition(0x00, 10);
		this.generateFooter();
	}
	
	public void generateHeader() {
		this.fileBytes.addAll(ID_STRING);
		this.fileBytes.join(this.generateChecksum());
	}
	
	public ByteList generateChecksum() {
		ByteList ret = new ByteList();
		ret.add(0xd1);
		ret.add(this.getAccount().type.getBytes().length);
		ret.add(0x7c);
		ret.add(this.getAccount().usernameEncripted.getBytes().length);
		ret.add(0x7c);
		ret.add(this.getAccount().encriptedPassword.getBytes().length);
		ret.addAll(0xbe, 0x1a, 0x0d, 0x0a);
		return ret;
	}
	
	public void generateBody() {
		this.fileBytes.addAll(0xd2, 0x90, 0x0d, 0x0a);
		this.fileBytes.addAll(this.generateTypeBytes());
		this.fileBytes.addAll(this.generateUsernameBytes());
		this.fileBytes.addAll(this.generatePasswordBytes());
		this.fileBytes.addAll(0xd3, 0x0d, 0x0a);
	}
	
	public ByteList generateTypeBytes() {
		ByteList ret = new ByteList();
		ret.addAll(0xAA, 0x21, 0x5C, 0x54, 0x3A);
		ret.addAll(ByteList.flipBytes(this.getAccount().type.getBytes()));
		return ret;
	}
	
	public ByteList generateUsernameBytes() {
		ByteList ret = new ByteList();
		ret.addAll(0xAB, 0x21, 0x5C, 0x55, 0x3A);
		ret.addAll(ByteList.flipBytes(this.getAccount().usernameEncripted.getBytes()));
		return ret;
	}
	
	public ByteList generatePasswordBytes() {
		ByteList ret = new ByteList();
		ret.addAll(0xAC, 0x21, 0x5C, 0x50, 0x3A);
		ret.addAll(ByteList.flipBytes(this.getAccount().encriptedPassword.getBytes()));
		return ret;
	}
	
	private void generateFooter() {
		this.fileBytes.addAll(0xd4, 0x0d, 0x0a);
		this.fileBytes.addAll(0xAD, 0x21, 0x5C, 0x54, 0x3A);
		this.fileBytes.addAll(this.getAccount().type.getBytes());
		this.fileBytes.addAll(0xd5, 0x0d, 0x0a, 0x1a);
	}

}

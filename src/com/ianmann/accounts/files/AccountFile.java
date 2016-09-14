/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 23, 2016
 */
package com.ianmann.accounts.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;

import com.ianmann.accounts.Account;
import com.ianmann.accounts.errors.CorruptFileException;
import com.ianmann.accounts.errors.WrongFileTypeException;
import com.ianmann.utils.data.structures.ByteList;

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
	/**
	 * Signifies beginning of question and answers. if none exists, a return character (0x0a) will follow.
	 */
	public static final ByteList SECURITY_QUESTION_ANSWER_SIGNIFYER = new ByteList(0xac, 0x21, 0x5c, 0x51, 0x41, 0x3a);
	public static final ByteList SECURITY_QUESTION_SIGNIFYER = new ByteList(0xac, 0x21, 0x5c, 0x51, 0x3a);
	public static final ByteList SECURITY_ANSWER_SIGNIFYER = new ByteList(0xac, 0x21, 0x5c, 0x41, 0x3a);
	/**
	 * Signifies end of a question/answer entry.
	 */
	public static final ByteList SECUTITY_QUESTION_END_SIGNIFYER = new ByteList(0xac, 0x21, 0x5c, 0x45, 0x3a);
	
	public void readAccount() throws WrongFileTypeException, CorruptFileException {
		int valid = this.checkHeader();
		if (valid == 0) {
			try {
			this.account.type = this.getType();
			this.account.usernameEncripted = this.getUsername();
			this.account.encriptedPassword = this.getPassword();
			this.getSecurityQuestions();
			} catch (Exception e) {
				throw new CorruptFileException(e);
			}
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
		ByteList passwordBytes = this.fileBytes.between(PASSWORD_SIGNIFYER, SECURITY_QUESTION_ANSWER_SIGNIFYER);
		String password = passwordBytes.flippedBytes().stringValues();
		return password;
	}
	
	public void getSecurityQuestions() {
		ByteList allQuestions = this.fileBytes.between(SECURITY_QUESTION_ANSWER_SIGNIFYER, new ByteList(0xd3, 0x0d, 0x0a));
		
		boolean done = false;
		while (!done) {
			if (allQuestions.size() > 1 && allQuestions.slice(0, 4).equals(SECURITY_QUESTION_SIGNIFYER)) {
				/*
				 * remove question signifyer, jump to the next question, then remove all bytes
				 * that remain in the previous question (the one we just counted)
				 */
				allQuestions = allQuestions.slice(5, allQuestions.size()-1);
				/*
				 * Now get the question and answer.
				 */
				ByteList questionBytes = allQuestions.slice(0, SECURITY_ANSWER_SIGNIFYER);
				ByteList answerBytes = allQuestions.between(SECURITY_ANSWER_SIGNIFYER, SECUTITY_QUESTION_END_SIGNIFYER);
				String question = questionBytes.flippedBytes().stringValues();
				String answer = answerBytes.flippedBytes().stringValues();
				this.account.securityQuestionsEncripted.put(question, answer);
				
				/*
				 * done with that question so remove it from allQuestions
				 */
				int nextIndex = allQuestions.indexOfGroup(SECURITY_QUESTION_SIGNIFYER);
				if (nextIndex != -1) {
					allQuestions = allQuestions.slice(nextIndex, allQuestions.size()-1);
				} else {
					done = true;
				}
			} else {
				done = true;
			}
		}
	}
	
	public int numQuestions() {
		ByteList allQuestions = this.fileBytes.between(SECURITY_QUESTION_ANSWER_SIGNIFYER, new ByteList(0xd3, 0x0d, 0x0a));
		int count = 0;
		boolean done = false;
		while (!done) {
			if (allQuestions.slice(0, 4).equals(SECURITY_QUESTION_SIGNIFYER)) {
				count ++;
				/*
				 * remove question signifyer, jump to the next question, then remove all bytes
				 * that remain in the previous question (the one we just counted)
				 */
				for (int i = 0; i < 5; i++) {
					allQuestions.remove(i);
				}
				int nextIndex = allQuestions.indexOfGroup(SECURITY_QUESTION_SIGNIFYER);
				if (nextIndex != -1) {
					allQuestions = allQuestions.slice(nextIndex, allQuestions.size()-1);
				} else {
					done = true;
				}
			} else {
				done = true;
			}
		}
		return count;
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
		this.fileBytes.addRepetition(0x00, 100);
		this.generateBody();
		this.fileBytes.addRepetition(0x00, 100);
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
		this.fileBytes.addAll(this.generateSecurityQuestions());
		this.fileBytes.addAll(0xd3, 0x0d, 0x0a);
//		System.out.println(this.fileBytes);
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
	
	public ByteList generateSecurityQuestions() {
		ByteList ret = new ByteList();
		ret.addAll(0xAC, 0x21, 0x5C, 0x51, 0x41, 0x3A);
		if (this.account.securityQuestionsEncripted.size() == 0) {
			ret.add(0x0a);
		} else {
			for (String question : this.account.securityQuestionsEncripted.keySet()) {
				ret.addAll(0xAC, 0x21, 0x5C, 0x51, 0x3A);
				ret.addAll(new ByteList(question.getBytes()).flippedBytes());
				ret.addAll(0xAC, 0x21, 0x5C, 0x41, 0x3A);
				ret.addAll(new ByteList(this.account.securityQuestionsEncripted.get(question).getBytes()).flippedBytes());
				ret.addAll(new ByteList(0xac, 0x21, 0x5c, 0x45, 0x3a));
			}
		}
		return ret;
	}
	
	private void generateFooter() {
		this.fileBytes.addAll(0xd4, 0x0d, 0x0a);
		this.fileBytes.addAll(0xAD, 0x21, 0x5C, 0x54, 0x3A);
		this.fileBytes.addAll(this.getAccount().type.getBytes());
		this.fileBytes.addAll(0xd5, 0x0d, 0x0a, 0x1a);
	}

}

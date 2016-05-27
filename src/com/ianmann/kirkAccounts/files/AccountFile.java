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
import java.util.ArrayList;
import java.util.Arrays;

import com.ianmann.kirkAccounts.Account;
import com.ianmann.kirkAccounts.errors.CorruptFileException;
import com.ianmann.kirkAccounts.errors.WrongFileTypeException;

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
	
	/**
	 * Formatted as so:
	 * 0x89 a c c t \r \n 0x1a \r
	 */
	private static final byte[] ID_STRING = new byte[]{(byte) 0x89, 0x61, 0x63, 0x63, 0x74, '\r', '\n', 0x1a, '\r'};
	public static final int ID_STRING_LENGTH = ID_STRING.length;
	
	private Account account = Account.NULL_ACCOUNT;

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

	/**
	 * @param _path
	 */
	private AccountFile(String _path) {
		this.filePath = _path;
	}
	
	public void setAccountAttributes(byte[] _fileBytes) {
		this.account.type = this.readType(_fileBytes);
		this.account.usernameEncripted = this.readUsername(_fileBytes);
		this.account.encriptedPassword = this.readPassword(_fileBytes);
	}
	
	/**
	 * <p>Reads the file at {@code pathToAccount} and parses
	 * it into an Account object.</p>
	 * @param pathToAccount
	 * @return AccountFile object containing an Account object at {@code pathToAccount}
	 * @throws IOException 
	 */
	public static AccountFile readAccountFile(String pathToAccount) throws IOException {
		AccountFile file = new AccountFile(pathToAccount);
		byte[] fileBytes = file.getByteContents();
		int validStatus = file.validateFile(fileBytes);
		
		switch (validStatus) {
		case 0:
			file.setAccountAttributes(fileBytes);
			return file;
		case 1:
			throw new CorruptFileException();
		case 2:
			throw new WrongFileTypeException();
		default:
			break;
		}
		
		return file;
	}
	
	/**
	 * Reads the bytes contained in {@code this.filePath}.
	 * This method does not validate the bytes.
	 * @return
	 * @throws IOException 
	 */
	public byte[] getByteContents() throws IOException {
		this.inStream = new FileInputStream(this.filePath);
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		
		boolean done = false;
		while (!done) {
			int b = this.inStream.read();
			if (b == -1) {
				done = true;
				break;
			}
			bytes.add((byte) b);
		}
		this.inStream.close();
		return AccountFilesUtil.wrapperByteToPrimitive(bytes);
	}
	
	public static boolean checkIdString(byte[] bytes) {
		if (Arrays.equals(bytes, ID_STRING)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * <p>Finds the type in the file and returns it
	 * as an encrypted string.</p>
	 * 
	 * <p>If type not found (signifier byte not found)
	 * then this will return null.</p>
	 * @param _fileBytes
	 * @return
	 */
	private String readType(byte[] _fileBytes) {
		ArrayList<Byte> typeBytes = new ArrayList<Byte>();
		boolean readingType = false;
		for (int i = 0; i < _fileBytes.length; i++) {
			if ((_fileBytes[i] & 0xff) == 0xac) {
				readingType = false;
				break;
			}
			if (readingType) {
				typeBytes.add(_fileBytes[i]);
			}
			if ((_fileBytes[i] & 0xff) == 0xab) {
				readingType = true;
			}
		}
		if (typeBytes.size() > 0) {
			return AccountFilesUtil.fileBytesToTrueString(typeBytes);
		} else {
			return null;
		}
	}
	
	/**
	 * <p>Finds the username in the file and returns it
	 * as an encrypted string.</p>
	 * 
	 * <p>If username not found (signifier byte not found)
	 * then this will return null.</p>
	 * @param _fileBytes
	 * @return
	 */
	private String readUsername(byte[] _fileBytes) {
		ArrayList<Byte> usernameBytes = new ArrayList<Byte>();
		boolean readingUsername = false;
		for (int i = 0; i < _fileBytes.length; i++) {
			if ((_fileBytes[i] & 0xff) == 0xad) {
				readingUsername = false;
				break;
			}
			if (readingUsername) {
				usernameBytes.add(_fileBytes[i]);
			}
			if ((_fileBytes[i] & 0xff) == 0xac) {
				readingUsername = true;
			}
		}
		if (usernameBytes.size() > 0) {
			return AccountFilesUtil.fileBytesToTrueString(usernameBytes);
		} else {
			return null;
		}
	}
	
	/**
	 * <p>Finds the password in the file and returns it
	 * as an encrypted string.</p>
	 * 
	 * <p>If password not found (signifier byte not found)
	 * then this will return null.</p>
	 * @param _fileBytes
	 * @return
	 */
	private String readPassword(byte[] _fileBytes) {
		ArrayList<Byte> passwordBytes = new ArrayList<Byte>();
		boolean readingPassword = false;
		for (int i = 0; i < _fileBytes.length; i++) {
			if ((_fileBytes[i] & 0xff) == 0xae || (_fileBytes[i] & 0xff) == 0xbf || (_fileBytes[i] & 0xff) == 0xba) {
				readingPassword = false;
				break;
			}
			if (readingPassword) {
				passwordBytes.add(_fileBytes[i]);
			}
			if ((_fileBytes[i] & 0xff) == 0xad) {
				readingPassword = true;
			}
		}
		if (passwordBytes.size() > 0) {
			return AccountFilesUtil.fileBytesToTrueString(passwordBytes);
		} else {
			return null;
		}
	}
	
	/**
	 * <p>reads the header of the file and return
	 * it with a status code as the first index.
	 * If it was not successful, the only byte will
	 * be the status code. No other content will be returned.</p>
	 * 
	 * <p>
	 * <ul>
	 * 	<li>0xb1 - Good</li>
	 * 	<li>0xb2 - Wrong File Type (wrong ID_STRING)</li>
	 * 	<li>0xb3 - missing some data</li>
	 * 	<li>0xb4 - file is corrupt (checksum failed)</li>
	 * </ul>
	 * </p>
	 * @param _fileBytes
	 * @return
	 */
	private byte[] readHeader(byte[] _fileBytes) {
		try {
			for (int i = 0; i < ID_STRING.length; i++) {
				if (_fileBytes[i] != ID_STRING[i]) {
					return new byte[]{(byte) 0xb2};
				}
			}
			// ID String is correct. onto the checksum
			int headerEndIndex = 0;
			for (int i = 0; i < _fileBytes.length; i++) {
				if ((_fileBytes[i] & 0xff) == 0xaa) {
					int typeLen = (int) _fileBytes[i + 1];
					int usernameLen = (int) _fileBytes[i + 3];
					int passwordLen = (int) _fileBytes[i + 5];
					if ((_fileBytes[i + 6] & 0xff) != 0xbe) {
						return new byte[]{(byte) 0xb3};
					}
					if (typeLen == this.readType(_fileBytes).length() &&
								usernameLen == this.readUsername(_fileBytes).length() &&
								passwordLen == this.readPassword(_fileBytes).length()) {
						headerEndIndex = i + 6;
						break;
					} else {
						break;
					}
				}
			}
			
			/*
			 * Two cases at this point:
			 * - found header in right format. return success
			 * - checksum failed so we know that the file was tampered with.
			 */
			if (headerEndIndex > ID_STRING.length) {
				byte[] bytesToReturn = new byte[headerEndIndex + 1];
				bytesToReturn[0] = (byte) 0xb1;
				for (int i = 1; i < headerEndIndex + 1; i++) {
					bytesToReturn[i] = _fileBytes[i];
				}
				return bytesToReturn;
			} else {
				return new byte[]{(byte) 0xb4};
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return new byte[]{(byte) 0xb2};
		}
	}
	
	/**
	 * <p>reads the footer of the file and return 
	 * it with a status code as the first index.
	 * If it was not successful, the only byte will
	 * be the status code. No other content will be returned.</p>
	 * 
	 * <p>Status code meanings:
	 * <ul>
	 * 	<li>0xb1 - Good</li>
	 * 	<li>0xb2 - incomplete footer (truncated)</li>
	 * 	<li>0xb3 - Corrupt footer. unexpected format</li>
	 * 	<li>0xb4 - Footer missing</li>
	 * </ul>
	 * </p>
	 * @param _fileBytes
	 * @return
	 */
	private byte[] readFooter(byte[] _fileBytes) {
		for (int i = 0; i < _fileBytes.length; i++) {
			byte current = _fileBytes[i];
			if ((current & 0xff) == 0xbb && (_fileBytes[i+1] & 0xff) == '\r' && (_fileBytes[i+2] & 0xff) == '\n') {
				if ((_fileBytes[i+3] & 0xff) == 0xbc) {
					for (int j = i+3; j < _fileBytes.length; j++) {
						if ((_fileBytes[j] & 0xff) == 0xbd && (_fileBytes[j+1] & 0xff) == 0x1a) {
							int len = (j+1) - (i) + 2; //length of footer
							byte[] bs = new byte[len];
							bs[0] = (byte) 0xb1; // footer is good so add the success byte to beginning of return array.
							int count = 1;
							for (int k = i; k < _fileBytes.length; k++) {
								bs[count] = _fileBytes[k];
								count ++;
							}
							return bs;
						}
					}
					// If this loop finishes, the footer has been
					// corrupt. It never found the two ending bytes. (0xbd, 0x1a)
					return new byte[]{(byte) 0xb3};
				} else {
					//Could not find the account type identifier byte (0xbc).
					return new byte[]{(byte) 0xb3};
				}
			}
		}
		// If this loop finishes, the footer signifier bytes (0xbb\r\n) could
		// not be found.
		return new byte[]{(byte) 0xb4};
	}
	
	/**
	 * Validates a file by checking the format.<br>
	 * Returns a status code based on whether the<br>
	 * file could be parsed or not as a .acct file.<br><br>
	 * The various status codes are as follows:
	 * <ul>
	 * 	<li>0 - File is good</li>
	 * 	<li>1 - Corrupt data</li>
	 * 	<li>2 - Not a .acct file (ID_STRING incorrect)</li>
	 *  <li>3 - unknown (according to the code, this should never happen but just in case...</li>
	 * </ul>
	 * @param _fileBytes
	 * @return
	 */
	public int validateFile(byte[] _fileBytes) {
		// Check Header
		byte[] headerBytes = this.readHeader(_fileBytes);
		if ((headerBytes[0] & 0xff) != 0xb1) {
			int headerStatus = headerBytes[0] & 0xff;
			/* 
			 * Header status codes available
			 * - 0xb1 - Good
			 * - 0xb2 - Wrong File Type (wrong ID_STRING)
			 * - 0xb3 - missing some data
			 * - 0xb4 - file is corrupt (checksum failed)
			 */
			switch (headerStatus) {
			case 0xb2:
				return 2;
			case 0xb3:
				return 1;
			case 0xb4:
				return 1;
			default:
				return 3; // Probably this will never be hit.
			}
		}
		
		// Check Footer
		byte[] footerBytes = this.readFooter(_fileBytes);
		if ((footerBytes[0] & 0xff) != 0xb1) {
			int footerStatus = footerBytes[0] & 0xff;
			/* 
			 * Header status codes available
			 * - 0xb1 - Good
			 * - 0xb2 - Incomplete Footer (Truncated)
			 * - 0xb3 - corrupt footer
			 * - 0xb4 - footer not found
			 */
			switch (footerStatus) {
			case 0xb2:
				return 1;
			case 0xb3:
				return 1;
			case 0xb4:
				return 1;
			default:
				return 3; // Again, probably this will never be hit.
			}
			/*
			 * I realize at the moment that all cases will return
			 * 1 but just in case in the future I want to add more
			 * status codes, I'll keep the switch statement.
			 */
		} else {
			/*
			 * If this is hit, we know that the header check
			 * passed and so did the footer check. So this is
			 * a valid file.
			 */
			return 0;
		}
	}
	
	

	
	/**
	 * <p>Initialize a new account file.</p>
	 * <p>
	 * 	<ul>
	 * 		<li>Creates a new file</li>
	 *		<li>Creates a new account for that file</li>
	 *		<li>opens the editor for that account</li>
	 * 	</ul>
	 * </p>
	 * @return
	 */
	public static AccountFile newAccount(String _pathToAccount) {
		AccountFile f = new AccountFile(_pathToAccount);
		f.save();
		return f;
	}
	
	public boolean save() {
		try {
			this.outStream = new FileOutputStream(this.filePath, false);
			byte[] fileContents = this.generateFileContents();
			this.outStream.write(fileContents);
			this.outStream.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Create the file then save.
			try {
				if(new File(this.filePath).createNewFile()) {
					return this.save();
				} else {
					return false;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private byte[] generateFileContents() {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		
		bytes.addAll(this.generateHeader());
		bytes.addAll(this.generateAccountBytes());
		bytes.addAll(this.generateFooter());
		
		return AccountFilesUtil.wrapperByteToPrimitive(bytes);
	}
	
	/**
	 * header is formatted as so:
	 * {ID_STRING} {checkSum} 0x1a \r \n
	 * @return
	 */
	private ArrayList<Byte> generateHeader() {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		
		bytes.addAll(AccountFilesUtil.wrapperPrimitiveToByte(ID_STRING));
		bytes.addAll(this.generateChecksum());
		
		bytes.add((byte) 0x1a);
		bytes.add((byte) '\r');
		bytes.add((byte) '\n');
		
		return bytes;
	}
	
	/**
	 * CheckSum is formatted as so:
	 * 
	 * 0xaa {account.type.length} | {account.username.length} | {account.password.length} |
	 * 0xbe
	 * @return
	 */
	private ArrayList<Byte> generateChecksum() {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		
		bytes.add((byte) 0xaa);
		
		bytes.add((byte) this.account.type.length());
		bytes.add((byte) '|');
		bytes.add((byte) this.account.usernameEncripted.length());
		bytes.add((byte) '|');
		bytes.add((byte) this.account.encriptedPassword.length());
		
		bytes.add((byte) 0xbe);
		
		return bytes;
	}
	
	/**
	 * Account format is as so:
	 * 
	 * 0x90\r\n
	 * 0xab{account.type}
	 * 0xac{account.username(encripted)}
	 * 0xad{account.password(encripted)}
	 * 0xae{account.security_question1(encripted)}0xaf{account.security_answer1(encripted)}
	 * 0xae{account.security_question2(encripted)}0xaf{account.security_answer2(encripted)}
	 * ... (other security questions)
	 * 0xba
	 * 
	 * 
	 * @param acct
	 * @return
	 */
	private ArrayList<Byte> generateAccountBytes() {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		bytes.add((byte) 0x90); bytes.add((byte) '\r'); bytes.add((byte) '\n');
		
		this.generateTypeBytes(this.account, bytes);
		this.generateUsernameBytes(this.account, bytes);
		this.generatePasswordBytes(this.account, bytes);
		
		bytes.add((byte) 0xba);

		return bytes;
	}
	
	private void generateTypeBytes(Account acct, ArrayList<Byte> bytes) {
		bytes.add((byte) 0xab);
		byte[] typeBytes = acct.type.getBytes();
		for (int i = 0; i < typeBytes.length; i++) {
			bytes.add((byte) ~(byte) (typeBytes[i]));
		}
	}
	
	private void generateUsernameBytes(Account acct, ArrayList<Byte> bytes) {
		bytes.add((byte) 0xac);
		byte[] usernameBytes = acct.usernameEncripted.getBytes();
		for (int i = 0; i < usernameBytes.length; i++) {
			bytes.add((byte) ~(byte) (usernameBytes[i]));
		}
	}
	
	private void generatePasswordBytes(Account acct, ArrayList<Byte> bytes) {
		bytes.add((byte) 0xad);
		byte[] passwordBytes = acct.encriptedPassword.getBytes();
		for (int i = 0; i < passwordBytes.length; i++) {
			bytes.add((byte) ~(byte) (passwordBytes[i]));
		}
	}
	
	/**
	 * This is in the format:
	 * 0xbb\r\n
	 * 0xbc{account.type}
	 * 0xbd
	 * 0x1a
	 * @return
	 */
	private ArrayList<Byte> generateFooter() {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		
		bytes.add((byte) 0xbb); bytes.add((byte) '\r'); bytes.add((byte) '\n');

		bytes.add((byte) 0xbc);
		bytes.addAll(AccountFilesUtil.wrapperPrimitiveToByte(this.account.type.getBytes()));
		
		bytes.add((byte) 0xbd);
		bytes.add((byte) 0x1a);
		
		return bytes;
	}

}

/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 24, 2016
 */
package com.ianmann.kirkAccounts.files;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 24, 2016
 *
 */
public final class AccountFilesUtil {

	private AccountFilesUtil(){}
	
	/**
	 * <p>checks the id string in a file to make sure that it is
	 * of the format .acct</p>
	 * 
	 * <p>if not, return false.</p>
	 * @return
	 * @throws IOException
	 */
	public static boolean fileIsAcct(String filePath) throws IOException {
		FileInputStream inStream = new FileInputStream(filePath);
		byte[] expectedIdString = new byte[AccountFile.ID_STRING_LENGTH];
		inStream.read(expectedIdString);
		inStream.close();
		
		if (AccountFile.checkIdString(expectedIdString)) {
			return true;
		}
		return false;
	}
	
	public static boolean dataIsAcct(byte[] data) {
		
		byte[] expectedIdString = new byte[AccountFile.ID_STRING_LENGTH];
		for (int i = 0; i < expectedIdString.length; i++) {
			expectedIdString[i] = data[i];
		}
		
		if (AccountFile.checkIdString(expectedIdString)) {
			return true;
		}
		return false;
	}
	
	public static ArrayList<Byte> wrapperPrimitiveToByte(byte[] bytes) {
		ArrayList<Byte> bytesArrayList = new ArrayList<Byte>();
		for (int i = 0; i < bytes.length; i++) {
			bytesArrayList.add(bytes[i]);
		}
		return bytesArrayList;
	}
	
	public static byte[] wrapperByteToPrimitive(ArrayList<Byte> bytes) {
		byte[] bytesPrimitive = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			bytesPrimitive[i] = bytes.get(i);
		}
		return bytesPrimitive;
	}
	
	/**
	 * Because an AccountFile flips the bytes in data it stores,
	 * we will "unflip" them and return the data as a string.
	 * @param bytes
	 * @return
	 */
	public static String fileBytesToTrueString(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ~ bytes[i];
		}
		String retString = new String(bytes);
		
		/*
		 * because arrays remain changed after going through
		 * a method, we need to put bytes back to the way it
		 * came in.
		 */
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) ~ bytes[i];
		}
		
		return retString;
	}
	
	/**
	 * Because an AccountFile flips the bytes in data it stores,
	 * we will "unflip" them and return the data as a string.
	 * @param bytes
	 * @return
	 */
	public static String fileBytesToTrueString(ArrayList<Byte> bytes) {
		byte[] newBytes = wrapperByteToPrimitive(bytes);
		for (int i = 0; i < newBytes.length; i++) {
			newBytes[i] = (byte) ~ newBytes[i];
		}
		return new String(newBytes);
	}
	
//	public static byte[] bytesToUnsigned(byte[] bytes) {
//		for (int i = 0; i < bytes.length; i++) {
//			bytes[i] = (byte) (bytes[i] & 0xff);
//		}
//	}
	
}

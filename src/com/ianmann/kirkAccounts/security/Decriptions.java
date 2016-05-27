package com.ianmann.kirkAccounts.security;

import java.util.Arrays;

public abstract class Decriptions {

	/**
	 * * letters to ascii
	 * * ascii nums += 7
	 * * ascii nums *= 7
	 * * put a 4 at the beginning of the value of this letter
	 * * put a 3 at the end of the value of this letter
	 * * flip the word around
	 * <br>
	 * * flip the word around
	 * * remove the random number at the end of the value of this letter
	 * * remove the random number at the beginning of the value of this letter
	 * * ascii nums /= 7
	 * * ascii nums -= 7
	 * * letters from ascii
	 * @param str
	 * @return
	 */
	public static String decript(String str){
		String finalString = "";
		String tempString = "";
		char[] strArray;
		String[] trueArray;
		
		//flipping around
		for (int i = str.length() - 1; i >= 0; i --) {
			tempString = tempString + String.valueOf(str.charAt(i));
		}
		trueArray = tempString.split("!|\\*|\\^");
		for (int i = 0; i < trueArray.length; i++) {
			//removing 4 and 3 from ends of character value
			String val = trueArray[i].substring(1, trueArray[i].length()-1);
			Integer intVal = Integer.valueOf(val);
			intVal = intVal / 7;
			intVal = intVal - 7;
			finalString = finalString + ((char) intVal.intValue());
		}
		
		return finalString;
	}
}

package com.ianmann.kirkAccounts.security;

public abstract class Encriptions {

	/**
	 * * letters to ascii
	 * * ascii nums += 7
	 * * ascii nums *= 7
	 * * put a random number at the beginning of the value of this letter
	 * * put a random number at the end of the value of this letter
	 * * flip the word around
	 * @param str
	 * @return
	 */
	public static String encript(String str){
		char[] strArray = str.toCharArray();
		String nstr = "";
		String finalString = "";
		
		for (int i = 0; i < strArray.length; i++) {
			int ascii = (int) strArray[i];
			ascii += 7;
			ascii *= 7;
			//adding a 3 to the end of ascii
			String first = String.valueOf(Integer.valueOf((String.valueOf((int)(Math.random()*10)).substring(0, 1))) + 1).substring(0, 1);
			String second = String.valueOf(Integer.valueOf((String.valueOf((int)(Math.random()*10)).substring(0, 1))) + 1).substring(0, 1);
			ascii = Integer.valueOf(first + String.valueOf(ascii) + second);
			
			//add it to delimeteredArray with it's coresponding symbol
			String finalVal = String.valueOf(ascii);
			if (Character.isLetter(strArray[i])) {
				finalVal = finalVal + "!";
			}
			else if (Character.isDigit(strArray[i])) {
				finalVal = finalVal + "*";
			}
			else{
				finalVal = finalVal + "^";
			}
			nstr = nstr + finalVal;
		}
		
		//flipping around
		for (int i = nstr.length() - 1; i >= 0; i --) {
			finalString = finalString + String.valueOf(nstr.charAt(i));
		}
		return finalString;
	}
}

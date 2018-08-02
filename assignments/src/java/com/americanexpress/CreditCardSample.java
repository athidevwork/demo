/**
 * 
 */
package com.americanexpress;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author athi
 *
 */
public class CreditCardSample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(validateCard("3721123423451218"));
		System.out.println(validateCard("4032123423451218"));
		System.out.println(validateCard("1234123423451218"));
		System.out.println(validateCard("1234123423457654"));
		System.out.println(validateCard("7652345678900954"));
		System.out.println(validateCard("765234567890987654"));
	}

	private static String validateCard(String cardNo) {
		//validate card
		Pattern pattern = Pattern.compile("^[1-9][0-9]{9,15}$");
		Matcher match = pattern.matcher(cardNo);
		
		if (!match.matches())
			return cardNo + " - Invalid number of digits";
		
		//check for last 4 digits to be valid month year
		Pattern monthYearPattern = Pattern.compile("^((0[1-9]|1[012])([0-9][0-9]{0,2}))$");
		
		String last4Digits = cardNo.substring(cardNo.length()-4, cardNo.length());
		if (!monthYearPattern.matcher(last4Digits).matches())
			return cardNo + " - Invalid Month/Year combination";
		//System.out.println("last 4 digits = " + last4Digits);
		
		String first4Digits = cardNo.substring(0, 4);
		
		switch (first4Digits) {
		case "1234":
			return cardNo + " - ABC";
		case "4032":
			return cardNo + " - Visa";
		case "3721":
			return cardNo + " - Amex";
		default:
			return cardNo + " - Invalid first 4 digits";
		}
	}

}

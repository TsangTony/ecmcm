package com.ibm.ecm.mm.util;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.ecm.mm.model.Lookup;

public class Util {
	
	public static boolean isNonNegativeInteger(String s) {
	    return isNonNegativeInteger(s,10);
	}

	public static boolean isNonNegativeInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static String findRegex(String searchString, String regex, String sequence) {
		Pattern pattern = Pattern.compile("(?i)"+regex);
		Matcher matcher = pattern.matcher(searchString);
		String value = "";
		if (sequence.equals("LAST")) {
			while (matcher.find())
				value = matcher.group();
		}
		else if (sequence.equals("FIRST")) {
			if (matcher.find())
				value = matcher.group();
		}
		return value;
	}
	
	public static String findRegex(String searchString, String regex, int capGroup, String sequence) {
		String value = "";
		if (capGroup==0)
			value = findRegex(searchString, regex, sequence);
		else {
			Pattern pattern = Pattern.compile("(?i)"+regex);
			Matcher matcher = pattern.matcher(searchString);
			if (sequence.equals("LAST")) {
				while (matcher.find())
					value = matcher.group(capGroup);
			}
			else if (sequence.equals("FIRST")) {
				if (matcher.find())
					value = matcher.group(capGroup);
			}
		}
		
		return value;
	}

	public static String findRegex(String searchString, Lookup lookup, String sequence) {
		String value = "";
		
		if (!findRegex(searchString, delimited(lookup.getLookupValue()), sequence).equals(""))
			return lookup.getReturnedValue();
		
		return value;
	}

	private static String delimited(String lookupValue) {
		String delimitor = "[\\W_]";
		return  "^" + lookupValue + delimitor + "|" + delimitor + lookupValue + "$|" + delimitor + lookupValue + delimitor + "|^" + lookupValue + "$";
	}		
	
	public static String getTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return "[" + strDate + "] ";
	}
	
}

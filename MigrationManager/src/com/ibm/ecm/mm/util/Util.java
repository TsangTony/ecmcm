package com.ibm.ecm.mm.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
	
	public static String findRegex(String searchString, String regex, int group, String sequence) {
		String value = "";
		if (group==0)
			value = findRegex(searchString, regex, sequence);
		else {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(searchString);
			if (sequence.equals("LAST")) {
				while (matcher.find())
					value = matcher.group(group);
			}
			else if (sequence.equals("FIRST")) {
				if (matcher.find())
					value = matcher.group(group);
			}
		}
		return value;
	}
}

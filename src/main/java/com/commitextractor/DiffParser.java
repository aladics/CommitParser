package com.commitextractor;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.commitextractor.model.Range;

public class DiffParser {
	private final static String DIFF_PATTERN = "(?m)^@@ -(?<fromFileStart>\\d+),(?<fromFileLength>\\d+) \\+(?<toFileStart>\\d+),(?<toFileLength>\\d+) @@"; 
	
	public enum ParseMode{
		TO_FILE, FROM_FILE
	}
	
	public static List<Range> parse(String patch, ParseMode MODE){
		List<Range> fileRanges = new LinkedList<Range>(); 
		String start_pattern, length_pattern;
		
		switch(MODE) {
			case TO_FILE:
				start_pattern = "toFileStart";
				length_pattern = "toFileLength";
				break;
			case FROM_FILE:
				start_pattern = "fromFileStart";
				length_pattern = "fromFileLength";
				break;
			default:
				start_pattern = "fromFileStart";
				length_pattern = "fromFileLength";
		}
		
		Pattern p = Pattern.compile(DIFF_PATTERN);
		Matcher m = p.matcher(patch);
		while(m.find()) {
			int start = Integer.parseInt(m.group(start_pattern));
			int length = Integer.parseInt(m.group(length_pattern));
			fileRanges.add(new Range(start, start + length));
		}
		
		return fileRanges;
	}
}
package edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ScreenScrapeComparePattern {		
	LABELLED_NUMBER(
			"The best matching label value, followed by a number (all digits, no spaces)",
			"Subaward ID: 12345678"),
	LABELLED_WORD(
			"The best matching label value, followed by a single word (all alpha, no spaces)",
			"Alpha ID: ABCdef"),
	LABELLED_BLOCK(
			"The best matching label value, followed by the first block of characters (all alpha-numeric, no spaces)",
			"AlphaNum ID: T3490LF");

	private String description;
	private String example;
	
	private ScreenScrapeComparePattern(String description, String example) {
		this.description = description;
		this.example = example;
	}
	
	public String getDescription() {
		return description;
	}		
	public String getExample() {
		return example;
	}
	public String getRegex(String replace) {
		// Match the label with all reserved regex operators escaped.
		String label = replace.replaceAll("([\\[\\]\\(\\)\\-\\^\\$\\.\\{\\}\\*\\?\\\\])", "\\\\$1");
		// Match a colon or hyphen padded by any amount of spaces or tabs, or just spaces and/or tabs.
		String separator = "[\\x20\\t]*[:\\-]?[\\x20\\t]*";
		// Match the value as indicated by the enum type
		String value = null;
		switch(this) {
		case LABELLED_BLOCK: value = "[\\dA-Za-z]+"; break;
		case LABELLED_NUMBER: value = "\\d+"; break;
		case LABELLED_WORD: value = "[A-Za-z]+"; break;
		}
		
		return label + separator + value;
	}
	public Pattern getPattern(String label) {
		return Pattern.compile(getRegex(label));
	}
	public Matcher getMatcher(String text, String label) {
		return getPattern(label).matcher(text);
	}
	public List<String> getMatches(String text, String label) {
		Matcher m = getMatcher(text, label);
		List<String> matches = new ArrayList<String>();
		while(m.find()) {
			matches.add(m.group());
		}
		return matches;
	}
}
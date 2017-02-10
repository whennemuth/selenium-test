package edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import edu.bu.ist.apps.kualiautomation.services.automate.locate.label.ComparableLabel;

/**
 * This class represents the innerText of an html element, which includes the innerText of all its descendent elements.
 * One of more occurrences of a particular combined label and value substring exist within the text, where the label
 * is a fixed string and the value matches a pattern.
 * An instance of this class is comparable because the innerText values are the result of a search and their are
 * criteria for evaluating one match better than another.
 * For example, considering the following two html elements:
 * 
 * <p><pre>
 * <b>1) html:</b>
 *     {@code <div> outer text 
 *         <div>
 *             <span>mylabel</span>
 *             <span>myvalue</span>
 *         </div>
 *         more outer text
 *     </div>}  
 *    <b>innerText:</b>
 *     {@code "outer text mylabel myvalue more outer text:}
 * 
 * <b>2) html:</b>
 *     {@code <span>mylabel</span> <span>myvalue</span> }
 *    <b>innerText:</b>
 *     {@code "mylabel myvalue"}
 * </pre></p>
 * 
 * bother the innerText values contain the {@code "mylabel myvalue"} string, but descendents are preferred so as to limit
 * extraneous content, and example 2 html could be the innerHTML of example 1 html and is a descendent, and would win out
 * in a comparison between the two. Other criteria also apply, but this is the most common one. 
 * 
 * @author wrh
 *
 */
public class ComparableScreenScrape extends ComparableLabel {

	private ScreenScrapeComparePattern pattern;
	private String value;
	private String cleanlabel;
	private String cleantext;
	private List<String> matches = new ArrayList<String>();
	
	/** Temporary local variable for another instance being comparedTo (remains null outside of customCompareTo()) */
	private ComparableScreenScrape other;
	
	/**
	 * Restrict this super constructor
	 */
	private ComparableScreenScrape(String label, String text, boolean useDefaultMethodIfIndeterminate) {
		super(label, text, useDefaultMethodIfIndeterminate);
	}

	/**
	 * Restrict this super constructor
	 */
	private ComparableScreenScrape(WebElement webElement, String label, String text,
			boolean useDefaultMethodIfIndeterminate) {
		super(webElement, label, text, useDefaultMethodIfIndeterminate);
	}

	/**
	 * Constructor
	 * @param parms
	 */
	public ComparableScreenScrape(ComparableParameters parms) {
		
		super(parms.getLabel(), parms.getText(), parms.isUseDefaultMethodIfIndeterminate());
		
		this.webElement = parms.getWebElement();
		this.pattern = parms.getPattern();
		
		this.cleanlabel = cleanLines(getRawLabel());
		this.cleantext = cleanLines(getRawText());
	}

	@Override
	protected int customCompareTo(ComparableLabel otherComparable) {
		
		try {
			other = (ComparableScreenScrape) otherComparable;
					
			qualify();
			
			if(this.disqualified && other.disqualified)
				return ITS_A_DRAW;
			
			if(this.disqualified)
				return OTHER_LABEL_IS_BETTER;
			
			if(other.disqualified)
				return THIS_LABEL_IS_BETTER;
			
			if(cleantext.equals(other.cleantext))
				return ITS_A_DRAW;
			
			if(matches.size() == other.matches.size()) {				
				return ITS_A_DRAW;
			}
			else {
				return matches.size() < other.matches.size() ? 
						THIS_LABEL_IS_BETTER : // Probably a descendent of other
						OTHER_LABEL_IS_BETTER; // Probably an ancestor of other
			}
		} 
		finally {
			other = null;
		}
	}

	/**
	 * Replace in a string all multiple consecutive occurrences of spaces and tabs with a single space, but not returns.
	 * 
	 * @param s
	 * @return
	 */
	private String cleanLines(String lines) {
		return lines.trim().replaceAll("[\\x20\\t]+", " ");
	}
	
	/**
	 * Determine if the label match qualifies with the a correctly matching value string immediately to its right. 
	 */
	private void qualify() {
		if(cleanlabel != null)
			return; // Has already run once.
		
		if(!cleanlabel.equals(other.cleanlabel)) {
			// If this error is thrown, then it means a programming error has been made.
			throw new IllegalStateException("compareTo() method called against two ComparableScreenScrapeLabel "
					+ "instances with unequal labels");
		}
		
		if(!cleantext.contains(cleanlabel)) {
			// Should never happen as a regex found the label, so it must be contained.
			disqualified = true;
			return;
		}
		
		// Up to now we know the screenScrape was a match because the label was found by xpath.
		// However if the characters that immediately follow it do not match what is expected, then disqualify.
		matches = pattern.getMatches(cleantext, cleanlabel);
		if(matches.isEmpty()) {
			disqualified = true;
		}
		
		other.qualify();
	}
	
	/**
	 * Get a "clone", but with a different text value set.
	 * @param newText
	 * @return
	 */
	public ComparableScreenScrape changeText(String newText) {
		return new ComparableScreenScrape(
			new ComparableParameters()
				.setLabel(getLabel())
				.setPattern(pattern)
				.setText(newText)
				.setUseDefaultMethodIfIndeterminate(useDefaultMethodIfIndeterminate)
				.setWebElement(webElement)
		);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

package edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape;

import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape.ComparableScreenScrape;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.screenscrape.ScreenScrapeComparePattern;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComparableScreenScrapeTest {

	private ComparableScreenScrape comparable;
	
	private static String[] sortable1 = new String[] {
			
	};
	
	@Test
	public void testIllegalState() {
		try {
			ComparableParameters parms1 = new ComparableParameters()
					.setLabel("label1")
					.setText("label1:value1")
					.setUseDefaultMethodIfIndeterminate(false)
					.setPattern(ScreenScrapeComparePattern.LABELLED_NUMBER);
			
			ComparableParameters parms2 = new ComparableParameters()
					.setLabel("label2")
					.setText(parms1.getLabel())
					.setUseDefaultMethodIfIndeterminate(parms1.isUseDefaultMethodIfIndeterminate())
					.setPattern(parms1.getPattern());
			
			(new ComparableScreenScrape(parms1)).compareTo(new ComparableScreenScrape(parms2));
		}
		catch(IllegalStateException e) { 
			return; // The test passes.
		}
		
		fail("Expected IllegalStateException for comparison of two ComparableScreenScrapeLabel "
				+ "instances with unequal labels");
	}
		
	@Test
	public void testLabelledNumber() {
		
		// Test equivalency scenarios
		(new ComparableObj(ScreenScrapeComparePattern.LABELLED_NUMBER, "mylabel", 
				"mylabel: myvalue")).assertIsEquivalentTo("mylabelmyvalue");
		
		(new ComparableObj(ScreenScrapeComparePattern.LABELLED_NUMBER, "mylabel", 
				"mylabel: 123")).assertIsEquivalentTo("mylabel123");
		
		(new ComparableObj(ScreenScrapeComparePattern.LABELLED_NUMBER, "mylabel", 
				"mylabel:123")).assertIsEquivalentTo("mylabel:456");
		
		(new ComparableObj(ScreenScrapeComparePattern.LABELLED_NUMBER, "mylabel", 
				"abc mylabel 123 def")).assertIsEquivalentTo("ghi mylabel 456 jkl");
		
		// Test better and worse scenarios
		// RESUME NEXT
	}

	/**
	 * Utility class for asserting compareTo() results. "Better" items would be occur before
	 * "Worse" items in a sorted list of comparable items.
	 * 
	 * @author wrh
	 *
	 */
	private class ComparableObj {
		private ComparableScreenScrape thisComparable;
		public ComparableObj(ScreenScrapeComparePattern pattern, String label, String text) {
			this.thisComparable = new ComparableScreenScrape(
					new ComparableParameters()
					.setPattern(pattern)
					.setLabel(label)
					.setText(text)
					.setUseDefaultMethodIfIndeterminate(false));
		}
		private int compare(String otherText) {
			ComparableScreenScrape otherComparable = thisComparable.changeText(otherText);
			return thisComparable.compareTo(otherComparable);
		}
		public void assertIsEquivalentTo(String otherText) {
			assertEquals(0, compare(otherText));
		}
		public void assertIsBetterMatchThan(String otherText) {
			ComparableScreenScrape otherComparable = thisComparable.changeText(otherText);
			assertEquals(-1, compare(otherText));
		}
		public void assertIsWorseMatchThan(String otherText) {
			ComparableScreenScrape otherComparable = thisComparable.changeText(otherText);
			assertEquals(1, compare(otherText));
		}
	}
}

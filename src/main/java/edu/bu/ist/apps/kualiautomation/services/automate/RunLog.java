package edu.bu.ist.apps.kualiautomation.services.automate;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.util.Utils;

/**
 * This class provides one or more log entries for the attempt to locate and assign a value to one element
 * on a web page. The entries are written to an OutputStream provided by client code.
 * 
 * @author wrh
 *
 */
public class RunLog {
	
	private Date start;
	private Date stop;
	private List<String> entries = new ArrayList<String>();

	public RunLog() {
		start = new Date(System.currentTimeMillis());
	}
	
	public void log(Element element, LabelAndValue lv) {
		StringBuilder s = new StringBuilder();
		
		printElement(s, element, lv);
		
		if(element.getElementType().acceptsKeystrokes()) {
			s.insert(0, "Applying to element ");
			s.append("] \r\n   value: \"")
			.append(lv.getValue())
			.append("\"");
		}
		else {
			s.insert(0, "Clicking element ");
			s.append("]");
		}

		entries.add(s.toString());
	}

	public void elementNotFound(LabelAndValue lv) {
		StringBuilder s = new StringBuilder();
		s.append("Cannot find element ");
		printElement(s, null, lv);
		entries.add(s.toString());
	}

	public void multipleElementCandidates(LabelAndValue lv, List<Element> elements) {
		StringBuilder s = new StringBuilder();
		s.append("More than one element was found for the specified search criteria ");
		for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {
			Element element = iterator.next();
			printElement(s, element, lv);
			s.append("]");
			if(iterator.hasNext()) {
				s.append(", ");
			}
		}
				
		entries.add(s.toString());		
	}

	public void valueApplicationError(LabelAndValue lv, Element element) {
		StringBuilder s = new StringBuilder();
		s.append("Error encountered for element ");
		printElement(s, null, lv);
		s.append("]");
		entries.add(s.toString());
	}

	private void printElement(StringBuilder s, Element element, LabelAndValue lv) {
		s.append("\r\n   ");
		if(element != null)
			s.append("[").append(element.getWebElement().getTagName())
			.append(" type=\"")
			.append(element.getWebElement().getAttribute("type"))
			.append("\", text=\"")
			.append(element.getWebElement().getText())
			.append("\", value=\"")
			.append(element.getWebElement().getAttribute("value"))
			.append("\" ");
		
		s.append("(criteria: label=\"")
		.append(lv.getLabel())
		.append("\"");
		
		if(!Utils.isEmpty(lv.getIdentifier())) {
			s.append(", identifier=\"")
				.append(lv.getIdentifier())
				.append("\"");
		}	
		s.append(")");
	}
	
	private long getDuration() {
		return (stop.getTime() - start.getTime())/1000;
	}
	
	public void printResults(OutputStream out) {
		stop = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("HH:mm:ss");
		PrintWriter pw = null;
		
		try {
			pw = new PrintWriter(out);
			pw.println("Cycle Log Results:");
			pw.println("---------------------------------");
			pw.println("Started: " + sdf.format(start));
			pw.println("Ended: " + sdf.format(stop) + " (" + String.valueOf(getDuration()) + " seconds)");
			
			for(String entry : entries) {
				pw.println(entry);
			}
		} 
		finally {
			if(pw != null) {
				pw.close();
			}
		}

	}
	
}
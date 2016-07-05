package edu.bu.ist.apps.kualiautomation.services.automate;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.util.Utils;

public class RunLog {
	
	private Date start;
	private Date stop;
	private List<String> entries = new ArrayList<String>();

	public void log(Element element, LabelAndValue lv) {
		checkStart();
		StringBuilder s = new StringBuilder();
		
		s.append(element.getWebElement().getTagName())
			.append(" (label=\"")
			.append(lv.getLabel())
			.append("\"");
			
		if(!Utils.isEmpty(lv.getIdentifier())) {
			s.append(", identifier=\"")
				.append(lv.getIdentifier())
				.append("\"");
		}
		
		if(element.getElementType().acceptsKeystrokes()) {
			s.insert(0, "Applying to element [");
			s.append("] value: \"")
			.append(lv.getValue())
			.append("\"");
		}
		else {
			s.insert(0, "Clicking element [");
			s.append("]");
		}

		entries.add(s.toString());
	}

	public void elementNotFound(LabelAndValue lv) {
		checkStart();
	}

	public void multipleElementCandidates(LabelAndValue lv, List<Element> elements) {
		checkStart();
		
	}

	public void valueApplicationError(LabelAndValue lv, Element element) {
		checkStart();
		
	}

	public void printResults(OutputStream out) {
		checkStart();
		stop = new Date(System.currentTimeMillis());
// RESUME NEXT: Finish this.		
	}

	private void checkStart() {
		if(start == null)
			start = new Date(System.currentTimeMillis());
	}
	
	
}

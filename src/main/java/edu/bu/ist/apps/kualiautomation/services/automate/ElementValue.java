package edu.bu.ist.apps.kualiautomation.services.automate;

import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;

public class ElementValue {

	private LabelAndValue lv;
	
	public ElementValue(LabelAndValue lv) {
		this.lv = lv;
	}

	public boolean apply(Element element) {
		// TODO Auto-generated method stub
		return true;
	}

}

package edu.bu.ist.apps.kualiautomation.services.automate;

import java.util.List;

import org.openqa.selenium.WebDriver;

import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.entity.Module;
import edu.bu.ist.apps.kualiautomation.entity.Suite;
import edu.bu.ist.apps.kualiautomation.entity.Tab;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.LocatorRunner;

/**
 * This class processes a cycle. Every suite, module, and tab is processed in order involving navigating to the
 * correct location, finding each element and applying a value to each or clicking. A log is returned detailing
 * the actions taken and any error encountered.
 * 
 * @author wrh
 *
 */
public class CycleRunner {

	private WebDriver driver;
	private Cycle cycle;
	private RunLog runlog;
	
	@SuppressWarnings("unused")
	private CycleRunner() { /* Restrict private constructor */ }
	
	public CycleRunner(Session session) {
		this(session.getDriver(), session.getCycle());
	}
	
	public CycleRunner(WebDriver driver, Cycle cycle) {
		this.driver = driver;
		this.cycle = cycle;
	}
	
	public RunLog run() {		
		System.out.println("Processing Cycle: " + cycle.getName());
		runlog = new RunLog();
		for(Suite suite : cycle.getSuites()) {
			System.out.println("Processing Suite: " + suite.getName());
			for(Module module : suite.getModules()) {
				if(!module.isBlank()) {
					System.out.println("Processing Page/Module " + String.valueOf(module.getSequence()));
				}
				for(Tab tab : module.getTabs()) {
					if(!tab.isBlank()) {
						System.out.println("Clicking tab: " + tab.getName());
					}
					for(LabelAndValue lv : tab.getLabelAndValues()) {
						ElementType elementType = ElementType.valueOf(lv.getElementType());
						LocatorRunner locator = new LocatorRunner(driver, elementType, lv.getLabel(), lv.getIdentifier());
						List<Element> elements = locator.run(true);
						processElement(lv, elements);
					}
				}
			}
		}
		
		return runlog;
	}
	
	/**
	 * Apply the provided value to the element or log a reason why this cannot be done,
	 * such as more than one element found, or none at all, or some unspecified error.
	 * 
	 * @param lv
	 * @param elements
	 */
	private boolean processElement(LabelAndValue lv, List<Element> elements) {
		if(elements.isEmpty()) {
			runlog.elementNotFound(lv);
			return false;
		}
		else if(elements.size() > 1) {
			runlog.multipleElementCandidates(lv, elements);
			return false;
		}

		Element element = elements.get(0);
		ElementValue val = new ElementValue(lv);
		if(val.apply(element)) {
			runlog.log(element, lv);
		}
		else {
			runlog.valueApplicationError(lv, element);
		}
		
		return true;
	}	
}

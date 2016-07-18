package edu.bu.ist.apps.kualiautomation.services.automate;

import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.WebDriver;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment;
import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.entity.LabelAndValue;
import edu.bu.ist.apps.kualiautomation.entity.Module;
import edu.bu.ist.apps.kualiautomation.entity.Suite;
import edu.bu.ist.apps.kualiautomation.entity.Tab;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementValue;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.LocatorRunner;
import edu.bu.ist.apps.kualiautomation.services.config.ConfigTestingDefaults;

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
		
		outerloop:
		for(Suite suite : cycle.getSuites()) {
			System.out.println("Processing Suite: " + suite.getName());
//			for (Iterator<Module> moduleIterator = suite.getModules().iterator(); moduleIterator.hasNext();) {
//				Module module =  moduleIterator.next();
//				if(!module.isBlank()) {
//					System.out.println("Processing Page/Module " + String.valueOf(module.getSequence()));
//				}
//				for(Tab tab : module.getTabs()) {
//					if(!tab.isBlank()) {
//						System.out.println("Clicking tab: " + tab.getName());
//					}
					for(LabelAndValue lv : suite.getLabelAndValues()) {
						ElementType elementType = ElementType.valueOf(lv.getElementType());
						LocatorRunner locator = new LocatorRunner(driver, elementType, lv.getLabel(), lv.getIdentifier());
						List<Element> elements = locator.run(true);
						if(elementLocated(lv, elements)) {
							boolean endOfPage = moduleIterator.hasNext();
							if(!applyElementValue(lv, elements.get(0), endOfPage)) {
								driver.switchTo().defaultContent();
								break outerloop;
							}
						}
						else {
							driver.switchTo().defaultContent();
							break outerloop;
						}
						driver.switchTo().defaultContent();
					}
//				}
//			}
		}
		
		return runlog;
	}

	/**
	 * Require that one element was found from the location attempt and make a log entry if not so.
	 * 
	 * @param lv
	 * @param elements
	 */
	private boolean elementLocated(LabelAndValue lv, List<Element> elements) {
		if(elements == null || elements.isEmpty()) {
			runlog.elementNotFound(lv);
			return false;
		}
		else if(elements.size() > 1) {
			runlog.multipleElementCandidates(lv, elements);
			return false;
		}
		return true;
	}
	
	/**
	 * Apply the provided value to the element or log any error in the attempt.
	 * 
	 * @param lv
	 * @param elements
	 */
	private boolean applyElementValue(LabelAndValue lv, Element element, boolean navigate) {
		ElementValue val = new ElementValue(driver, lv);
		runlog.log(element, lv);
		if(!val.applyTo(element, navigate)) {
			runlog.valueApplicationError(lv, element);
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		
		// Create the configuration
		Config config = new Config();
		ConfigTestingDefaults.populate(config);
		
		// Change the default environment
		ConfigEnvironment testDriveEnv = new ConfigEnvironment();
		testDriveEnv.setId(999);
		testDriveEnv.setName("TEST-DRIVE");
		testDriveEnv.setUrl("https://res-demo2.kuali.co/kc-dev/kr-login/login?viewId=DummyLoginView&returnLocation=%2Fkc-krad%2FlandingPage&formKey=68ba02c6-3587-4b81-a4c9-d8eb465eaa01&cacheKey=7ft9p0xr31nwqntioww2pa");
		config.addConfigEnvironment(testDriveEnv);
		config.setCurrentEnvironment(testDriveEnv);
		
		// Create 2nd login screen username data
		LabelAndValue lv1 = new LabelAndValue();
		lv1.setElementType(ElementType.TEXTBOX.name());
		lv1.setLabel("username / email");
		lv1.setValue("quickstart");
		
		// Create 2nd login screen username data
		LabelAndValue lv2 = new LabelAndValue();
		lv2.setElementType(ElementType.TEXTBOX.name());
		lv2.setLabel("password");
		lv2.setValue("password");
		
		// Create 2nd login screen login button
		LabelAndValue lv3 = new LabelAndValue();
		lv3.setElementType(ElementType.BUTTON.name());
		lv3.setLabel("sign in");
		
		Suite suite = new Suite();
		suite.addLabelAndValue(lv1);
		suite.addLabelAndValue(lv2);
		suite.addLabelAndValue(lv3);
		
		// Create the cycle to run and add the suite to it
		Cycle cycle = new Cycle();
		KerberosLoginParms parms = new KerberosLoginParms();
		parms.setUsername("quickstart");
		parms.setPassword(null);
		cycle.setKerberosLoginParms(parms);
		cycle.addSuite(suite);
		
		// Create and run the session
		Session session = new Session(config, cycle, false);
		Thread thread = new Thread(session);
		thread.start();
		System.out.println("thread started...");

	}
}

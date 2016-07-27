package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import edu.bu.ist.apps.kualiautomation.entity.ConfigShortcut;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.config.EmbeddedJettyStaticServer;

@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortcutElementLocatorTest1 {
	
	private static EmbeddedJettyStaticServer server;
	private static Map<String, String> handlers = new HashMap<String, String>();
	private static boolean headless;
	private static boolean windows;
	
	private WebDriver driver;
	private ShortcutElementLocator locator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		headless = !Boolean.valueOf(System.getenv("showbrowser"));
		windows = Boolean.valueOf(System.getenv("windows"));
		
		handlers.put("shortcut-page1", "ShortcutTestPage1.htm");
		handlers.put("shortcut-page2", "ShortcutTestPage2.htm");
		
		server = new EmbeddedJettyStaticServer();
		server.start(handlers);		
	}

	@Before
	public void setUpBefore() {
		if(headless) {
			if(windows) {
				DesiredCapabilities capabilities = DesiredCapabilities.firefox();
				capabilities.setCapability("version", "latest");
				capabilities.setCapability("platform", Platform.WINDOWS);
				capabilities.setCapability("name", "Testing Selenium");	
				capabilities.setJavascriptEnabled(true);
				driver = new HtmlUnitDriver(capabilities);
			}
			else {
				driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_38, true);
			}
		}
		else {
			driver = new FirefoxDriver();
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		server.stop();
		System.out.println("Jetty server stopped");
	}

	@After
	public void tearDownAfter() {
		driver.quit();		
	}
	
	@Test
	public void test1() throws CloneNotSupportedException {
		ConfigShortcut shortcut = null;
		LocateResultAssertion asserter = null;
		List<Element> results = null;
		
		// 1)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingA 1", "headingA 2", "headingA 3", "target 1"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		results = asserter.findAndAssertElements();
		assertEquals("target 1", results.get(0).getWebElement().getText());
		
		// 2)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingA 1", "headingA 2", "headingA 3", "icon-plus"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.HOTSPOT);
		asserter.setNumResults(1);
		results = asserter.findAndAssertElements();
		assertEquals("u10mjulv", results.get(0).getWebElement().getAttribute("id"));
		
		// 3)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingA 1", "headingA 2", "headingA 3", "icon-search"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.HOTSPOT);
		asserter.setNumResults(1);
		results = asserter.findAndAssertElements();
		assertEquals("u11mjulv", results.get(0).getWebElement().getAttribute("id"));
		
		// 4)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingA 1", "headingA 2", "headingA 3", "uif-actionLink"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.HOTSPOT);
		asserter.setNumResults(2);
		asserter.findAndAssertElements();
	}
	
	
	@Test
	public void test2() throws CloneNotSupportedException {
		ConfigShortcut shortcut = null;
		LocateResultAssertion asserter = null;
		List<Element> results = null;
		
		// 1)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setIdentifier(null);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingB 1", "headingB 2", "headingB 3", "target 2"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.BUTTON);
		asserter.setNumResults(1);
		results = asserter.findAndAssertElements();
		assertEquals("target 2", results.get(0).getWebElement().getAttribute("value"));
		
		// 2)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setIdentifier(null);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingB 1", "headingB 2", "headingB 3", "icon-plus"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.BUTTON);
		asserter.setNumResults(1);
		results = asserter.findAndAssertElements();
		assertEquals("u10mjulv", results.get(0).getWebElement().getAttribute("id"));
		
		// 3)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setIdentifier(null);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingB 1", "headingB 2", "headingB 3", "u10mjulv"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.BUTTON);
		asserter.setNumResults(1);
		results = asserter.findAndAssertElements();
		assertEquals("u10mjulv", results.get(0).getWebElement().getAttribute("id"));
	}
	
	
	@Test
	public void test3() throws CloneNotSupportedException {
		ConfigShortcut shortcut = null;
		LocateResultAssertion asserter = null;
		
		// 1)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setIdentifier(null);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingC 1", "headingC 2", "headingC 3", "target 3"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(0);
		asserter.findAndAssertElements();
		
		// 2)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setIdentifier(null);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingC 1", "headingC 2", "headingC 3", "icon-plus"
		});		
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.HOTSPOT);
		asserter.setNumResults(0);
		asserter.findAndAssertElements();
	}
	
	
	@Test
	public void test4() throws CloneNotSupportedException {
		ConfigShortcut shortcut = null;
		LocateResultAssertion asserter = null;
		
		// 1)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setIdentifier(null);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingD 1", "headingD 2", "headingD 3", "target 4"
		});	
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.BUTTON);
		asserter.setNumResults(0);
		asserter.findAndAssertElements();
		
		// 2)
		shortcut = new ConfigShortcut();
		shortcut.setNavigates(true);
		shortcut.setIdentifier(null);
		shortcut.setLabelHierarchyParts(new String[] {
			"headingD 1", "headingD 2", "headingD 3", "icon-plus"
		});	
		locator = new ShortcutElementLocator(driver, shortcut);
		asserter = new LocateResultAssertion(locator);
		asserter.setUrl("http://localhost:8080/shortcut-page1");
		asserter.setElementType(ElementType.HOTSPOT);
		asserter.setNumResults(0);
		asserter.findAndAssertElements();
	}	
	
}

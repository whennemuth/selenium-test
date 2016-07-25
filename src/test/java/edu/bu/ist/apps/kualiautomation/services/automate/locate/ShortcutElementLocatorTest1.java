package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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

	private @Mock ConfigShortcut shortcut;

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
	public void test() {
		when(shortcut.getLabelHierarchyParts()).thenReturn(new String[]{});
		when(shortcut.getElementType()).thenReturn(ElementType.HYPERLINK.name());
		when(shortcut.isNavigates()).thenReturn(true);
		when(shortcut.getIdentifier()).thenReturn(null);
		
		locator = new ShortcutElementLocator(driver, shortcut);
		String url = "http://localhost:8080/shortcut-page1";
		LocateResultAssertion asserter = new LocateResultAssertion(locator);
		asserter.setUrl(url);
		asserter.setElementType(ElementType.HYPERLINK);
		asserter.setNumResults(1);
		asserter.findAndAssertElements();
	}

}

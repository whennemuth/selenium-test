package edu.bu.ist.apps.kualiautomation.services.automate;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

import edu.bu.ist.apps.kualiautomation.util.Utils;

/**
 * The purpose of this class is to enumerate the main browsers and provide functionality to access their drivers
 * from root directory the application is running from.
 * 
 * @author wrh
 *
 */
public enum Driver {

	FIREFOX(null, org.openqa.selenium.firefox.FirefoxDriver.class), 
	CHROME("webdriver.chrome.driver", org.openqa.selenium.chrome.ChromeDriver.class), 
	IE("", null), 
	SAFARI("", null), 
	HEADLESS("", CustomHtmlUnitDriver.class);

	public static final String DRIVER_SYSTEM_PROPERTY = "automation.app.browser.driver";
	public static final Driver DEFAULT_DRIVER = FIREFOX;
	
	private String systemProperty;
	private Class<? extends WebDriver> driverClass;	
	private File root = null;
	private File driverFile = null;
		
	private Driver(String systemProperty, Class<? extends WebDriver> driverClass) {
		this.systemProperty = systemProperty;
		this.driverClass = driverClass;
	}
	
	/**
	 * @return An instance of the selected web driver as an implementation of WebDriver.
	 */
	public WebDriver getDriver() {
		
		if(findDriver()) {			
			driverFile = getDriverFile();
		}

		WebDriver driver = null;
		try {
			switch(this) {
			case FIREFOX:
				driver = new FirefoxDriver(); // Selenium bundles a firefox driver in its own library.
				break;
			case HEADLESS:
				driver = new Driver.CustomHtmlUnitDriver(BrowserVersion.FIREFOX_38, true);
				break;
			default:
				if(driverClass != null && driverFile != null) {
					driver = (WebDriver) driverClass.newInstance();
				}
				break;			
			}
		} catch (InstantiationException e) {
			e.printStackTrace(System.out);
		} catch (IllegalAccessException e) {
			e.printStackTrace(System.out);
		}
		
		return driver;
	}
	
	/**
	 * @return The driver exe file if it exists.
	 */
	private File getDriverFile() {
		File f = null;
		try {
			root = Utils.getRootDirectory();
			File[] drivers = root.listFiles(new FilenameFilter() {
				@Override public boolean accept(File dir, String name) {
					if(name.toUpperCase().contains(name()) && name.endsWith(".exe")) 
						return true;
					return false;
				}
			});
			if(drivers.length == 0) {
				System.out.println("Cannot find any web drivers at for " + name() + " at: " + root.getAbsolutePath());
			}
			else {
				if(drivers.length > 1) {
					System.out.println("Found more than one web driver for " + name() + ":\n");
					for(int i=0; i<drivers.length; i++) {
						System.out.println(drivers[i].getAbsolutePath() + "\n");
					}
				}
				System.out.println("Using web driver: " + drivers[0].getAbsolutePath());
				f = drivers[0];
				
				System.setProperty(systemProperty, f.getAbsolutePath());
			}
		} 
		catch (Exception e) {
			if(root == null)
				System.out.println("Problem finding web driver directory");
			else if(f == null)
				System.out.println("Problem finding web driver at: " + root.getAbsolutePath());
			else
				System.out.println("Problem loading web driver: " + f.getAbsolutePath());
			
			e.printStackTrace(System.out);
		}
		return f;
	}
	
	/**
	 * @return Is there enough information to start a search for the driver?
	 */
	private boolean findDriver() {
		return !Utils.isEmpty(systemProperty) && driverClass != null;
	}
	
	public static List<Driver> getAvailableDrivers() {
		List<Driver> drivers = new ArrayList<Driver>();
		for(Driver driver : Driver.values()) {
			switch(driver) {
			case FIREFOX:
				drivers.add(driver);
				break;
			case HEADLESS:
				break;
			default:
				if(driver.getDriverFile() != null) {
					drivers.add(driver);
				}
				break;			
			}			
		}
		return drivers;
	}
	
	/**
	 * Allow for enabled javascript, but do not throw exceptions.
	 * @author wrh
	 *
	 */
	public static class CustomHtmlUnitDriver extends HtmlUnitDriver {
		public CustomHtmlUnitDriver(boolean javascriptEnabled) {
			super(javascriptEnabled);
		}
		public CustomHtmlUnitDriver(DesiredCapabilities capabilities) {
			super(capabilities);
		}
		public CustomHtmlUnitDriver(BrowserVersion firefox38, boolean javascriptEnabled) {
			super(firefox38, javascriptEnabled);
		}
		@Override protected WebClient modifyWebClient(WebClient client) {
			WebClient modifiedClient = super.modifyWebClient(client);
			modifiedClient.getOptions().setThrowExceptionOnScriptError(false);
			return modifiedClient;
		}
	}
}

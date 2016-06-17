package edu.bu.ist.apps.kualiautomation.services.automate;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.services.config.ConfigTestingDefaults;
import edu.bu.ist.apps.kualiautomation.services.element.BasicLocatorImpl;
import edu.bu.ist.apps.kualiautomation.services.element.Element;
import edu.bu.ist.apps.kualiautomation.services.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.element.Locator;

public class KerberosLogin {
	
	private Session session;
	private String username;
	private String password;
	private String loginUrl;
	private WebDriver driver;
	private long started;
	private Locator locator;
	
	public KerberosLogin(Session session) {
		this.session = session;
		this.username = session.getCycle().getKerberosUsername();
		this.password = session.getCycle().getKerberosPassword();
		this.loginUrl = session.getConfig().getCurrentEnvironment().getUrl();
		this.driver = session.getDriver();
		this.locator = new BasicLocatorImpl(driver);
	}

	public boolean login() {
		
		driver.get(loginUrl);
		
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		ExpectedCondition<Boolean> loginSucceeds = new ExpectedCondition<Boolean>() {			  
			public Boolean apply(WebDriver drv) {
				
				if(started == 0) {
					System.out.println("Going to login page...");
					Element usrname = locator.locate("", ElementType.TEXTBOX);
					Element psswd = locator.locate("", ElementType.TEXTBOX);
					Element submit = locator.locate("", ElementType.BUTTON);
					
					usrname.getWebElement().sendKeys(username);
					if(psswd.isVisible()) {
						psswd.getWebElement().sendKeys(password);
					}
					submit.getWebElement().click();
					
//					WebElement usrname = drv.findElement(By.id("username"));
//					WebElement psswd = drv.findElement(By.id("password"));
//					
					// send the values to each text entry input. After filling user and pwd, submit is clicked.
//					usrname.sendKeys(username);
//					psswd.sendKeys(password);
//					drv.findElement(By.xpath("//input[@class='input-submit']")).click();
					
					started = System.currentTimeMillis();
					return false;
				}
				
				return success();
			}
		};
		
		try {
			wait.until(loginSucceeds);
		} 
		catch (TimeoutException e) {
			return false;
		}

		return true;
	}

	private boolean success() {
		return !driver.getCurrentUrl().contains("weblogin");
	}
	
	public static void main(String[] args) {
		
		// Create the configuration
		Config config = new Config();
		ConfigTestingDefaults.populate(config);
		
		// Create the cycle to run
		Cycle cycle = new Cycle();
		cycle.setKerberosUsername("wrh");
		cycle.setKerberosPassword("warHEN123!@#");
		
		// Create and run the session
		Session session = new Session(config, cycle);
		Thread thread = new Thread(session);
		thread.start();
		System.out.println("thread started...");
	}
}

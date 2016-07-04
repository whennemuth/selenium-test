package edu.bu.ist.apps.kualiautomation.services.automate;

import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.services.automate.element.Element;
import edu.bu.ist.apps.kualiautomation.services.automate.element.ElementType;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.LabelledElementLocator;
import edu.bu.ist.apps.kualiautomation.services.automate.locate.Locator;
import edu.bu.ist.apps.kualiautomation.services.config.ConfigTestingDefaults;

public class KerberosLogin {
	
	private Session session;
	private KerberosLoginParms loginParms;
	private String loginUrl;
	private WebDriver driver;
	private long submittedAt;
	private final Locator locator;
	private WebDriverWait wait;
	private int timeout;
	private Element username;
	private String failureMessage;
	
	public KerberosLogin(Session session, int timeout) {
		this.session = session;
		this.loginParms = session.getCycle().getKerberosLoginParms();
		this.loginUrl = session.getConfig().getCurrentEnvironment().getUrl();
		this.driver = session.getDriver();
		this.locator = new LabelledElementLocator(driver);
		this.timeout = timeout;
		this.wait = new WebDriverWait(driver, timeout);
	}

	public boolean login() {
		try {
			driver.get(loginUrl);
			
			wait.until(loginPageLoaded());
			
			typeAndSubmit();
			
			wait.until(arrivedAtNextPage());
		} 
		catch (TimeoutException e) {
			failureMessage = "Timed out (" + 
					String.valueOf(timeout) + " seconds) waiting for " + 
					(submittedAt == 0 ? "initial login page to load." : "login submission to succeed");
			return false;
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
			return false;
		}

		return true;
	}

	private void typeAndSubmit() throws Exception {
		System.out.println("Going to login page...");
		Element password = locator.locateFirst(ElementType.TEXTBOX, Arrays.asList(new String[]{
			loginParms.getPasswordLabel(),
			loginParms.getPasswordOtherIdentifier()
		}));
		Element submit = locator.locateFirst(ElementType.BUTTON, Arrays.asList(new String[]{
			loginParms.getSubmitButtonLabel()
		}));
		
		username.setValue(loginParms.getUsername());
		if(password != null && password.isInteractive()) {
			password.setValue(loginParms.getPassword());
		}
		submit.click();
		
		submittedAt = System.currentTimeMillis();
	}

	private ExpectedCondition<Boolean> loginPageLoaded() {
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {			  
			public Boolean apply(WebDriver drv) {
				if(locator.busy()) {
					System.out.println("busy finding " + loginParms.getUsernameLabel());
					return false;
				}
				
				try {
					System.out.println("Start search for " + loginParms.getUsernameLabel());
					username = locator.locateFirst(ElementType.TEXTBOX, Arrays.asList(new String[]{
							loginParms.getUsernameLabel(),
							loginParms.getUsernameOtherIdentifier()
						}));
					
					return usernameLocated();
				} 
				catch (Exception e) {
					// The apply method will ignore checked exceptions, so wrap them in RuntimeExceptions
					throw new RuntimeException(e);
				}
			}
		};
		return condition;
	}
	
	private Boolean waitingForNextPage = false;
	private ExpectedCondition<Boolean> arrivedAtNextPage() {		
		ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {			  
			public Boolean apply(WebDriver drv) {
				if(waitingForNextPage) {
					System.out.println("busy finding next page");
					return false;
				}
				try {
					waitingForNextPage = true;
					username.getWebElement().findElements(By.id("does not matter"));
				} 
				catch (StaleElementReferenceException e) {
					if(!driver.findElements(By.tagName("html")).isEmpty()) {
						return true;
					}
				}
				catch(Exception e) {
					// The apply method will ignore checked exceptions, so wrap them in RuntimeExceptions
					throw new RuntimeException(e);
				}
				finally {
					waitingForNextPage = false;
				}
				return false;
			}
		};
		return condition;
	}
	
	private boolean usernameLocated() {
		boolean located = username.isInteractive() && username.getElementType().acceptsKeystrokes();
		StringBuilder s = new StringBuilder(loginParms.getUsernameLabel())
				.append(" ");
		if(!located)
			s.append("NOT ");
		s.append("located");
		System.out.println(s.toString());
		return located;
	}
	
	public boolean loginFailed() {
		return failureMessage != null;
	}
	
	public String getFailureMessage() {
		return failureMessage;
	}
	
	public static void main(String[] args) {
		
		// Create the configuration
		Config config = new Config();
		ConfigTestingDefaults.populate(config);
		
		// Create the cycle to run
		Cycle cycle = new Cycle();
		KerberosLoginParms parms = new KerberosLoginParms();
		parms.setUsername("quickstart");
		parms.setPassword(null);
		cycle.setKerberosLoginParms(parms);
		
		// Create and run the session
		Session session = new Session(config, cycle, false);
		Thread thread = new Thread(session);
		thread.start();
		System.out.println("thread started...");
	}
}

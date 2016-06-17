package edu.bu.ist.apps.kualiautomation.services.automate;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.services.config.ConfigDefaults;

public class Session implements Runnable {

	private Cycle cycle;
	private Config config;
	private WebDriver driver;
	
	public Session(Config config, Cycle cycle) {
		this.config = config;
		this.cycle = cycle;
	}
	
	@Override
	public void run() {
		
		try {
			if(config.isHeadless())
				driver = new HtmlUnitDriver();
			else
				driver = new FirefoxDriver();	
			
			if( login()) {
			
				work();
				
				reportResults();
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(driver != null) {
				driver.close();
				driver.quit();
			}
		}
	}

	private void reportResults() {
		System.out.println("Reporting results...");
	}

	private void work() {
		System.out.println("Working...");
	}

	private boolean login() throws Exception {
		if(new KerberosLogin(this).login()) {
			return true;
		}
		else {
			System.out.println("Login failed!");
			return false;
		}
	}

	public Cycle getCycle() {
		return cycle;
	}

	public Config getConfig() {
		return config;
	}

	public WebDriver getDriver() {
		return driver;
	}
	
	public static void main(String[] args) {
		Config config = new Config();
		ConfigDefaults.populate(config);
		Cycle cycle = new Cycle();
		Thread thread = new Thread(new Session(config, cycle));
		thread.start();
		System.out.println("thread started...");
	}
}

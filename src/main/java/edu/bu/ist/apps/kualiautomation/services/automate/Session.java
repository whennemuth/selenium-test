package edu.bu.ist.apps.kualiautomation.services.automate;

import java.io.OutputStream;

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
	private boolean terminate;
	private RunLog runLog;
	private OutputStream logOutput;
	
	@SuppressWarnings("unused")
	private Session() { /* Restrict private constructor */ }
	
	public Session(Config config, Cycle cycle, boolean terminate) {
		this.config = config;
		this.cycle = cycle;
		this.terminate = terminate;
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
			if(terminate()) {
				driver.close();
				driver.quit();
			}
		}
	}

	private void work() {
		System.out.println("Running cycle...");
		CycleRunner runner = new CycleRunner(this);
		runLog = runner.run();		
	}
	
	private void reportResults() {
		System.out.println("Reporting results...");
		if(logOutput == null)
			logOutput = System.out;
		runLog.printResults(logOutput);
	}

	private boolean login() throws Exception {
		KerberosLogin kerberos = new KerberosLogin(this, 10);
		if(kerberos.login()) {
			return true;
		}
		else {
			System.out.println(kerberos.getFailureMessage());
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
	
	public void setLogOutput(OutputStream logOutput) {
		this.logOutput = logOutput;
	}

	private boolean terminate() {
		if(driver == null)
			return false;
		if(config.isHeadless())
			return true;
		return terminate;
	}
	
	public static void main(String[] args) {
		Config config = new Config();
		ConfigDefaults.populate(config);
		Cycle cycle = new Cycle();
		Thread thread = new Thread(new Session(config, cycle, false));
		thread.start();
		System.out.println("thread started...");
	}
}

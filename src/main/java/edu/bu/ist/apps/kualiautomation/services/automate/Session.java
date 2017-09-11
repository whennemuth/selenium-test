package edu.bu.ist.apps.kualiautomation.services.automate;

import java.io.OutputStream;

import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import edu.bu.ist.apps.kualiautomation.entity.Config;
import edu.bu.ist.apps.kualiautomation.entity.ConfigEnvironment;
import edu.bu.ist.apps.kualiautomation.entity.Cycle;
import edu.bu.ist.apps.kualiautomation.services.config.ConfigDefaults;
import edu.bu.ist.apps.kualiautomation.util.Utils;

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
		this.runLog = new RunLog(true);
	}
	
	@Override
	public void run() {
		
		try {
			setDriver();			
			
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
				try {
					driver.close();
					driver.quit();
				} 
				catch (NoSuchWindowException e) {
					// Do nothing. It just means we are in headless mode.
				}
			}
		}
	}

	private void setDriver() throws Exception {
		if(config.isHeadless()) {
			driver = Driver.HEADLESS.getDriver();
		}
		else {
			String drivername = System.getProperty(Driver.DRIVER_SYSTEM_PROPERTY);
			if(Utils.isEmpty(drivername)) {
				driver = Driver.DEFAULT_DRIVER.getDriver();
			}
			else {
				driver = Driver.valueOf(drivername).getDriver();
			}			
		}
	}
	
	private void work() {
		System.out.println("Running cycle...");
		CycleRunner runner = new CycleRunner(this, runLog);
		runLog = runner.run();		
	}
	
	private void reportResults() {
		System.out.println("Reporting results...");
		if(logOutput == null)
			logOutput = System.out;
		runLog.printResults(logOutput);
	}

	private boolean login() throws Exception {
		if(driver == null)
			return false;
		KerberosLogin kerberos = new KerberosLogin(this, 10, runLog);
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
		
		// Create a configuration and load with default values.
		Config config = new Config();
		ConfigDefaults.populate(config);
		
		// Change the default environment
		ConfigEnvironment testDriveEnv = new ConfigEnvironment();
		testDriveEnv.setUrl("https://res-demo2.kuali.co/kc-dev/kr-login/login?viewId=DummyLoginView&returnLocation=%2Fkc-krad%2FlandingPage&formKey=68ba02c6-3587-4b81-a4c9-d8eb465eaa01&cacheKey=7ft9p0xr31nwqntioww2pa");
		testDriveEnv.setId(999);
		config.addConfigEnvironment(testDriveEnv);
		config.setCurrentEnvironment(testDriveEnv);
		
		// Create a cycle and start it in a thread. 
		Cycle cycle = new Cycle();
		Thread thread = new Thread(new Session(config, cycle, false));
		thread.start();
		System.out.println("thread started...");
	}
}

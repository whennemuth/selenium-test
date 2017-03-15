package edu.bu.ist.apps.kualiautomation.services.automate.element;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import edu.bu.ist.apps.kualiautomation.AbstractJettyBasedTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XpathElementCacheTest extends AbstractJettyBasedTest {

	@Override
	public void setupBefore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadHandlers(Map<String, String> handlers) {
		handlers.put("subaward-entry-1", "SubawardEntry.htm");
		handlers.put("SubawardEntry_files", "SubawardEntry_files");
	}

	@Test
	public void test01XpathAndContext() {
		//XpathAndContext ctx1 = new XpathAndContext(driver)
	}

}

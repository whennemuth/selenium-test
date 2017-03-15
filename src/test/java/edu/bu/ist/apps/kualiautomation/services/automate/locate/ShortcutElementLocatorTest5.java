package edu.bu.ist.apps.kualiautomation.services.automate.locate;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.runners.MockitoJUnitRunner;

import edu.bu.ist.apps.kualiautomation.AbstractJettyBasedTest;

@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortcutElementLocatorTest5 extends AbstractJettyBasedTest {

	@Override
	public void setupBefore() { /* TODO Auto-generated method stub */ }

	@Override
	public void loadHandlers(Map<String, String> handlers) {
		handlers.put("subaward-entry-1", "SubawardEntry.htm");
		handlers.put("SubawardEntry_files", "SubawardEntry_files");
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}

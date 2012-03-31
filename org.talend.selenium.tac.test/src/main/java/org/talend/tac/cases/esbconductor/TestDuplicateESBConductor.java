package org.talend.tac.cases.esbconductor;

import org.talend.tac.base.WebdriverLogin;
import org.talend.tac.modules.impl.DuplicateESBConductorImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestDuplicateESBConductor extends WebdriverLogin {
	DuplicateESBConductorImpl duplicateESBConductorImpl;
	@BeforeMethod
    public void beforeMethod(){
		duplicateESBConductorImpl = new DuplicateESBConductorImpl(driver);
    }
	/*add esbconductor of service*/
	@Test
	@Parameters({"labelOfService", "desOfService", "repository", "group", "artifact",
		"version", "name", "type", "context", "serverOfRuntime"})
	public void testDuplicateESBConductorOfService(String label, String des, String repository,
			String group, String artifact, String version, String name, String type, 
			String context, String server) {		
		duplicateESBConductorImpl.duplicateConductor(label, des, repository, group, artifact, version, name, type, context, server);
	}
	
}

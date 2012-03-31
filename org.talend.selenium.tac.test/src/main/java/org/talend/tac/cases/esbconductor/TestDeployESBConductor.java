package org.talend.tac.cases.esbconductor;

import org.talend.tac.base.WebdriverLogin;
import org.talend.tac.modules.impl.DeployESBConductorImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestDeployESBConductor extends WebdriverLogin {
	DeployESBConductorImpl deployESBConductorImpl;
	@BeforeMethod
    public void beforeMethod(){
		deployESBConductorImpl = new DeployESBConductorImpl(driver);
    }
	@Test
	@Parameters({"labelOfService", "name"})
	public void testDeployESBConductor(String label, String name) {		
		deployESBConductorImpl.deployEsbConductor(label, name);								
	}
	

	@Test
	@Parameters({"labelOfService", "name"})
	public void testUndeployESBConductor(String label, String name) {	
		deployESBConductorImpl.undeployEsbConductor(label,name);						
	}
	
}

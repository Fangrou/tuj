package com.talend.tac.cases.users;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class TestDeleteUser extends Users {
    
	
	@Test
	@Parameters({"userName","LoginNameChooseAdministratorRole"})
	public void testCancleDeleteUser(String userName,String deleteUser) throws Exception {
		this.clickWaitForElementPresent("idMenuUserElement");
		selenium.setSpeed(MID_SPEED);
		Assert.assertTrue(selenium.isTextPresent(userName));
		selenium.setSpeed(MIN_SPEED);
		selenium.mouseDown("//div[text()='"+deleteUser+"']");//Select an existing user
		selenium.chooseCancelOnNextConfirmation();
		selenium.click("idSubModuleDeleteButton");
	    Assert.assertTrue(selenium.getConfirmation().matches("^"+other.getString("delete.User.confirmation")+" [\\s\\S]$"));
	    Assert.assertTrue(selenium.isElementPresent("//div[text()='"+deleteUser+"']"));
	    
	}
	
	@Test
	@Parameters({"userName","LoginNameChooseAdministratorRole","userNameAllRoles"})
	public void testDeleteUser(String userName,String deleteUser,String deleteUser1) throws Exception {
		deleteUser(userName, deleteUser);
		deleteUser(userName,deleteUser1);
		selenium.setSpeed(MID_SPEED);
	    Assert.assertFalse(selenium.isElementPresent("//div[text()='"+deleteUser1+"']"));
        selenium.setSpeed(MIN_SPEED);  
	    
	}
    
	
	@Test
	@Parameters({"userName"})
	public void testDeleteLoginUser(String userName) throws Exception {
        deleteUser(userName,userName);
        selenium.setSpeed(MID_SPEED);
		Assert.assertTrue(selenium.isTextPresent(rb.getString("user.error.deleteCurrentLoggedUser")));
        selenium.setSpeed(MIN_SPEED);  
		
	}
}

package com.talend.tac.cases.executionTask;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.talend.tac.cases.Login;

public class TestDeteleTask  extends Login {
    
	
	@Test
	public void testCancleDeleteTask() {
		this.clickWaitForElementPresent("!!!menu.executionTasks.element!!!");
		selenium.setSpeed(MID_SPEED);
	    Assert.assertTrue(selenium.isElementPresent("//div[contains(text(),'Conductor') and @class='header-title']"));
		selenium.setSpeed(MIN_SPEED);
	    selenium.click("idSubModuleRefreshButton");//click "Refresh"
	    this.waitForElementPresent("//span[text()='testTaskNotChooseActive']", WAIT_TIME);
		selenium.mouseDown("//span[text()='testTaskNotChooseActive']");//select a exist task
		selenium.chooseCancelOnNextConfirmation();
		selenium.click("idSubModuleDeleteButton");//clcik "Delete"
		selenium.setSpeed(MID_SPEED);
		Assert.assertTrue(selenium.getConfirmation().matches(other.getString("delete.plan.warning")));
		Assert.assertTrue(selenium.isElementPresent("//span[text()='testTaskNotChooseActive']"));
		selenium.setSpeed(MIN_SPEED);
		
	}
	
	@Test
	public void testDeleteTask() {
		this.clickWaitForElementPresent("!!!menu.executionTasks.element!!!");
		selenium.setSpeed(MID_SPEED);
	    Assert.assertTrue(selenium.isElementPresent("//div[contains(text(),'Conductor') and @class='header-title']"));
		selenium.setSpeed(MIN_SPEED);
	    selenium.click("idSubModuleRefreshButton"); //click "Refresh"
		selenium.mouseDown("//span[text()='testTaskNotChooseActive']");//select a exist task
		selenium.chooseOkOnNextConfirmation();
		selenium.chooseOkOnNextConfirmation();
		selenium.click("idSubModuleDeleteButton");//clcik "Delete"
		Assert.assertTrue(selenium.getConfirmation().matches(other.getString("delete.plan.warning")));
		Assert.assertEquals(selenium.getConfirmation(), "Do you want to remove all of the related logs and archives");
		selenium.setSpeed(MID_SPEED);
		Assert.assertFalse(selenium.isElementPresent("//span[text()='testTaskNotChooseActive']"));//the plan cannot appear
		selenium.setSpeed(MIN_SPEED);
		
	}
}

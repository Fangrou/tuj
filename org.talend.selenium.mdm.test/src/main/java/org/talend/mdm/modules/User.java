package org.talend.mdm.modules;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.talend.mdm.Base;
import org.testng.Assert;


public class User extends Base{
	
	public User(WebDriver driver) {
		super.setDriver(driver);
		this.driver = driver;
	}
	
	protected void openMenuAdministrator(){
		this.logger.info("open Administration page");
		Assert.assertTrue(this.isElementPresent(By.xpath(locator.getString("xpath.Administration.menu")), WAIT_TIME_MAX));
		this.clickElementByXpath(locator.getString("xpath.Administration.menu"));
		}
	
	public void gotoUserManagePage(){
		this.clickElementByXpath(locator.getString("xpath.mangeuser.menu"));
		Assert.assertTrue(this.isElementPresent(By.xpath(locator.getString("xpath.user.button.add")), this.WAIT_TIME_MAX));
	}
	
	public void selectRoles(String[] roles){
		for(String role: roles) {
			this.clickElementByXpath(locator.getString("xpath.user.add.role.img"));
			this.waitfor(By.xpath(this.getString(locator, "xpath.user.add.role.select", role)), 30);
			this.clickElementByXpath(this.getString(locator, "xpath.user.add.role.select", role));
			this.clickElementByXpath(locator.getString("xpath.user.add.role.add"));
			this.waitforElementDisplayed(By.xpath(this.getString(locator, "xpath.user.add.role.added.listview", roles)),WAIT_TIME_MAX);
		}
		
	}
	
	public void selectRoles(String roles){
		this.logger.info("click to open roles selection drop down list!");
		this.clickElementByXpath(locator.getString("xpath.user.add.role.img"));
		this.logger.info("roles selection drop down list opened ok!");
		this.logger.info("select role to add");
		this.waitforElementDisplayed(By.xpath(this.getString(locator, "xpath.user.add.role.select", roles)), WAIT_TIME_MAX);
		this.clickElementByXpath(this.getString(locator, "xpath.user.add.role.select", roles));
		this.clickElementByXpath(locator.getString("xpath.user.add.role.add"));
		this.waitforElementDisplayed(By.xpath(this.getString(locator, "xpath.user.add.role.added.listview", roles)),WAIT_TIME_MAX);
		this.logger.info("select role to add");
	}
	
	public void confBaseUserInfo(String identifier,String firstName,String lastName,String password,String confirmPassword,
			String email, String company, String defaultVersion, boolean active){
		this.typeTextByName(locator.getString("name.user.add.name"), identifier);
		this.typeTextByName(locator.getString("name.user.add.password"), password);
		this.typeTextByXpath(locator.getString("xpath.user.add.password.confirm"), confirmPassword);
		this.typeTextByName(locator.getString("name.user.add.givenName"), firstName);
		this.typeTextByName(locator.getString("name.user.add.familyName"), lastName);
		this.typeTextByName(locator.getString("name.user.add.realEmail"), email);
		this.typeTextByName(locator.getString("name.user.add.company"), company);
		this.typeTextByName(locator.getString("name.user.add.universe"), defaultVersion);
		if(active) {
			this.getElementByName(locator.getString("name.user.add.enabled")).click();
			this.logger.info("click add user active button!");
		}
		
	}
	
	public void configureUser(String identifier,String firstName,String lastName,String password,String confirmPassword,
			String email, String company, String defaultVersion, boolean active, String[] roles) {
		
		this.confBaseUserInfo(identifier, firstName, lastName, password, confirmPassword, email, company, defaultVersion, active);
		this.selectRoles(roles);
	}
	
	protected void addUser(String identifier,String firstName,String lastName,String password,String confirmPassword,
			String email, String company, String defaultVersion, boolean active, String[] roles) {
		this.clickElementByXpath(locator.getString("xpath.user.button.add"));
		this.confBaseUserInfo(identifier, firstName, lastName, password, confirmPassword, email, company, defaultVersion, active);
		this.selectRoles(roles);
		this.clickElementByXpath(locator.getString("xpath.user.add.role.save"));
		this.getElementByXpath(locator.getString("xpath.user.add.role.flashcache.ok")).click();
		Assert.assertTrue((this.isElementPresent(By.xpath(this.getString(locator, "xpath.user.identifier", identifier)), WAIT_TIME_MAX)));
	    this.logger.info("user "+identifier+" had been added succussfull!");
//		Assert.assertNotNull("Haven't add the user " + identifier + "successfully!", getUserDeleteElement(identifier));
	}
	
	protected void addUser(String identifier,String firstName,String lastName,String password,String confirmPassword,
			String email, String company, String defaultVersion, boolean active, String roles) {
		
		
		this.clickElementByXpath(locator.getString("xpath.user.button.add"));
		this.confBaseUserInfo(identifier, firstName, lastName, password, confirmPassword, email, company, defaultVersion, active);
		this.selectRoles(roles);
		this.clickElementByXpath(locator.getString("xpath.user.add.role.save"));
		this.logger.info("click add user save button!");
		this.getElementByXpath(locator.getString("xpath.user.add.role.flashcache.ok")).click();
		this.logger.info("click add flush cache ok button!");
	    Assert.assertTrue((this.isElementPresent(By.xpath(this.getString(locator, "xpath.user.identifier", identifier)), WAIT_TIME_MAX)));
	    this.logger.info("user "+identifier+" had been added succussfull!");
	}
	
	
	

	
	public void deleteUser(String userName) {
		Assert.assertTrue(this.isElementPresent(By.xpath(this.getString(locator, "xpath.user.delete", userName)), WAIT_TIME_MAX), "the user to delete not present right now.");
		this.sleepCertainTime(1000);
		this.clickElementByXpath(this.getString(locator, "xpath.user.delete", userName));
		Assert.assertTrue(this.isElementPresent(By.xpath(locator.getString("xpath.user.delete.yes")), WAIT_TIME_MAX));
		this.clickElementByXpath(locator.getString("xpath.user.delete.yes"));
		logger.info("########" + this.getElementByXpath(this.getString(locator, "xpath.user.delete", userName)));
//		Assert.assertFalse(this.waitforElementDisplayed(By.xpath(this.getString(locator, "xpath.user.delete", userName)), 30), "user "+userName+" delete failed!");
	}
	
	protected void deleteAllUsersExcept(String userExcept){
		
		
		
		List a = this.getElementsByXpath(locator.getString("xpath.user.listdisplay.identiferlist"));
		List b = new ArrayList<String>();
		for(int i=0;i<a.size();i++){
			String userName = this.getValue((WebElement)a.get(i));
			logger.info(userName);
			if(userName.equals(userExcept)){
				continue;
			}
			else{
				b.add(this.getValue((WebElement)a.get(i)));
				logger.info("b+++++++++++++++++++++++++++++++++++++++++++++++++++++"+userName);
			}
//			System.err.println("total user number is :"+this.getElementsByXpath(locator.getString("xpath.user.listdisplay.identiferlist")).size());
		}
		
		for(int j=0;j <b.size();j++){
			String userName = b.get(j).toString();
			logger.info("The "+j+" time to delete user name is :"+userName);
			this.deleteUser(userName);
			
		}
		this.sleepCertainTime(3000);
		System.err.println("after delete,total user number is :"+this.getElementsByXpath(locator.getString("xpath.user.listdisplay.identiferlist")).size());
		Assert.assertTrue(this.getElementsByXpath(locator.getString("xpath.user.listdisplay.identiferlist")).size()==1);
		
	}

	public void searchUser(String userName) {
		this.modifyTextByXpath(locator.getString("xpath.user.search.input"), userName);
		this.clickElementByXpath(locator.getString("xpath.user.search"));
	}
	
	public void clickAddNewUser(){
		this.clickElementByXpath(locator.getString("xpath.user.button.add"));
	}
	public  WebElement getUserDeleteElement(String userName) {
		return	this.getElementByXpath(this.getString(locator, "xpath.user.delete", userName));		 
	}
	
	public void typeUserName(String userName) {
		this.typeTextByName(locator.getString("name.user.add.name"), userName);
	}
	public void typePassword(String password) {
		this.typeTextByName(locator.getString("name.user.add.password"), password);
	}
	public void typeConfirmPassword(String password) {
		this.typeTextByXpath(locator.getString("xpath.user.add.password.confirm"), password);
	}
	public void typeFirstName(String firstName) {
		this.typeTextByName(locator.getString("name.user.add.givenName"), firstName);
	}
	public void typeLastName(String lastName) {
		this.typeTextByName(locator.getString("name.user.add.familyName"), lastName);
	}
	public void typeEmail(String email) {
		this.typeTextByName(locator.getString("name.user.add.realEmail"), email);
	}
	public void typeCompany(String company) {
		this.typeTextByName(locator.getString("name.user.add.company"), company);
	}
	public void typeUniVerse(String universe) {
		this.typeTextByName(locator.getString("name.user.add.universe"), universe);
	}
	
	public void clickActive() {
		this.getElementByName(locator.getString("name.user.add.enabled")).click();
	}
	
	public void clickFlushCacheButton(){
		this.clickElementByXpath(locator.getString("xpath.user.add.role.flashcache.button"));
	}
	
	public void clickFlushCacheOK(){
		this.clickElementByXpath(locator.getString("xpath.user.add.role.flashcache.ok"));
	}
	public boolean isActive(){
		return this.getElementByXpath(locator.getString("xpath.user.add.enabled.true")) != null;
	}
	
	public void addRoles(String[] roles) {
		for(String role: roles) {
			this.clickElementByXpath(locator.getString("xpath.user.add.role.img"));
			this.clickElementByXpath(this.getString(locator, "xpath.user.add.role.select", role));
			this.clickElementByXpath(locator.getString("xpath.user.add.role.add"));
		}
	}
	public void deleteRoles(String[] roles) {
		for(String role: roles) {
			this.clickElementByXpath(this.getString(locator, "xpath.user.delete.role.select", role));
			this.clickElementByXpath(locator.getString("xpath.user.delete.role.delete"));
		}
	}
	
	// unfinished
	public List<String> getRoles(){
		List<String> roles = new ArrayList<String>();
		List<WebElement> elements = this.getElementsByXpath(locator.getString("xpath.user.added.roles"));
		
		for(WebElement element : elements) {
			roles.add(element.getText());
		}
		return roles;
	}

	
	public void clickSave(){
		this.clickElementByXpath(locator.getString("xpath.user.add.role.save"));
	}
	
	public void clickSaveAndCheckExpectedTrue(String userName){
		this.clickElementByXpath(locator.getString("xpath.user.add.role.save"));
		this.clickElementByXpath(locator.getString("xpath.user.add.role.flashcache.ok"));
	    Assert.assertTrue(this.isElementPresent(By.xpath(this.getString(locator, "xpath.user.identifier", userName)), WAIT_TIME_MID), "user"+userName+"added failed!");
	}
	
	public void selectAUser(String userName) {
		this.clickElementByXpath(this.getString(locator, "xpath.user.select", userName));
	}
	
	public boolean isUserSheetExist(){
		return this.getElementsByXpath(locator.getString("xpath.user.sheet")) != null;
	}
}
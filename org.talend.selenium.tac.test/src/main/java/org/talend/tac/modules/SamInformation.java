package org.talend.tac.modules;


import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.Locatable;
import org.talend.tac.base.WebDriverBase;

import com.talend.tac.base.Karaf;

public class SamInformation extends WebDriverBase{
     public  SamInformation(WebDriver driver) {
    	 super.setDriver(driver);
         this.driver = driver;
     }
     
     public void mouseDown(String xpathExpression) {
  	   Locatable hoverItem = (Locatable) driver.findElement(By.xpath(xpathExpression));
  	   Mouse mouse = ((HasInputDevices) driver).getMouse();
  	   mouse.mouseDown(hoverItem.getCoordinates());
  	   try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
     }
     
     public void installServiceAndConsumer(String karafURL,String providerName,String consumerName) { 
    	 Karaf karaf=new Karaf(karafURL);
    	 karaf.karafAction("start"+providerName+"control-bundle", WAIT_TIME_MAX);
    	 karaf.karafAction("install -s file://"+getAbsolutePath("org/talend/tac/folder/esb/"+consumerName+"-0.1.jar")+"", WAIT_TIME_MAX);    	 
    	 logger.info("install Service and Consumer");
     }
     
     public void intoSamPage() {	 
    	 this.waitforElementDisplayed(By.id("!!!menu.serviceactivity.element!!!"), WAIT_TIME_MAX);
    	 this.clickElementById("!!!menu.serviceactivity.element!!!");
    	 this.waitforTextDisplayed("Service Activity Monitoring",WAIT_TIME_MIN);
    	 logger.info("intoSamPage");    	 
     }
     
     public void locatorEvents() { 
    	 this.waitforElementDisplayed(By.xpath("//div[@title='one-way']"), WAIT_TIME_MIN);
    	 this.mouseDown("//div[@title='one-way']");
    	 Assert.assertTrue(this.isElementPresent(By.xpath("//legend[text()='Consumer']//parent::fieldset[@class='samui-participant-details']"), WAIT_TIME_MAX));
    	 Assert.assertTrue(this.isElementPresent(By.xpath("//legend[text()='Provider']//parent::fieldset[@class='samui-participant-details']"), WAIT_TIME_MAX));
    	 Assert.assertTrue(this.isElementPresent(By.xpath("//fieldset[@class='samui-event-details-REQ_OUT']"), WAIT_TIME_MAX));
    	 Assert.assertTrue(this.isElementPresent(By.xpath("//fieldset[@class='samui-event-details-REQ_IN']"), WAIT_TIME_MAX));
    	 logger.info("locator and check events");
     }
     
}

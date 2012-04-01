package org.talend.mdm.cases.login;

import org.talend.mdm.Base;
import org.talend.mdm.Login;
import org.talend.mdm.impl.LicenseImpl;
import org.talend.mdm.impl.LogonImpl;
import org.talend.mdm.impl.UserImpl;
import org.talend.mdm.modules.Logon;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class TestLogin extends Base {
	LogonImpl log;
	@BeforeMethod
	public void beforeMethod(){
		log = new LogonImpl(driver);
	}
		
	
	@Test
	@Parameters( { "url", "root","user.name","user.password","message" })
	public void testRightLogin(String url,String root,String userName,String userPassword,String message,ITestContext context) {
		log.initWebdriver(url, root, context);
		log.loginAdministratorImpl( userName, userPassword , message);
	}
	
	@Test
	@Parameters( { "url", "root","user.name","user.password","message" })
	public void testLogoutCorrectly(String url,String root,String userName,String userPassword,String message,ITestContext context) {
		log.initWebdriver(url, root, context);
		log.loginAdministratorLogoutImpl( userName, userPassword , message);
	}
	
	@Test
	@Parameters( { "url", "root","user.name","user.password","message" ,"session.timeout"})
	public void testLoginAndSessionTimeout(String url,String root,String userName,String userPassword,String message,int timeout,ITestContext context) {
		log.initWebdriver(url, root, context);
		log.loginAdministratorSessionTimeoutImpl(userName, userPassword, message, timeout);
	}
	
	
	@Test
	@Parameters( { "url", "root","user.name","user.password","message.wrong.username" })
	public void testWongLogin(String url,String root,String userName,String userPassword,String message,ITestContext context) {
		log.initWebdriver(url, root, context);
		log.loginWithWrongNamePassImpl( userName, userPassword , message);
	}
	
	
}

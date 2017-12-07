package no.imr.geoexplorer.integration.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;


import ch.qos.logback.core.net.SyslogOutputStream;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

/**
 * Test GUI components in map-client
 * 
 * @author endrem
 *
 */
public class SeleniumWeb {
	
	private WebDriver driver;
	
	@Test
	public void aTest() throws IOException {
		
	    FirefoxDriverManager.getInstance().setup();

	    FirefoxProfile profile = new FirefoxProfile();
	    profile.setPreference("browser.download.folderList", 2);
	    profile.setPreference("browser.download.manager.showWhenStarting", false);
	    profile.setPreference("browser.download.dir", System.getProperty("user.dir") );
	    profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "image/png");

	    FirefoxOptions options = new FirefoxOptions();
	    options.setProfile(profile);
	    		
	    WebDriver driver = new FirefoxDriver( options );
	    
		driver.get("http://webtest1.nodc.no/mareano/mareano.html");

		WebElement element = driver.findElement( By.className("icon-printer") );
		System.out.println("icon-print:"+element+":"+element.getText()+":");
		element.click();
		
		Wait wait = new FluentWait(driver)
				.withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS);
//				.ignoring(NoSuchElementException.class);
				 
		Boolean printMapFound = (Boolean) wait.until(new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver2) {
				File folder = new File( System.getProperty("user.dir") );
				File[] listOfFiles = folder.listFiles();
				for ( int i=0; i < listOfFiles.length; i++ ) {
					String name = listOfFiles[i].getName();
					
					if ( name.contains("printMap") && name.contains( ".png") ) {

						
						System.out.println( name );
						System.out.println("fileSize:"+listOfFiles[i].length());
						
						boolean fileOver10kb = (listOfFiles[i].length() > 10000) ;
						listOfFiles[i].delete();
						if ( fileOver10kb ) {
							return true;
						}
					}
				}
				return false;
			}
		});
		
		driver.quit();
	}
	
//    @Before
    public void setupTest() {
    	driver = new FirefoxDriver();
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }	
	
//	@Test
	public void doTest() {

//     driver.get("http://www.google.com");
		driver.get("http://webtest1.nodc.no/mareano/mareano.html");
//		System.out.println("" + driver.getPageSource() + " ");
     
		WebElement element = driver.findElement( By.xpath("/*") );
		System.out.println("Page title is: " + element);

     element = driver.findElement( By.xpath("//body") );
     System.out.println("Page title is: " + element + " text:"+element.getText()+"_");
     
		element = driver.findElement( By.xpath("/html/head/title") );
		System.out.println("Page title is: " + element.getText());
	}
}

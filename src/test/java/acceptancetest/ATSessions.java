package acceptancetest;

import acceptancetest.helpers.InputHelper;
import acceptancetest.helpers.LoginHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ATSessions {
    private String baseUrl;
    
    private String username = "test-user";
    private String password = "pass";
    
    private String organizationName = "test organization 1";
    private String categoryName = "test category 1";
    private String topicName = "test topic 1";
    
    @BeforeClass
    public void setupClass() {
        baseUrl = System.getProperty("app.baseUrl");
        WebDriver driver = new ChromeDriver();
        LoginHelper.register(driver, username, password);
        
        driver.get(baseUrl);

        (new WebDriverWait(driver, 5)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/#/dashboard"));
        
        driver.findElement(By.name("new-organization")).click();
        
        (new WebDriverWait(driver, 5)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/#/organization/create"));
        
        //driver.findElement(By.id("organizationName")).sendKeys(organizationName);
        InputHelper.sendKeys(driver, driver.findElement(By.id("organizationName")), organizationName);
        driver.findElement(By.name("add-organization")).click();

        (new WebDriverWait(driver, 5)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/#/dashboard"));
        
        driver.findElement(By.linkText("My organizations")).click();
        driver.findElement(By.linkText(organizationName)).click();
    }
    
    @Test
    public void placeholder() {
        
    }
    
    @After
    public void tearDown() {
        
    }
}

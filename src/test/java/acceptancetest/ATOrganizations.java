package acceptancetest;

import acceptancetest.helpers.InputHelper;
import acceptancetest.helpers.LoginHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ATOrganizations {
    private String baseUrl;
    private String username = "user";
    private String password = "pass";
    WebDriver driver;

    @Before
    public void setup() {
        baseUrl = System.getProperty("app.baseUrl");
        driver = new ChromeDriver();
        LoginHelper.login(driver, username, password);
        driver.get(String.format("%s/#/organization/create", baseUrl));
    }
    
    @Test
    public void testCreateNewOrganization() {

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-organization")) != null
        );
        
        WebElement newOrganizationButton = driver.findElement(By.name("new-organization"));
        newOrganizationButton.click();


        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("form-new-organization")) != null
        );

        WebElement nameElement=driver.findElement(By.name("organization-name"));
        InputHelper.sendKeys(driver,nameElement,"Karel de grote4");

        driver.findElement(By.name("add-organization")).click();


        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("my-organizations")) != null
        );

        driver.findElement(By.name("my-organizations")).click();
/*
        List<WebElement> organizationElements = driver.findElements(By.className("organization"));

        Assert.assertEquals(1, organizationElements.size());
        Assert.assertEquals("Karel de Grote", organizationElements.get(0).getText());  */
    }


}

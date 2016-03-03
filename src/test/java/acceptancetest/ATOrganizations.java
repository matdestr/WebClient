package acceptancetest;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ATOrganizations {
    private String baseUrl;
    private String username = "user";
    private String password = "pass";
    
    @Before
    public void setup() {
        WebDriver driver = new ChromeDriver();
        
        LoginHelper.register(driver, username, password);
        driver.get(String.format("%s/organizations/create", baseUrl));
        
        // TODO
    }
    
    @Test
    public void testCreateNewOrganization() {
        baseUrl = System.getProperty("app.baseUrl");

        WebDriver driver = new ChromeDriver();
        driver.get(baseUrl);
        
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("form-sign-in")) != null
        );
        
        driver.findElement(By.name("username")).sendKeys("user");
        driver.findElement(By.name("password")).sendKeys("pass");
        
        driver.findElement(By.name("form-sign-in")).submit();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-organization")) != null
        );
        
        WebElement newOrganizationButton = driver.findElement(By.name("new-organization"));
        newOrganizationButton.click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("form-new-organization")) != null
        );
        
        WebElement form = driver.findElement(By.name("form-new-organization"));
        
        form.findElement(By.name("organization-name")).sendKeys("Karel de Grote");
        //form.findElement(By.id("add-user-btn")).click();
        //form.findElements(By.className("email-input")).stream().forEach(e -> e.sendKeys("unexisting@notamailservice.com"));
        form.findElement(By.name("add-organization")).click();
        
        /*driver.get(baseUrl);

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("my-organizations")) != null
        );
        
        driver.findElement(By.name("my-organizations")).click();

        List<WebElement> organizationElements = driver.findElements(By.className("organization"));
        
        Assert.assertEquals(1, organizationElements.size());
        Assert.assertEquals("Karel de Grote", organizationElements.get(0).getText());*/
    }
}

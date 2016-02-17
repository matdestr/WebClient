package acceptancetest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ATCreateOrganization {
    private String baseUrl;
    private String username = "user";
    private String password = "pass";
    
    @Before
    public void setup() {
        /*WebDriver driver = new ChromeDriver();
        
        LoginHelper.register(driver, username, password);
        driver.get(String.format("%s/organizations/create", baseUrl));
        
        // TODO*/
    }
    
    //@Test
    public void testCreateNewOrganization() {
        //baseUrl = System.getProperty("app.baseUrl");
        //System.out.println("BASE URL: " + baseUrl);
        baseUrl = "http://localhost:8080/kandoe"; // TODO : Fix
        
        WebDriver driver = new ChromeDriver();
        driver.get(baseUrl);
        
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("form-sign-in")) != null
        );
        
        WebElement newOrganizationButton = driver.findElement(By.name("new-organization"));
        newOrganizationButton.click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/organizations/create")
        );
        
        WebElement form = driver.findElement(By.name("form-new-organization"));
        driver.switchTo().frame(form);
        
        driver.findElement(By.name("organization-name")).sendKeys("Karel de Grote");
        driver.findElement(By.name("add-organization")).click();
        
        driver.get(baseUrl);

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.getCurrentUrl().equals(baseUrl)
        );
        
        driver.findElement(By.name("my-organizations")).click();

        List<WebElement> organizationElements = driver.findElements(By.className("organization"));
        
        Assert.assertEquals(1, organizationElements.size());
        Assert.assertEquals("Karel de Grote", organizationElements.get(0).getText());
    }
}

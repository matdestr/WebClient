package acceptancetest;


import acceptancetest.helpers.LoginHelper;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ATCategory {
    private String baseUrl;
    private String username = "user";
    private String password = "pass";
    private WebDriver driver;


    @Before
    public void setup() {
        baseUrl = System.getProperty("app.baseUrl");

        driver = new ChromeDriver();

        LoginHelper.register(driver, username, password);

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
        form.findElement(By.name("add-organization")).click();

        driver.get(String.format("%s/categories/create", baseUrl));

    }

    @Test
    public void testCreateNewCategory() {
        (new WebDriverWait(driver, 100)).until((WebDriver d) -> d.findElement(By.id("form-sign-up")) != null);

    }


}

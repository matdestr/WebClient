package acceptancetest;

import acceptancetest.helpers.LoginHelper;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by thaneestevens on 7/03/16.
 */
public class ATTopic {
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
    public void testCreateTopic(){

    }
}

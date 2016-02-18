package acceptancetest;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.WebDriverWait;


import static org.junit.Assert.assertEquals;


/**
 * Created on 14/02/2016
 *
 * @author Arne De Cock
 */
public class ATSignInUser {

    private String baseUrl;

    private final String NAME_SIGN_IN_USERNAME = "username";
    private final String NAME_SIGN_IN_PASSWORD = "password";

    private final String DUMMY_USERNAME = "user1";
    private final String DUMMY_PASSWORD = "test123";

    private final short DEFAULT_SLEEP_TIMEOUT = 5;

    //todo: test with ChromeDriver (set system property to path ChromeDriver)
    //@Before
    public void setup() {
        baseUrl = System.getProperty("app.baseUrl");
        System.out.println(baseUrl);
        WebDriver driver = new ChromeDriver();
        driver.get(baseUrl);

        WebElement registrationForm = driver.findElement(By.name("form-sign-up"));
        driver.switchTo().frame(registrationForm);

        driver.findElement(By.name("username")).sendKeys(DUMMY_USERNAME);
        driver.findElement(By.name("name")).sendKeys("User");
        driver.findElement(By.name("surname")).sendKeys("Tester");
        driver.findElement(By.name("email")).sendKeys("test@user.com");
        driver.findElement(By.name("password")).sendKeys(DUMMY_PASSWORD);
        driver.findElement(By.name("verify-password")).sendKeys(DUMMY_PASSWORD);

        registrationForm.submit();

        (new WebDriverWait(driver, DEFAULT_SLEEP_TIMEOUT)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/dashboard"));

        driver.findElement(By.name("sign-out")).click();

        (new WebDriverWait(driver, DEFAULT_SLEEP_TIMEOUT)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl) && d.getTitle().equals("Welcome to CanDo"));
    }

    //@Test
    public void testUserLoginSuccess() {
        val driver = new ChromeDriver();
        driver.get(baseUrl);

        val loginForm = driver.findElementByName("form-sign-in");
        driver.switchTo().frame(loginForm);

        driver.findElement(By.name(NAME_SIGN_IN_USERNAME)).sendKeys(DUMMY_USERNAME);
        driver.findElement(By.name(NAME_SIGN_IN_PASSWORD)).sendKeys(DUMMY_PASSWORD);

        loginForm.submit();

        (new WebDriverWait(driver, DEFAULT_SLEEP_TIMEOUT)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/overview"));

        assertEquals("user1", driver.findElementByName("username"));
    }

    //@Test
    public void testUserLoginFailure() {
        WebDriver driver = new ChromeDriver();
        driver.get(baseUrl);

        WebElement loginForm = driver.findElement(By.name("form-sign-in"));
        driver.switchTo().frame(loginForm);

        driver.findElement(By.name(NAME_SIGN_IN_USERNAME)).sendKeys(DUMMY_USERNAME);
        driver.findElement(By.name(NAME_SIGN_IN_PASSWORD)).sendKeys(DUMMY_PASSWORD + '4');

        loginForm.submit();

        (new WebDriverWait(driver, DEFAULT_SLEEP_TIMEOUT)).until((WebDriver d) -> !(d.findElement(By.id("error")).getText().equals("")));

        assertEquals("Wrong username or password", driver.findElement(By.id("error")).getText());
    }
}

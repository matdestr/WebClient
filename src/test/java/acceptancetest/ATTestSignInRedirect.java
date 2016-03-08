package acceptancetest;

import acceptancetest.helpers.LoginHelper;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ATTestSignInRedirect {
    private String baseUrl;
    private String username = "user";
    private String password = "pass";

    private final String PATH_ROUTE_PREFIX = "/#/";
    private final String NAME_SIGN_IN_USERNAME = "username";
    private final String NAME_SIGN_IN_PASSWORD = "password";

    @Before
    public void setup() {
        baseUrl = System.getProperty("app.baseUrl");
    }
    
    @Test
    public void testSignInRedirectsToDashboard() {
        WebDriver driver = new ChromeDriver();
        driver.get(baseUrl);

        WebElement loginForm = driver.findElement(By.name("form-sign-in"));
        loginForm.findElement(By.name(NAME_SIGN_IN_USERNAME)).sendKeys(username);
        loginForm.findElement(By.name(NAME_SIGN_IN_PASSWORD)).sendKeys(password);
        loginForm.submit();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.getCurrentUrl().endsWith("/dashboard")
        );
    }
    
    @Test
    public void testUnauthenticatedDashboardRedirectsToSignIn() {
        WebDriver driver = new ChromeDriver();
        driver.get(baseUrl + "/#/dashboard");
        
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.getCurrentUrl().equals(baseUrl + PATH_ROUTE_PREFIX)
        );
    }
    
    @Test
    public void testAuthenticatedDashboardDoesNotRedirect() {
        WebDriver driver = new ChromeDriver();
        
        LoginHelper.login(driver, "user", "pass");
        
        driver.get(baseUrl + PATH_ROUTE_PREFIX + "dashboard");

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.getCurrentUrl().equals(baseUrl + PATH_ROUTE_PREFIX + "dashboard")
        );
    }
}

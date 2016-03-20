package acceptancetest.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Helper class for login and register with Sellenium.
 */
public class LoginHelper {
    private static boolean registered = false;
    private static final int DEFAULT_TIMEOUT_SECONDS = 5;

    public static void register(WebDriver driver, String username, String password) {
        register(driver, username, password, DEFAULT_TIMEOUT_SECONDS);
    }

    public static void register(WebDriver driver, String username, String password, int timeout) {
        if (!registered) {
            String baseUrl = System.getProperty("app.baseUrl");
            driver.get(baseUrl);
            driver.findElement(By.id("form-sign-up-link")).click();

            (new WebDriverWait(driver, 1)).until((WebDriver d) -> d.findElement(By.id("form-sign-up")) != null);

            WebElement registrationForm = driver.findElement(By.id("form-sign-up"));

            InputHelper.sendKeys(driver, registrationForm.findElement(By.name("username")), username);
            InputHelper.sendKeys(driver, registrationForm.findElement(By.name("name")), "User");
            InputHelper.sendKeys(driver, registrationForm.findElement(By.name("surname")), "One");
            InputHelper.sendKeys(driver, registrationForm.findElement(By.name("email")), username + "@cando.com");
            InputHelper.sendKeys(driver, registrationForm.findElement(By.name("password")), password);
            InputHelper.sendKeys(driver, registrationForm.findElement(By.name("verify-password")), password);

            registrationForm.submit();

            (new WebDriverWait(driver, timeout)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/#/dashboard"));
            LoginHelper.registered = true;
            System.out.println("User registered.");
        } else {
            System.out.println("User is already registered.");
        }
    }

    public static void login(WebDriver driver, String username, String password) {
        login(driver, username, password, DEFAULT_TIMEOUT_SECONDS);
    }

    public static void login(WebDriver driver, String username, String password, int timeout) {
        // TODO
        String baseUrl = System.getProperty("app.baseUrl");
        driver.get(baseUrl);

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("form-sign-in")) != null
        );
        WebElement loginForm = driver.findElement(By.name("form-sign-in"));


        InputHelper.sendKeys(driver, loginForm.findElement(By.name("username")), username);
        InputHelper.sendKeys(driver, loginForm.findElement(By.name("password")), password);

        loginForm.submit();

        (new WebDriverWait(driver, timeout)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/#/dashboard"));
    }
}

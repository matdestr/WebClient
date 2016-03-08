package acceptancetest.helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginHelper {
    private static final int DEFAULT_TIMEOUT_SECONDS = 5;

    public static void register(WebDriver driver, String username, String password) {
        register(driver, username, password, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static void register(WebDriver driver, String username, String password, int timeout) {
        String baseUrl = System.getProperty("app.baseUrl");
        driver.get(baseUrl);
        driver.findElement(By.id("form-sign-up-link")).click();
        
        (new WebDriverWait(driver, 1)).until((WebDriver d) -> d.findElement(By.id("form-sign-up")) != null);
        
        WebElement registrationForm = driver.findElement(By.id("form-sign-up"));

        /*registrationForm.findElement(By.name("username")).sendKeys(username);
        registrationForm.findElement(By.name("name")).sendKeys("User");
        registrationForm.findElement(By.name("surname")).sendKeys("One");
        registrationForm.findElement(By.name("email")).sendKeys("user@kandoe.be");
        registrationForm.findElement(By.name("password")).sendKeys(password);
        registrationForm.findElement(By.name("verify-password")).sendKeys(password);*/
        
        InputHelper.sendKeys(driver, registrationForm.findElement(By.name("username")), username);
        InputHelper.sendKeys(driver, registrationForm.findElement(By.name("name")), "User");
        InputHelper.sendKeys(driver, registrationForm.findElement(By.name("surname")), "One");
        InputHelper.sendKeys(driver, registrationForm.findElement(By.name("email")), username + "@cando.com");
        InputHelper.sendKeys(driver, registrationForm.findElement(By.name("password")), password);
        InputHelper.sendKeys(driver, registrationForm.findElement(By.name("verify-password")), password);

        registrationForm.submit();
        (new WebDriverWait(driver, timeout)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/#/dashboard"));
    }
    
    public static void login(WebDriver driver, String username, String password) {
        login(driver, username, password, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static void login(WebDriver driver, String username, String password, int timeout) {
        // TODO
        String baseUrl = System.getProperty("app.baseUrl");
        driver.get(baseUrl);

        WebElement loginForm = driver.findElement(By.name("form-sign-in"));

        //loginForm.findElement(By.name("username")).sendKeys(username);
        //loginForm.findElement(By.name("password")).sendKeys(password);
        
        InputHelper.sendKeys(driver, loginForm.findElement(By.name("username")), username);
        InputHelper.sendKeys(driver, loginForm.findElement(By.name("password")), password);

        loginForm.submit();
        (new WebDriverWait(driver, timeout)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/#/dashboard"));
    }
}

package acceptancetest;

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
        WebElement registrationForm = driver.findElement(By.name("form-sign-up"));

        registrationForm.findElement(By.name("username")).sendKeys(username);
        registrationForm.findElement(By.name("name")).sendKeys("User");
        registrationForm.findElement(By.name("surname")).sendKeys("One");
        registrationForm.findElement(By.name("email")).sendKeys("user@kandoe.be");
        registrationForm.findElement(By.name("password")).sendKeys(password);
        registrationForm.findElement(By.name("verify-password")).sendKeys(password);

        registrationForm.submit();
        (new WebDriverWait(driver, timeout)).until((WebDriver d) -> d.getCurrentUrl().equals(baseUrl + "/overview"));
    }
    
    public static void login(WebDriver driver, String username, String password) {
        login(driver, username, password, DEFAULT_TIMEOUT_SECONDS);
    }
    
    public static void login(WebDriver driver, String username, String password, int timeout) {
        String baseUrl = System.getProperty("app.baseUrl");
        driver.get(baseUrl);

        WebElement loginForm = driver.findElement(By.name("form-sign-in"));

        loginForm.findElement(By.name("username")).sendKeys(username);
        loginForm.findElement(By.name("password")).sendKeys(password);

        loginForm.submit();
    }
}

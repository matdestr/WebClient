package acceptancetest;


import acceptancetest.helpers.LoginHelper;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ATGoToProfile {
    private String baseUrl;
    private String username = "MyAwesomeUsername";
    private String password = "MyMoreAwesomePassword";

    @Before
    public void setup(){
        WebDriver driver = new ChromeDriver();
        LoginHelper.register(driver, username, password);
        driver.get(String.format("%s/dashboard", baseUrl));
    }

    @Test
    public void goToProfile(){
        WebDriver driver = new ChromeDriver();
        driver.get(baseUrl);


        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.id("sign-in-button")).click();

        (new WebDriverWait(driver, 5)).until((WebDriver d) -> d.findElement(By.className("toolbar-dropdown-toggle-container")) != null);

        WebElement dropDownMenu = driver.findElement(By.className("toolbar-dropdown-toggle-container"));
        dropDownMenu.click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) ->
                        d.findElement(By.className("toolbar-dropdown-menu-item")) != null);

        WebElement profileElement = driver.findElement(By.className("toolbar-dropdown-menu-item"));
        profileElement.click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.getCurrentUrl().equalsIgnoreCase(baseUrl + "/profile?username=" + username));
    }
}

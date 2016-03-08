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
    private String username = "username";
    private String password = "password";

    @Before
    public void setup(){
        baseUrl = System.getProperty("app.baseUrl");
        WebDriver driver = new ChromeDriver();
        LoginHelper.register(driver, username, password);
    }

    @Test
    public void goToProfile(){
        WebDriver driver = new ChromeDriver();
        LoginHelper.login(driver, username, password);

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) ->
                        d.findElement(By.className("toolbar-dropdown-toggle")) != null);

        WebElement dropDownMenu = driver.findElement(By.className("toolbar-dropdown-toggle-container"));
        dropDownMenu.click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) ->
                        d.findElement(By.className("toolbar-dropdown-menu-item")) != null);

        WebElement profileElement = driver.findElement(By.className("toolbar-dropdown-menu-item"));
        profileElement.click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.getCurrentUrl().equalsIgnoreCase(baseUrl + "/#/profile?username=" + username));
    }
}

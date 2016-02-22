package acceptancetest;


import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ATCreateCategory {
    private String baseUrl;
    private String username = "user";
    private String password = "pass";

    @Test
    public void testCreateNewOrganization() {

        baseUrl = System.getProperty("app.baseUrl");
        System.out.println("BASE URL: " + baseUrl);

        WebDriver driver = new ChromeDriver();
        driver.get(baseUrl);

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("form-sign-in")) != null
        );

        driver.findElement(By.name("username")).sendKeys("user");
        driver.findElement(By.name("password")).sendKeys("pass");

        driver.findElement(By.name("form-sign-in")).submit();

        //TODO: Go to "Karel De Grote"
        //(new WebDriverWait(driver,5)).until((WebDriver d) -> d.findElement(By.name("")));

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-category")) != null
        );
        WebElement newCategoryButton = driver.findElement(By.name("new-category"));
        newCategoryButton.click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("form-new-category")) != null
        );
        WebElement form = driver.findElement(By.name("form-new-category"));

        form.findElement(By.name("category-name")).sendKeys("Probleemwijken");
        form.findElement(By.name("add-category")).click();

        driver.get(baseUrl);

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("my-categories")) != null
        );
        driver.findElement(By.name("my-categories")).click();
    }
}

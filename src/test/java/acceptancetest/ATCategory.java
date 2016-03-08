package acceptancetest;


import acceptancetest.helpers.LoginHelper;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ATCategory {
    private String baseUrl;
    private String username = "user";
    private String password = "pass";


    @Before
    public void setup() {
        WebDriver driver = new ChromeDriver();

        LoginHelper.register(driver, username, password);
        driver.get(String.format("%s/categories/create", baseUrl));
    }

    @Test
    public void testCreateNewCategory() {

    }
}

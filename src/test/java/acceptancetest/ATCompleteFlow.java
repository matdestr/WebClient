
package acceptancetest;

import acceptancetest.helpers.InputHelper;
import acceptancetest.helpers.LoginHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

public class ATCompleteFlow {
    private String baseUrl;
    private String username = "user";
    private String password = "pass";
    private String organizationName = "Karel De Grote - Test";
    private String categoryName = "Toegepaste Informatica";
    private String topicName = "Naschoolse activiteit";
    private String cardName1 = "Pretpark";
    private String cardName2="Zee";
    private String cardName3="Vakantie";
    private String cardName4="Boekenbeurs";
    WebDriver driver;

    @Before
    public void setup() {
        baseUrl = System.getProperty("app.baseUrl");
        driver = new ChromeDriver();
        LoginHelper.login(driver, username, password);
        driver.get(String.format("%s/#/organization/create", baseUrl));
    }

    @Test
    public void testCreateNewOrganization() {
        //Create Organization

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-organization")) != null
        );
        WebElement newOrganizationButton = driver.findElement(By.name("new-organization"));
        newOrganizationButton.click();


        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("form-new-organization")) != null
        );

        WebElement nameElement = driver.findElement(By.name("organization-name"));
        InputHelper.sendKeys(driver, nameElement, organizationName);

        driver.findElement(By.name("add-organization")).click();


        (new WebDriverWait(driver, 10)).until(
                (WebDriver d) -> d.findElement(By.name("my-organizations")) != null
        );
        driver.findElement(By.name("my-organizations")).click();

        (new WebDriverWait(driver, 10)).until(
                (WebDriver d) -> d.findElement(By.name(organizationName)) != null
        );
        driver.findElement(By.name(organizationName)).click();

        //Create Category
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-category")) != null
        );
        driver.findElement(By.name("new-category")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("category-name")) != null
        );
        WebElement catNameElement = driver.findElement(By.name("category-name"));
        InputHelper.sendKeys(driver, catNameElement, categoryName);

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("category-description")) != null
        );
        WebElement descriptionElement = driver.findElement(By.name("category-description"));
        InputHelper.sendKeys(driver, descriptionElement, "This is a description for the category Toegepaste Informatica in the organization Karel de grote");

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-category")) != null
        );
        driver.findElement(By.name("add-category")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.xpath("//div[@class='black-block']")) != null
        );
        driver.findElement(By.xpath("//div[@class='black-block']")).click();

        //Create Topic
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-topic")) != null
        );
        driver.findElement(By.name("new-topic")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("topic-name")) != null
        );
        WebElement topicNameElement = driver.findElement(By.name("topic-name"));
        InputHelper.sendKeys(driver, topicNameElement, topicName);

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("topic-description")) != null
        );
        WebElement topicDescriptionElement = driver.findElement(By.name("topic-description"));
        InputHelper.sendKeys(driver, topicDescriptionElement, "This is a description for the topic naschoolse activiteit in toegepaste informatica");

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-topic")) != null
        );
        driver.findElement(By.name("add-topic")).click();

        //Create Cards in Category
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-card")) != null
        );
        driver.findElement(By.name("new-card")).click();
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("card-text")) != null
        );
        WebElement cardNameElement = driver.findElement(By.name("card-text"));
        InputHelper.sendKeys(driver, cardNameElement, cardName1);
        WebElement cardImageElement = driver.findElement(By.name("card-image"));
        InputHelper.sendKeys(driver, cardImageElement, "http://lastminutesnederland.nl/wp-content/uploads/2012/11/pretpark.jpg");
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-card")) != null
        );
        driver.findElement(By.name("add-card")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-card")) != null
        );
        driver.findElement(By.name("new-card")).click();
        WebElement cardName2Element = driver.findElement(By.name("card-text"));
        InputHelper.sendKeys(driver, cardName2Element, cardName2);
        WebElement cardImage2Element = driver.findElement(By.name("card-image"));
        InputHelper.sendKeys(driver, cardImage2Element, "http://www.photofacts.nl/fotografie/foto/et/arjan-de-wit-zeeschap.jpg");
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-card")) != null
        );
        driver.findElement(By.name("add-card")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-card")) != null
        );
        driver.findElement(By.name("new-card")).click();
        WebElement cardName3Element = driver.findElement(By.name("card-text"));
        InputHelper.sendKeys(driver, cardName3Element, cardName3);
        WebElement cardImage3Element = driver.findElement(By.name("card-image"));
        InputHelper.sendKeys(driver, cardImage3Element, "http://i.imgur.com/RRUe0Mo.png");
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-card")) != null
        );
        driver.findElement(By.name("add-card")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-card")) != null
        );
        driver.findElement(By.name("new-card")).click();
        WebElement cardName4Element = driver.findElement(By.name("card-text"));
        InputHelper.sendKeys(driver, cardName4Element, cardName4);
        WebElement cardImage4Element = driver.findElement(By.name("card-image"));
        InputHelper.sendKeys(driver, cardImage4Element, "http://www.udenscollege.nl/wp-content/uploads/2014/06/boeken.jpg");
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-card")) != null
        );
        driver.findElement(By.name("add-card")).click();

        //Add Cards to topic
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.xpath("//div[@class='black-block']")) != null
        );
        driver.findElement(By.xpath("//div[@class='black-block']")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-card")) != null
        );
        driver.findElement(By.name("add-card")).click();

        /*
        driver.findElement(By.xpath("//card-detail[1]")).click();
        driver.findElement(By.xpath("//card-detail[2]")).click();
        driver.findElement(By.xpath("//card-detail[3]")).click();
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-card")) != null
        );
        driver.findElement(By.name("add-card")).click();
        */

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("dismiss")) != null
        );
        driver.findElement(By.name("dismiss")).click();

        //Create SynchronousSession in topic
        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("new-session")) != null
        );
        driver.findElement(By.name("new-session")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("synchronous")) != null
        );
        driver.findElement(By.name("synchronous")).click();

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("minNumberOfCardsPerParticipant")) != null
        );
        WebElement minCardsElement = driver.findElement(By.name("minNumberOfCardsPerParticipant"));
        InputHelper.sendKeys(driver, minCardsElement, "2");

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("maxNumberOfCardsPerParticipant")) != null
        );
        WebElement maxCardsElement = driver.findElement(By.name("maxNumberOfCardsPerParticipant"));
        InputHelper.sendKeys(driver, maxCardsElement, "4");

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("amountOfCircles")) != null
        );
        WebElement amountOfCirclesElement = driver.findElement(By.name("amountOfCircles"));
        InputHelper.sendKeys(driver, amountOfCirclesElement, "5");

        driver.findElement(By.name("participantsCanAddCards")).click();
        driver.findElement(By.name("cardCommentsAllowed")).click();

        WebElement startDateTimeElement = driver.findElement(By.name("startDateTime"));
        InputHelper.sendKeys(driver, startDateTimeElement, "03062016");
        InputHelper.sendKeys(driver,startDateTimeElement, Keys.TAB.toString());
        InputHelper.sendKeys(driver,startDateTimeElement,"1223");

        (new WebDriverWait(driver, 5)).until(
                (WebDriver d) -> d.findElement(By.name("add-session")) != null
        );
        driver.findElement(By.name("add-session")).click();

    }
}

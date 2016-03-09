package acceptancetest.helpers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InputHelper {
    /**
     * Workaround for a bug in the sendKeys() method.
     * sendKeys() often skips letters, entering wrong input. This method enters the letters of a string
     * one by one and waits for every letter to be entered before proceeding.
     * */
    public static void sendKeys(WebDriver driver, WebElement element, String text) {
        String filledInText = "";
        
        for (char c : text.toCharArray()) {
            element.sendKeys(c + "");
            filledInText += c;
            
            final String filledInTextFinal = filledInText;
            
            (new WebDriverWait(driver, 1)).until((WebDriver d) -> element.getAttribute("value").equals(filledInTextFinal));
        }
    }
}

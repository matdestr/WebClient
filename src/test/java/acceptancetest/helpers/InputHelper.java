package acceptancetest.helpers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InputHelper {
    public static void sendKeys(WebDriver driver, WebElement element, String text) {
        /*for (int i = 0; i < 3; i++) {
            element.clear();
            element.sendKeys(text);
            
            if (element.getAttribute("value").equals(text))
                break;
        }*/
        
        String filledInText = "";
        
        for (char c : text.toCharArray()) {
            element.sendKeys(c + "");
            filledInText += c;
            
            final String filledInTextFinal = filledInText;
            
            (new WebDriverWait(driver, 1)).until((WebDriver d) -> element.getAttribute("value").equals(filledInTextFinal));

            /*try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }
}

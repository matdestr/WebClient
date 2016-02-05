package be.kdg.kandoe;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by Vincent on 5-2-2016.
 */

public class ContextListener implements ApplicationContextAware {


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(s -> System.out.println(s));
    }
}

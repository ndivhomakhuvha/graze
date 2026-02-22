package com.graze.graze;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayJpaConfig implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!beanFactory.containsBeanDefinition("entityManagerFactory")
                || !beanFactory.containsBeanDefinition("flyway")) {
            return;
        }
        BeanDefinition entityManagerFactory = beanFactory.getBeanDefinition("entityManagerFactory");
        String[] existingDependsOn = entityManagerFactory.getDependsOn();
        if (existingDependsOn == null) {
            entityManagerFactory.setDependsOn("flyway");
        } else {
            String[] updatedDependsOn = new String[existingDependsOn.length + 1];
            System.arraycopy(existingDependsOn, 0, updatedDependsOn, 0, existingDependsOn.length);
            updatedDependsOn[existingDependsOn.length] = "flyway";
            entityManagerFactory.setDependsOn(updatedDependsOn);
        }
    }
}

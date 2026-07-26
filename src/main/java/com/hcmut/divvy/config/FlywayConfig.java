package com.hcmut.divvy.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Ensures Flyway runs migrations BEFORE Hibernate initializes/validates the database schema.
 */
@Configuration
public class FlywayConfig implements BeanFactoryPostProcessor {

    @Bean(name = "flyway", initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory.containsBeanDefinition("entityManagerFactory")) {
            BeanDefinition bd = beanFactory.getBeanDefinition("entityManagerFactory");
            String[] dependsOn = bd.getDependsOn();
            if (dependsOn == null) {
                bd.setDependsOn("flyway");
            } else {
                boolean alreadyContains = false;
                for (String dep : dependsOn) {
                    if ("flyway".equals(dep)) {
                        alreadyContains = true;
                        break;
                    }
                }
                if (!alreadyContains) {
                    String[] newDependsOn = Arrays.copyOf(dependsOn, dependsOn.length + 1);
                    newDependsOn[dependsOn.length] = "flyway";
                    bd.setDependsOn(newDependsOn);
                }
            }
        }
    }
}

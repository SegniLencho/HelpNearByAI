package com.helpnearby;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HelpNearByApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void webAutoConfigurationIsActive() {
        // Verify that Spring MVC (Web) auto-configuration is active
        assertThat(applicationContext.containsBean("dispatcherServlet")).isTrue();
        assertThat(applicationContext.getBean(DispatcherServlet.class)).isNotNull();
    }

    @Test
    void jpaAutoConfigurationIsActive() {
        // Verify that JPA auto-configuration is active
        assertThat(applicationContext.containsBean("entityManagerFactory")).isTrue();
    }

}

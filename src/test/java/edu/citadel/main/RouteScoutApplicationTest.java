package edu.citadel.main;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "google.maps.key=test-key-for-testing"
})
public class RouteScoutApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Verify that the application context loads successfully
        assertNotNull(applicationContext);
    }

    @Test
    void testMainApplicationClass() {
        // Verify that the main application class is correctly annotated
        assertTrue(RouteScoutApplication.class.isAnnotationPresent(
            org.springframework.boot.autoconfigure.SpringBootApplication.class
        ));
    }

    @Test
    void testComponentScanConfiguration() {
        // Verify that the component scan is configured for edu.citadel package
        org.springframework.context.annotation.ComponentScan componentScan =
            RouteScoutApplication.class.getAnnotation(
                org.springframework.context.annotation.ComponentScan.class
            );

        assertNotNull(componentScan);
        String[] basePackages = componentScan.value();
        assertEquals(1, basePackages.length);
        assertEquals("edu.citadel", basePackages[0]);
    }

    @Test
    void testApplicationContextContainsExpectedBeans() {
        // Verify that expected beans are loaded in the context
        assertTrue(applicationContext.containsBean("customHealthController"));
    }

    @Test
    void testRouteScoutAgentBeanExists() {
        // Verify that RouteScoutAgent bean exists
        assertTrue(applicationContext.containsBean("routeScoutAgent"));
    }

    @Test
    void testAPIKeysBeanExists() {
        // Verify that APIKeys bean exists
        assertTrue(applicationContext.containsBean("APIKeys"));
    }

    @Test
    void testRouteEndpointsBeanExists() {
        // Verify that RouteEndpoints bean exists
        assertTrue(applicationContext.containsBean("routeEndpoints"));
    }

    @Test
    void testAIEndpointsBeanExists() {
        // Verify that AIEndpoints bean exists
        assertTrue(applicationContext.containsBean("AIEndpoints"));
    }

    @Test
    void testApplicationContextIsActive() {
        // Verify that the application context is active
        if (applicationContext instanceof org.springframework.context.ConfigurableApplicationContext) {
            org.springframework.context.ConfigurableApplicationContext configurableContext =
                (org.springframework.context.ConfigurableApplicationContext) applicationContext;
            assertTrue(configurableContext.isActive());
        }
    }

    @Test
    void testApplicationContextBeanCount() {
        // Verify that the application context has loaded beans
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0, "Application context should have loaded beans");
    }

    @Test
    void testSpringBootApplicationAnnotation() {
        // Verify SpringBootApplication annotation is present
        org.springframework.boot.autoconfigure.SpringBootApplication annotation =
            RouteScoutApplication.class.getAnnotation(
                org.springframework.boot.autoconfigure.SpringBootApplication.class
            );
        assertNotNull(annotation);
    }
}
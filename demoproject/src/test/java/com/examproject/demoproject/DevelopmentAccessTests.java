package com.examproject.demoproject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
// MockMvc sends requests through Spring without opening a real network port.
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class DevelopmentAccessTests
{
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHome_withoutLogin_returnsBackendMessage() throws Exception
    {
        // Send a request without a user account or login session.
        MvcResult homeResult = this.mockMvc.perform(get("/")).andReturn();

        int actualStatus = homeResult.getResponse().getStatus();
        String actualMessage = homeResult.getResponse().getContentAsString();

        assertEquals(200, actualStatus); // HTTP 200 means the request succeeded.
        assertTrue(actualMessage.contains("Carpool backend is running"));

        MvcResult loginResult = this.mockMvc.perform(get("/login")).andReturn();

        // HTTP 404 confirms there is no generated login page in development.
        assertEquals(404, loginResult.getResponse().getStatus());
    }

    @Test
    void testPost_withoutCsrfToken_returnsForbidden() throws Exception
    {
        // Open access does not remove CSRF protection for state-changing requests.
        MvcResult result = this.mockMvc.perform(post("/example")).andReturn();

        int actualStatus = result.getResponse().getStatus();

        assertEquals(403, actualStatus); // HTTP 403 means the request was refused.
    }

    @Test
    void testDevelopmentSecurity_prodAlsoActive_doesNotCreateOpenAccessConfiguration()
    {
        // This small Spring context loads only the configuration being checked.
        ApplicationContextRunner contextRunner = new ApplicationContextRunner();
        contextRunner = contextRunner.withUserConfiguration(DevelopmentSecurityConfiguration.class);

        // Each with... method returns a new runner, so keep the returned value.
        contextRunner = contextRunner.withInitializer(context ->
        {
            context.getEnvironment().setActiveProfiles("dev", "prod");
        });

        contextRunner.run(context ->
        {
            boolean hasNoDevelopmentSecurity = context.getBeansOfType(SecurityFilterChain.class).isEmpty();

            // This checks exclusion of our development configuration, not full
            // production security or the future Microsoft sign-in.
            assertTrue(hasNoDevelopmentSecurity);
        });
    }
}
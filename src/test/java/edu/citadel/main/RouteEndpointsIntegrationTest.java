package edu.citadel.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.citadel.api.request.RouteRequestBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"GOOGLE_MAPS_KEY = ${GOOGLE_MAPS_KEY}"})
@AutoConfigureMockMvc
public class RouteEndpointsIntegrationTest {
    // Prepare to simulate HTTP requests with JSON input and output
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGenerateRoute_success() throws Exception {
        // Create a valid request to test
        RouteRequestBody routeRequestBody = new RouteRequestBody();
        routeRequestBody.setOrigin("Harbor Walk East - College of Charleston");
        routeRequestBody.setDestination("Thompson Hall - The Citadel");

        // Expect a response with the correct origin and destination that includes values for distance and duration as well as an instructions array
        mockMvc.perform(post("/route/generateRoute").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(routeRequestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.origin").value("Harbor Walk East - College of Charleston"))
                .andExpect(jsonPath("$.destination").value("Thompson Hall - The Citadel"))
                .andExpect(jsonPath("$.distance").exists())
                .andExpect(jsonPath("$.duration").exists())
                .andExpect(jsonPath("$.instructions").isArray());

    }

    @Test
    public void testGenerateRoute_fail() throws Exception {
        // Create an invalid request to test
        RouteRequestBody routeRequestBody = new RouteRequestBody();
        routeRequestBody.setOrigin("Harbor Walk East - College of Charleston");
        routeRequestBody.setDestination("Nowhere");

        // Expect a 5xx internal server error
        mockMvc.perform(post("/route/generateRoute").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(routeRequestBody)))
                .andExpect(status().isInternalServerError());
    }
}

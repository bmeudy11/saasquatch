package edu.citadel.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.citadel.api.request.DestinationRequestBody;
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
public class DestinationEndpointsTest {
    // Prepare to simulate HTTP requests with JSON input and output
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGenerateDestination_success() throws Exception {
        DestinationRequestBody destinationRequestBody = new DestinationRequestBody();
        destinationRequestBody.setOrigin("Harbor Walk East - College of Charleston");
        destinationRequestBody.setRadius(50000.0);

        // Expect a response that includes the correct origin, a destination type, destination name, distance, duration, and instructions
        mockMvc.perform(post("/destination/generateDestination").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(destinationRequestBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.routeDetails.origin").value("Harbor Walk East - College of Charleston"))
                .andExpect(jsonPath("$.destinationType").exists())
                .andExpect(jsonPath("$.routeDetails.destination").exists())
                .andExpect(jsonPath("$.routeDetails.distance").exists())
                .andExpect(jsonPath("$.routeDetails.duration").exists())
                .andExpect(jsonPath("$.routeDetails.instructions").isArray());

    }

    @Test
    public void testGenerateDestination_failure_invalidOrigin() throws Exception {
        DestinationRequestBody destinationRequestBody = new DestinationRequestBody();
        destinationRequestBody.setOrigin("Nowhere");
        destinationRequestBody.setRadius(50000.0);

        // Expect a response that identifies a "bad request."
        mockMvc.perform(post("/destination/generateDestination").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destinationRequestBody)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateDestination_failure_zeroRadius() throws Exception {
        DestinationRequestBody destinationRequestBody = new DestinationRequestBody();
        destinationRequestBody.setOrigin("Charleston, SC");
        destinationRequestBody.setRadius(0.0);

        // Expect a response that identifies a "not found" error.
        mockMvc.perform(post("/destination/generateDestination").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destinationRequestBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGenerateDestination_failure_noResults() throws Exception {
        DestinationRequestBody destinationRequestBody = new DestinationRequestBody();
        destinationRequestBody.setOrigin("Point Nemo"); // Remote oceanic location: yields no nearby destinations
        destinationRequestBody.setRadius(1.0);

        // Expect a response that identifies a "not found" error.
        mockMvc.perform(post("/destination/generateDestination").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destinationRequestBody)))
                .andExpect(status().isNotFound());
    }
}

package edu.citadel.api;

import edu.citadel.api.request.AISuggestionRequestBody;
import edu.citadel.api.request.AIPOIsRequestBody;
import edu.citadel.api.response.AISuggestionResponse;
import edu.citadel.api.response.AIPOIsResponse;
import edu.citadel.main.RouteScoutAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/suggest")
public class AIEndpoints {
    private static final Logger logger = LoggerFactory.getLogger(AIEndpoints.class);

    private final RouteScoutAgent routeScoutAgent;

    public AIEndpoints(RouteScoutAgent routeScoutAgent) {
        this.routeScoutAgent = routeScoutAgent;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSuggestions() {
        var response = new java.util.HashMap<String, Object>();
        response.put("status", "Service available");

        return ResponseEntity.ok(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> suggest(@RequestBody AISuggestionRequestBody request) {
        try {
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Message is required.\"}");
            }

            AISuggestionResponse response = routeScoutAgent.getSuggestions(request.getMessage());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error in suggest endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to get suggestions: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping(value = "/POIs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getPOIs(@RequestBody AIPOIsRequestBody request) {
        try {
            if ((request.getOrigin() == null || request.getOrigin().trim().isEmpty()) ||
                    (request.getDestination() == null || request.getDestination().trim().isEmpty())) {
                return ResponseEntity.badRequest().body("{\"error\": \"Origin and Destination is required.\"}");
            }

            var response = new java.util.HashMap<String, Object>();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in suggest/POIs endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"Failed to get points of interest: " + e.getMessage() + "\"}");
    }
    }


}

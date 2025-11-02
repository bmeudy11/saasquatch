package edu.citadel.api;

import edu.citadel.main.RouteScoutAgent;
import lombok.Data;
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
    public ResponseEntity<String> suggest(@RequestBody SuggestionRequest request) {
        try {
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Message is required.\"}");
            }

            String jsonResponse = routeScoutAgent.getSuggestions(request.getMessage());
            return ResponseEntity.ok(jsonResponse);

        } catch (Exception e) {
            logger.error("Error in suggest endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to get suggestions: " + e.getMessage() + "\"}");
        }
    }

    // DTO class for request body
    @Data
    public static class SuggestionRequest {
        private String message;
    }
}

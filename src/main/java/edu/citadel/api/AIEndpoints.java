package edu.citadel.api;


import com.google.genai.Client;
import edu.citadel.main.RouteScoutAgent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/suggest")
public class AIEndpoints {

    private final RouteScoutAgent routeScoutAgent;

    public AIEndpoints() {
        this.routeScoutAgent = new RouteScoutAgent();
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
            System.err.println("Error in suggest endpoint: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to get suggestions: " + e.getMessage() + "\"}");
        }
    }

    // DTO class for request body
    public static class SuggestionRequest {
        private String message;

        public SuggestionRequest() {}

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

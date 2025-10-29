package edu.citadel.api;


import com.google.genai.Client;
import edu.citadel.main.RouteScoutAgent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;



@RestController
@RequestMapping("/suggest")
public class AIEndpoints {

    private final RouteScoutAgent routeScoutAgent;

    public AIEndpoints() {
        // Initialize RouteScoutAgent with a Gemini client
        Client genaiClient = new Client();
        this.routeScoutAgent = new RouteScoutAgent(genaiClient);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSuggestions() {
        // Create a sample JSON response
        var response = new java.util.HashMap<String, Object>();
        response.put("status", "success");
        response.put("message", "Suggestions retrieved successfully");
        response.put("data", java.util.List.of(
                java.util.Map.of("id", 1, "suggestion", "Sample suggestion 1"),
                java.util.Map.of("id", 2, "suggestion", "Sample suggestion 2")
        ));

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

package edu.citadel.api;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/suggest")
public class AIEndpoints {

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

}

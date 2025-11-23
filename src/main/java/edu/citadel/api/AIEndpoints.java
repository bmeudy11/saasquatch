package edu.citadel.api;

import edu.citadel.api.request.AISuggestionRequestBody;
import edu.citadel.api.request.AIPOIsRequestBody;
import edu.citadel.api.response.AISuggestionResponse;
import edu.citadel.api.response.AIPOIsResponse;
import edu.citadel.dal.model.Suggestion;
import edu.citadel.main.RouteScoutAgent;
import edu.citadel.services.POISearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/suggest")
public class AIEndpoints {
    private static final Logger logger = LoggerFactory.getLogger(AIEndpoints.class);

    private final RouteScoutAgent routeScoutAgent;
    private final POISearchService poiSearchService;

    public AIEndpoints(RouteScoutAgent routeScoutAgent, POISearchService poiSearchService) {
        this.routeScoutAgent = routeScoutAgent;
        this.poiSearchService = poiSearchService;
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
            // Validate required fields
            if ((request.getOrigin() == null || request.getOrigin().trim().isEmpty()) ||
                    (request.getDestination() == null || request.getDestination().trim().isEmpty())) {
                return ResponseEntity.badRequest().body("{\"error\": \"Origin and Destination are required.\"}");
            }

            // If query is provided, use the enhanced POI search flow
            if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
                logger.info("Processing POI request with query: {}", request.getQuery());

                // Step 1: Parse the natural language query to get place types
                List<String> placeTypes = routeScoutAgent.parsePOIQuery(request.getQuery());
                logger.debug("Parsed place types: {}", placeTypes);

                // Step 2: Search for POIs along the route
                List<Suggestion> suggestions = poiSearchService.searchPOIsAlongRoute(
                        request.getOrigin(),
                        request.getDestination(),
                        placeTypes
                );

                // Step 3: Create response with suggestions
                AIPOIsResponse response = new AIPOIsResponse(new ArrayList<>(suggestions));
                logger.info("Returning {} POI suggestions for route from {} to {}", suggestions.size(),
                           request.getOrigin(), request.getDestination());
                return ResponseEntity.ok(response);

            } else {
                // Fallback to simple AI-generated POIs without specific query
                logger.info("Processing POI request without query - using basic AI suggestions");
                AIPOIsResponse response = routeScoutAgent.getAIPOIs(request.getOrigin(), request.getDestination());
                return ResponseEntity.ok(response);
            }

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"Invalid request: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Error in suggest/POIs endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to get points of interest: " + e.getMessage() + "\"}");
        }
    }


}

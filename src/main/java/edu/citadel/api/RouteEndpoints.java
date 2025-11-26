package edu.citadel.api;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import edu.citadel.api.request.RouteRequestBody;
import edu.citadel.api.response.RouteResponse;
import edu.citadel.dal.keys.APIKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/route")
public class RouteEndpoints {
    private final GeoApiContext context;
    private static final Logger logger = LoggerFactory.getLogger(RouteEndpoints.class);

    public RouteEndpoints(APIKeys apiKeys) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKeys.getMapsApiKey())
                .build();
    }

    private String makeHumanReadable(String instruction) {
        String cleaned = instruction
                // Add spaces around tags so words don't merge when tags are removed
                .replaceAll("(?<=[a-zA-Z])<", " <")
                .replaceAll(">(?=[a-zA-Z])", "> ");

        // Remove all HTML tags
        cleaned = cleaned.replaceAll("<[^>]*>", "");

        // Add missing punctuation between street names and "Destination..."
        cleaned = cleaned.replaceAll(
                "(?i)\\b(Ct|St|Rd|Dr|Blvd|Ln|Way|Ave|Pkwy|Cir|Pl|Ter)\\b(\\s*Destination)",
                "$1. $2"
        );

        // Remove spaces around slashes
        cleaned = cleaned.replaceAll("\\s*/\\s*", "/");

        // Normalize spaces
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        // Add sentence breaks for readability
        cleaned = cleaned.replaceAll(
                "(,\\s*)(?=(Turn|Merge|Keep|Continue|Head|Take|At|Slight|Follow))",
                ". "
        );
        cleaned = cleaned.replaceAll(
                "(\\s+and\\s+)(?=(merge|turn|continue|keep|head|take|follow))",
                ". "
        );

        // Remove unwanted spaces before commas or closing parentheses
        cleaned = cleaned.replaceAll("\\s+,", ",");
        cleaned = cleaned.replaceAll("\\s+\\)", ")");

        // Capitalize each sentence after a period
        cleaned = Arrays.stream(cleaned.split("(?<=\\.)\\s+"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));

        // Ensure each instruction ends with a period
        if (!cleaned.endsWith(".")) {
            cleaned += ".";
        }

        return cleaned;
    }

    // Method with primary direction generation logic (allows for internal use)
    public RouteResponse getDirections(RouteRequestBody body, String destinationName) throws Exception {
        // Convert the input list into a String of waypoints separated by the "|" character (needed for Google)
        StringBuilder waypoints = new StringBuilder();
        if (body.getWaypoints() != null) {
            for (int i = 0; i < body.getWaypoints().size(); i++) {
                waypoints.append(makeHumanReadable(body.getWaypoints().get(i)));
                if (i < body.getWaypoints().size() - 1) {
                    waypoints.append("|");
                }
            }
        }

        // Call the Google Maps Directions API for car travel and origin/destination
        DirectionsResult result = DirectionsApi.newRequest(context)
                .mode(TravelMode.DRIVING)
                .origin(body.getOrigin())
                .destination(body.getDestination())
                .waypoints(waypoints.toString())
                .await();

        // Add step-by-step driving instructions to instructions
        ArrayList<String> instructions = new ArrayList<>();
        Arrays.stream(result.routes[0].legs).forEach((leg ->
                Arrays.stream(leg.steps).forEach((step -> {
                    String instruction = makeHumanReadable(step.htmlInstructions);
                    instructions.add(instruction);
                }))
        ));

        // Create the response object
        RouteResponse response = new RouteResponse();
        response.setOrigin(body.getOrigin());
        response.setDestination(destinationName);

        // Calculate and save the total distance in miles
        double distanceInMeters = 0;
        for (int i = 0; i < result.routes[0].legs.length; i++) {
            distanceInMeters += result.routes[0].legs[i].distance.inMeters;
        }

        double distanceInMiles = distanceInMeters * 0.000621371;
        distanceInMiles = Math.round(distanceInMiles * 100.00) / 100.00;

        String distanceString = String.valueOf(distanceInMiles) + " mi";
        response.setDistance(distanceString);

        long totalSeconds = 0;
        for (int i = 0; i < result.routes[0].legs.length; i++) {
            totalSeconds += result.routes[0].legs[i].duration.inSeconds;
        }

        int seconds = (int) totalSeconds % 60;

        long totalMinutes = totalSeconds / 60;
        int minutes = (int) totalMinutes % 60;

        long totalHours = totalMinutes / 60;
        int hours = (int) totalHours;

        response.setDuration(hours + " hours " +  minutes + " minutes " + seconds + " seconds");
        response.setWaypoints(body.getWaypoints());
        response.setInstructions(instructions);

        // Add encoded polyline for map visualization
        if (result.routes.length > 0 && result.routes[0].overviewPolyline != null) {
            response.setEncodedPolyline(result.routes[0].overviewPolyline.getEncodedPath());
        }

        return response;
    }

    // Accepts a POST request with origin and destination Strings (e.g. "Charleston, SC")
    @PostMapping("/generateRoute")
    public ResponseEntity<?> generateRoute(@RequestBody RouteRequestBody body) {
        try {
            RouteResponse response = getDirections(body, body.getDestination());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in generateRoute: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

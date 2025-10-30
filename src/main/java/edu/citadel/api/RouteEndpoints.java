package edu.citadel.api;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import edu.citadel.api.request.RouteRequestBody;
import edu.citadel.api.response.RouteResponse;
import edu.citadel.dal.keys.APIKeys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/route")
public class RouteEndpoints {
    private final GeoApiContext context;

    public RouteEndpoints(APIKeys apiKeys) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKeys.getMapsApiKey())
                .build();
    }

    // Accepts a POST request with origin and destination Strings (e.g. "Charleston, SC")
    @PostMapping("/generateRoute")
    public ResponseEntity<?> generateRoute(@RequestBody RouteRequestBody body) {
        try {
            // Call the Google Maps Directions API for car travel and origin/destination
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .mode(TravelMode.DRIVING)
                    .origin(body.getOrigin())
                    .destination(body.getDestination())
                    .await();

            // Add step-by-step driving instructions to instructions
            ArrayList<String> instructions = new ArrayList<>();
            Arrays.stream(result.routes[0].legs).forEach((leg ->
                    Arrays.stream(leg.steps).forEach((step -> {
                        String noHtmlTags = step.htmlInstructions.replaceAll("<[^>]*>", "");
                        instructions.add(noHtmlTags);
                    }))
            ));

            // Create the response object
            RouteResponse response = new RouteResponse();
            response.setOrigin(body.getOrigin());
            response.setDestination(body.getDestination());
            response.setDistance(result.routes[0].legs[0].distance.humanReadable);
            response.setDuration(result.routes[0].legs[0].duration.humanReadable);
            response.setInstructions(instructions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

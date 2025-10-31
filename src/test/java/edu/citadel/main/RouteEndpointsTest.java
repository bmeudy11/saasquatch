package edu.citadel.main;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import edu.citadel.api.RouteEndpoints;
import edu.citadel.api.request.RouteRequestBody;
import edu.citadel.api.response.RouteResponse;
import edu.citadel.dal.keys.APIKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RouteEndpointsTest {

    private APIKeys apiKeys;
    private RouteEndpoints routeEndpoints;

    @BeforeEach
    void setUp() {
        apiKeys = mock(APIKeys.class);
        when(apiKeys.getMapsApiKey()).thenReturn("fake-api-key");
        routeEndpoints = new RouteEndpoints(apiKeys);
    }

    @Test
    void testGenerateRoute_success() throws Exception {
        // Prepare mock DirectionsResult
        DirectionsResult mockResult = new DirectionsResult();
        DirectionsRoute mockRoute = new DirectionsRoute();
        DirectionsLeg mockLeg = new DirectionsLeg();
        DirectionsStep mockStep1 = new DirectionsStep();
        DirectionsStep mockStep2 = new DirectionsStep();
        DirectionsStep mockStep3 = new DirectionsStep();

        mockStep1.htmlInstructions = "Head <b>north</b> on Meeting St";
        mockStep2.htmlInstructions = "Turn <b>left</b> on King St";
        mockStep3.htmlInstructions = "Turn <b>left</b> on Calhoun St <div>Destination is on the left</div>";

        mockLeg.steps = new DirectionsStep[]{mockStep1, mockStep2, mockStep3};
        mockLeg.distance = new Distance();
        mockLeg.distance.humanReadable = "5 mi";
        mockLeg.duration = new Duration();
        mockLeg.duration.humanReadable = "10 mins";

        mockRoute.legs = new DirectionsLeg[]{mockLeg};
        mockResult.routes = new DirectionsRoute[]{mockRoute};

        RouteRequestBody body = new RouteRequestBody();
        body.setOrigin("Charleston, SC");
        body.setDestination("Summerville, SC");

        // Mock static DirectionsApi.newRequest() call
        try (MockedStatic<DirectionsApi> mockedDirectionsApi = mockStatic(DirectionsApi.class)) {
            DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
            mockedDirectionsApi.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);

            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.origin(anyString())).thenReturn(mockRequest);
            when(mockRequest.destination(anyString())).thenReturn(mockRequest);
            when(mockRequest.await()).thenReturn(mockResult);

            // Act
            ResponseEntity<?> response = routeEndpoints.generateRoute(body);

            // Assert
            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.getBody() instanceof RouteResponse);

            RouteResponse routeResponse = (RouteResponse) response.getBody();
            assertEquals("Charleston, SC", routeResponse.getOrigin());
            assertEquals("Summerville, SC", routeResponse.getDestination());
            assertEquals("5 mi", routeResponse.getDistance());
            assertEquals("10 mins", routeResponse.getDuration());

            ArrayList<String> instructions = routeResponse.getInstructions();
            assertEquals(3, instructions.size());
            assertEquals("Head north on Meeting St.", instructions.get(0));
            assertEquals("Turn left on King St.", instructions.get(1));
            assertEquals("Turn left on Calhoun St. Destination is on the left.", instructions.get(2));
        }
    }

    @Test
    void testGenerateRoute_failure() throws Exception {
        RouteRequestBody body = new RouteRequestBody();
        body.setOrigin("Charleston, SC");
        body.setDestination("Nowhere");

        try (MockedStatic<DirectionsApi> mockedDirectionsApi = mockStatic(DirectionsApi.class)) {
            DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
            mockedDirectionsApi.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);

            when(mockRequest.mode(any(TravelMode.class))).thenReturn(mockRequest);
            when(mockRequest.origin(anyString())).thenReturn(mockRequest);
            when(mockRequest.destination(anyString())).thenReturn(mockRequest);
            when(mockRequest.await()).thenThrow(new RuntimeException("API Error"));

            ResponseEntity<?> response = routeEndpoints.generateRoute(body);

            assertEquals(500, response.getStatusCodeValue());
            assertTrue(response.getBody().toString().contains("API Error"));
        }
    }
}

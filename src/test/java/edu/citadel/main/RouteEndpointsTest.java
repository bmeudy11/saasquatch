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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RouteEndpointsTest {

    private RouteEndpoints routeEndpoints;

    @BeforeEach
    void setUp() {
        APIKeys apiKeys = mock(APIKeys.class);
        when(apiKeys.getMapsApiKey()).thenReturn("fake-api-key");
        routeEndpoints = new RouteEndpoints(apiKeys);
    }

    @Test
    void testGenerateRoute_success() throws Exception {
        // Prepare mock DirectionsResult
        DirectionsResult mockResult = new DirectionsResult();
        DirectionsRoute mockRoute = new DirectionsRoute();
        DirectionsLeg mockLeg = new DirectionsLeg();

        DirectionsStep step1 = new DirectionsStep();
        step1.htmlInstructions = "Head <b>north</b> on Meeting St";

        DirectionsStep step2 = new DirectionsStep();
        step2.htmlInstructions = "Turn <b>left</b> on King St";

        DirectionsStep step3 = new DirectionsStep();
        step3.htmlInstructions = "Turn <b>left</b> on Calhoun St <div>Destination is on the left</div>";

        mockLeg.steps = new DirectionsStep[]{step1, step2, step3};

        mockLeg.distance = new Distance();
        mockLeg.distance.inMeters = 8046; // 5 miles approx

        mockLeg.duration = new Duration();
        mockLeg.duration.inSeconds = 600; // 10 minutes

        mockRoute.legs = new DirectionsLeg[]{mockLeg};
        mockResult.routes = new DirectionsRoute[]{mockRoute};

        RouteRequestBody body = new RouteRequestBody();
        body.setOrigin("Charleston, SC");
        body.setDestination("Summerville, SC");

        try (MockedStatic<DirectionsApi> mockedDirectionsApi = mockStatic(DirectionsApi.class)) {
            DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
            mockedDirectionsApi.when(() -> DirectionsApi.newRequest(any(GeoApiContext.class))).thenReturn(mockRequest);

            when(mockRequest.mode(any())).thenReturn(mockRequest);
            when(mockRequest.origin(anyString())).thenReturn(mockRequest);
            when(mockRequest.destination(anyString())).thenReturn(mockRequest);
            when(mockRequest.waypoints(anyString())).thenReturn(mockRequest);
            when(mockRequest.await()).thenReturn(mockResult);

            ResponseEntity<?> response = routeEndpoints.generateRoute(body);

            assertEquals(200, response.getStatusCodeValue());
            RouteResponse routeResponse = (RouteResponse) response.getBody();

            assertEquals("Charleston, SC", routeResponse.getOrigin());
            assertEquals("Summerville, SC", routeResponse.getDestination());
            assertEquals("5.0 mi", routeResponse.getDistance());
            assertEquals("0 hours 10 minutes 0 seconds", routeResponse.getDuration());

            assertEquals(3, routeResponse.getInstructions().size());
            assertEquals("Head north on Meeting St.", routeResponse.getInstructions().get(0));
            assertEquals("Turn left on King St.", routeResponse.getInstructions().get(1));
            assertEquals("Turn left on Calhoun St. Destination is on the left.", routeResponse.getInstructions().get(2));
        }
    }

    @Test
    void testGenerateRoute_failure() throws Exception {
        RouteRequestBody body = new RouteRequestBody();
        body.setOrigin("Charleston, SC");
        body.setDestination("Nowhere");

        try (MockedStatic<DirectionsApi> mockedDirectionsApi = mockStatic(DirectionsApi.class)) {
            DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
            mockedDirectionsApi.when(() -> DirectionsApi.newRequest(any())).thenReturn(mockRequest);

            when(mockRequest.mode(any())).thenReturn(mockRequest);
            when(mockRequest.origin(anyString())).thenReturn(mockRequest);
            when(mockRequest.destination(anyString())).thenReturn(mockRequest);
            when(mockRequest.waypoints(anyString())).thenReturn(mockRequest);
            when(mockRequest.await()).thenThrow(new RuntimeException("API Error"));

            ResponseEntity<?> response = routeEndpoints.generateRoute(body);
            assertEquals(500, response.getStatusCodeValue());
            assertTrue(response.getBody().toString().contains("API Error"));
        }
    }

    @Test
    void testGenerateRoute_withWaypoints() throws Exception {
        DirectionsResult mockResult = new DirectionsResult();
        DirectionsRoute mockRoute = new DirectionsRoute();
        DirectionsLeg mockLeg = new DirectionsLeg();
        mockLeg.steps = new DirectionsStep[]{};
        mockLeg.distance = new Distance();
        mockLeg.distance.inMeters = 1000;
        mockLeg.duration = new Duration();
        mockLeg.duration.inSeconds = 60;
        mockRoute.legs = new DirectionsLeg[]{mockLeg};
        mockResult.routes = new DirectionsRoute[]{mockRoute};

        RouteRequestBody body = new RouteRequestBody();
        body.setOrigin("A");
        body.setDestination("B");
        body.setWaypoints(List.of("Stop1", "Stop2"));

        try (MockedStatic<DirectionsApi> mockedDirectionsApi = mockStatic(DirectionsApi.class)) {
            DirectionsApiRequest mockRequest = mock(DirectionsApiRequest.class);
            mockedDirectionsApi.when(() -> DirectionsApi.newRequest(any())).thenReturn(mockRequest);

            when(mockRequest.mode(any())).thenReturn(mockRequest);
            when(mockRequest.origin(anyString())).thenReturn(mockRequest);
            when(mockRequest.destination(anyString())).thenReturn(mockRequest);

            // FIXED LINE:
            when(mockRequest.waypoints(any(String[].class))).thenReturn(mockRequest);

            when(mockRequest.await()).thenReturn(mockResult);

            ResponseEntity<?> response = routeEndpoints.generateRoute(body);

            assertEquals(200, response.getStatusCodeValue());
            RouteResponse rr = (RouteResponse) response.getBody();
            assertEquals(List.of("Stop1", "Stop2"), rr.getWaypoints());
        }
    }

    @Test
    void testMakeHumanReadable() throws Exception {
        var method = RouteEndpoints.class.getDeclaredMethod("makeHumanReadable", String.class);
        method.setAccessible(true);

        String cleaned = (String) method.invoke(routeEndpoints, "Turn <b>left</b> on King St <div>Destination</div>");
        assertEquals("Turn left on King St. Destination.", cleaned);
    }
}

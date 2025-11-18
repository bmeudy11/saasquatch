package edu.citadel.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.citadel.api.AmenityEndpoints;
import edu.citadel.api.response.AmenityResponse;
import edu.citadel.api.GeolocationEndpoint;
import edu.citadel.api.request.AmenityRequest;
import edu.citadel.api.request.AmenityRequestCurrent;
import edu.citadel.dal.keys.APIKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AmenityEndpointsTest {

    private AmenityEndpoints amenityEndpoints;
    private APIKeys apiKeys;
    private GeolocationEndpoint geolocationEndpoint;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        apiKeys = mock(APIKeys.class);
        geolocationEndpoint = mock(GeolocationEndpoint.class);
        when(apiKeys.getMapsApiKey()).thenReturn("dummy-key");
        amenityEndpoints = Mockito.spy(new AmenityEndpoints(apiKeys, geolocationEndpoint));
        objectMapper = new ObjectMapper();
    }

    @Test
    void testNearestAmenity_Success() throws Exception {
        //test a location
        AmenityRequest request = new AmenityRequest();
        request.setLatitude(40.0);
        request.setLongitude(-73.0);
        request.setRadius(1000);
        request.setType("restaurant");

        //fake JSON response
        String mockResponseJson = """
            {
              "places": [
                {
                  "id": "abc123",
                  "displayName": {"text": "Test Restaurant"},
                  "formattedAddress": "123 Main St",
                  "rating": 4.5,
                  "userRatingCount": 200,
                  "location": {"latitude": 40.1, "longitude": -73.1},
                  "types": ["restaurant"]
                }
              ]
            }
        """;
        JsonNode mockResponse = objectMapper.readTree(mockResponseJson);

        doReturn(mockResponse).when(amenityEndpoints).makeSearchNearbyRequest(any(Map.class));

        ResponseEntity<?> response = amenityEndpoints.nearestAmenity(request);

        //asserts
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AmenityResponse);
        AmenityResponse body = (AmenityResponse) response.getBody();
        assertEquals(1, body.getAmenities().size());
        assertEquals("Test Restaurant", body.getAmenities().get(0).getName());
    }

    @Test
    void testNearestAmenity_Error() throws Exception {
        AmenityRequest request = new AmenityRequest();
        request.setLatitude(40);
        request.setLongitude(-73);

        //test exception
        doThrow(new RuntimeException("API failure"))
                .when(amenityEndpoints)
                .makeSearchNearbyRequest(any(Map.class));

        ResponseEntity<?> response = amenityEndpoints.nearestAmenity(request);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertTrue(body.get("error").contains("API failure"));
    }

    @Test
    void testNearestAmenityCurrentGeolocation_Success() throws Exception {
        Map<String, Object> geoLocationData = Map.of(
                "latitude", 40.0,
                "longitude", -73.0
        );
        ResponseEntity<?> mockGeoResponse = ResponseEntity.ok(geoLocationData);
        doReturn(mockGeoResponse).when(geolocationEndpoint).autoGeolocation();
        //test a location
        AmenityRequestCurrent request = new AmenityRequestCurrent();
        //request.setLatitude(40.0);
        //request.setLongitude(-73.0);
        request.setRadius(2000);
        request.setType("store");

        //fake JSON response
        String mockResponseJson = """
            {
              "places": [
                {
                  "id": "abc123",
                  "displayName": {"text": "Test Store"},
                  "formattedAddress": "123 Main St",
                  "rating": 4.5,
                  "userRatingCount": 200,
                  "location": {"latitude": 40.1, "longitude": -73.1},
                  "types": ["store"]
                }
              ]
            }
        """;
        JsonNode mockResponse = objectMapper.readTree(mockResponseJson);

        doReturn(mockResponse).when(amenityEndpoints).makeSearchNearbyRequest(any(Map.class));

        ResponseEntity<?> response = amenityEndpoints.currentGeolocation(request);

        //asserts
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AmenityResponse);
        AmenityResponse body = (AmenityResponse) response.getBody();
        assertEquals(1, body.getAmenities().size());
        assertEquals("Test Store", body.getAmenities().get(0).getName());
    }

    @Test
    void testNearestAmenityCurrentGeolocation_Error() throws Exception {
        // Mock a failed geolocation response
        Map<String, Object> errorData = Map.of("error", "Geolocation service unavailable");
        ResponseEntity<Map<String, Object>> mockGeoResponse = ResponseEntity.status(500).body(errorData);
        doReturn(mockGeoResponse).when(geolocationEndpoint).autoGeolocation();

        AmenityRequestCurrent request = new AmenityRequestCurrent();
        request.setRadius(2000);
        request.setType("store");

        //test exception
        doThrow(new RuntimeException("API failure"))
                .when(amenityEndpoints)
                .makeSearchNearbyRequest(any(Map.class));

        ResponseEntity<?> response = amenityEndpoints.currentGeolocation(request);

        // Asserts
        assertEquals(500, response.getStatusCodeValue());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertTrue(body.get("error").contains("Geolocation"));
    }

    @Test
    void testBuildSearchRequest_ContainsLocationAndRadius() {
        Map<String, Object> body = amenityEndpoints.buildSearchRequest(40.0, -73.0, 1000, List.of("restaurant"));

        assertTrue(body.containsKey("locationRestriction"));
        assertEquals(20, body.get("maxResultCount"));
        Map<String, Object> locationRestriction = (Map<String, Object>) body.get("locationRestriction");
        assertNotNull(locationRestriction.get("circle"));
    }

    @Test
    void testConvertJsonToAmenityList_ParsesFields() throws Exception {
        String json = """
            {"places": [
                {"id":"123", "displayName":{"text":"Cafe"}, "formattedAddress":"456 Road",
                 "rating":4.0, "userRatingCount":50,
                 "location":{"latitude":10.0,"longitude":20.0},
                 "types":["cafe"]}
            ]}
        """;
        JsonNode node = objectMapper.readTree(json);

        var result = amenityEndpoints.convertJsonToAmenityList(node);
        assertEquals(1, result.size());
        var dto = result.get(0);
        assertEquals("Cafe", dto.getName());
        assertEquals("456 Road", dto.getVicinity());
        assertEquals(4.0, dto.getRating());
    }
}

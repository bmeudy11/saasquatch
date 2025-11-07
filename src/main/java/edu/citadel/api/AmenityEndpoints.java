package edu.citadel.api;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import edu.citadel.api.request.AmenityRequest;
import edu.citadel.api.request.MultiTypeRequest;
import edu.citadel.dal.keys.APIKeys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/nearest")
public class AmenityEndpoints {
    private final GeoApiContext context;

    public AmenityEndpoints(APIKeys apiKeys) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKeys.getMapsApiKey())
                .build();
    }

    /**
     * Find amenities near a given location
     * POST /nearest/amenity
     * Body: {
     *   "latitude": 40.758896,
     *   "longitude": -73.985130,
     *   "radius": 1500,
     *   "type": "restaurant",
     *   "keyword": "pizza"
     * }
     */
    @PostMapping("/amenity")
    public ResponseEntity<?> nearestAmenity(@RequestBody AmenityRequest request) {
        try {
            LatLng location = new LatLng(request.getLatitude(), request.getLongitude());

            // Create nearby search request
            NearbySearchRequest searchRequest = PlacesApi.nearbySearchQuery(context, location);

            // Set radius (in meters)
            searchRequest.radius(request.getRadius() != 0 ? request.getRadius() : 1000);

            // Set place type if provided
            if (request.getType() != null && !request.getType().isEmpty()) {
                PlaceType placeType = getPlaceType(request.getType());
                if (placeType != null) {
                    searchRequest.type(placeType);
                }
            }

            // Set keyword if provided
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                searchRequest.keyword(request.getKeyword());
            }

            // Execute the search
            PlacesSearchResponse response = searchRequest.await();

            // Convert to DTOs
            List<AmenityDTO> amenities = convertToAmenityList(response.results);

            return ResponseEntity.ok(new AmenityResponse(amenities, response.nextPageToken));

        } catch (ApiException | InterruptedException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching amenities: " + e.getMessage()));
        }
    }

    /**
     * Search amenities by specific types
     * POST /nearest/amenity/types
     */
    @PostMapping("/amenity/types")
    public ResponseEntity<?> amenitiesByTypes(@RequestBody MultiTypeRequest request) {
        try {
            LatLng location = new LatLng(request.getLatitude(), request.getLongitude());
            List<AmenityDTO> allAmenities = new ArrayList<>();

            // Search for each type
            for (String typeStr : request.getTypes()) {
                PlaceType placeType = getPlaceType(typeStr);
                if (placeType != null) {
                    NearbySearchRequest searchRequest = PlacesApi.nearbySearchQuery(context, location)
                            .radius(request.getRadius() != 0 ? request.getRadius() : 1000)
                            .type(placeType);

                    PlacesSearchResponse response = searchRequest.await();
                    allAmenities.addAll(convertToAmenityList(response.results));
                }
            }

            return ResponseEntity.ok(new AmenityResponse(allAmenities, null));

        } catch (ApiException | InterruptedException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error fetching amenities by types: " + e.getMessage()));
        }
    }

    // Helper method to convert string to PlaceType enum
    private PlaceType getPlaceType(String type) {
        try {
            return PlaceType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Return null if invalid type
            return null;
        }
    }

    // Helper method to convert results to DTOs
    private List<AmenityDTO> convertToAmenityList(PlacesSearchResult[] results) {
        List<AmenityDTO> amenities = new ArrayList<>();

        if (results != null) {
            for (PlacesSearchResult result : results) {
                AmenityDTO dto = new AmenityDTO();
                dto.setPlaceId(result.placeId);
                dto.setName(result.name);
                dto.setVicinity(result.vicinity);
                dto.setRating(result.rating);
                dto.setUserRatingsTotal(result.userRatingsTotal);

                if (result.geometry != null && result.geometry.location != null) {
                    dto.setLatitude(result.geometry.location.lat);
                    dto.setLongitude(result.geometry.location.lng);
                }

                if (result.types != null && result.types.length > 0) {
                    dto.setTypes(result.types);
                }

                dto.setOpenNow(result.openingHours != null && result.openingHours.openNow);
                //dto.setPriceLevel(result.priceLevel != null ? result.priceLevel.ordinal() : -1);

                amenities.add(dto);
            }
        }

        return amenities;
    }
}

// Response DTOs
@Getter
@Setter
class AmenityDTO {
    private String placeId;
    private String name;
    private String vicinity;
    private float rating;
    private int userRatingsTotal;
    private double latitude;
    private double longitude;
    private String[] types;
    private boolean openNow;
    private int priceLevel;

}

@Getter
@Setter
class AmenityResponse {
    private List<AmenityDTO> amenities;
    private String nextPageToken;

    public AmenityResponse(List<AmenityDTO> amenities, String nextPageToken) {
        this.amenities = amenities;
        this.nextPageToken = nextPageToken;
    }

}

@Getter
@Setter
class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}
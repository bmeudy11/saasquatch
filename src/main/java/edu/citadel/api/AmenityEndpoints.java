package edu.citadel.api;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import edu.citadel.api.request.AmenityRequest;
import edu.citadel.api.request.MultiTypeRequest;
import edu.citadel.dal.keys.APIKeys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ...

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
                dto.setOpenNow(result.openingHours != null && result.openingHours.openNow);
                //dto.setHasRestroom(result.restroom); //should work is using the newest NearbySearch

                if (result.geometry != null && result.geometry.location != null) {
                    dto.setLatitude(result.geometry.location.lat);
                    dto.setLongitude(result.geometry.location.lng);
                }

                if (result.types != null && result.types.length > 0) {
                    dto.setTypes(result.types);
                }

                //Restroom and Restaurant Information
                try {
                    PlaceDetails details = PlacesApi.placeDetails(context, result.placeId).await();

                    dto.setWebsite(details.website.toString());
                    dto.setPhoneNumber(details.formattedPhoneNumber);
                    dto.setWheelchairAccessibleEntrance(details.wheelchairAccessibleEntrance);
                    dto.setPriceLevel(details.priceLevel != null ? details.priceLevel.ordinal() : -1);

                    boolean hasRestroom = false;
                    boolean isRestaurant = false;
                    boolean music = false;
                    if (details.reviews != null) {
                        for (PlaceDetails.Review review : details.reviews) {
                            String text = review.text != null ? review.text.toLowerCase() : "";
                            if (text.toLowerCase().contains("restroom") ||
                                    text.toLowerCase().contains("toilet") ||
                                    text.toLowerCase().contains("washroom") ||
                                    text.toLowerCase().contains("bathroom")) {
                                hasRestroom = true;
                                break;
                            }
                            if (result.types != null) {
                                for (String type : result.types) {
                                    if (type.equalsIgnoreCase("food") || type.equalsIgnoreCase("restaurant") || type.equalsIgnoreCase("cafe") || type.equalsIgnoreCase("bar")) {
                                        isRestaurant = true;
                                        break;
                                    }
                                }
                            }
                            if (isRestaurant) {
                                dto.setVegetarianFood(details.servesVegetarianFood);
                                dto.setDelivery(details.delivery);
                                dto.setTakeout(details.takeout);
                                dto.setDineIn(details.dineIn);
                                dto.setServesBreakfast(details.servesBreakfast);
                                dto.setServesLunch(details.servesLunch);
                                dto.setServesDinner(details.servesDinner);
                                dto.setReservable(details.reservable);
                            }
                            if (text.toLowerCase().contains("music")) {
                                music = true;
                                break;
                            }
                        }
                    }

                    dto.setMusic(music);
                    dto.setHasRestroom(hasRestroom);

                } catch (Exception e) {
                    dto.setHasRestroom(false);
                }
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
    private String website;
    private String phoneNumber;
    private boolean openNow;
    private boolean wheelchairAccessibleEntrance;
    private int priceLevel;
    private boolean hasRestroom;
    private boolean dineIn;
    private boolean takeout;
    private boolean delivery;
    private boolean servesBreakfast;
    private boolean servesLunch;
    private boolean servesDinner;
    private boolean vegetarianFood;
    private boolean music;
    private boolean reservable;
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
package edu.citadel.api.response;

import edu.citadel.api.AmenityDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AmenityResponseTest {

    @Test
    void testConstructor_WithValidData() {
        // Arrange
        AmenityDTO amenity1 = createMockAmenity("place1", "Restaurant A");
        AmenityDTO amenity2 = createMockAmenity("place2", "Restaurant B");
        List<AmenityDTO> amenities = Arrays.asList(amenity1, amenity2);
        String nextPageToken = "token123";

        // Act
        AmenityResponse response = new AmenityResponse(amenities, nextPageToken);

        // Assert
        assertNotNull(response);
        assertEquals(amenities, response.getAmenities());
        assertEquals(nextPageToken, response.getNextPageToken());
        assertEquals(2, response.getAmenities().size());
    }

    @Test
    void testConstructor_WithNullAmenities() {
        // Arrange
        String nextPageToken = "token123";

        // Act
        AmenityResponse response = new AmenityResponse(null, nextPageToken);

        // Assert
        assertNotNull(response);
        assertNull(response.getAmenities());
        assertEquals(nextPageToken, response.getNextPageToken());
    }

    @Test
    void testConstructor_WithEmptyList() {
        // Arrange
        List<AmenityDTO> emptyList = new ArrayList<>();
        String nextPageToken = "token123";

        // Act
        AmenityResponse response = new AmenityResponse(emptyList, nextPageToken);

        // Assert
        assertNotNull(response);
        assertEquals(emptyList, response.getAmenities());
        assertTrue(response.getAmenities().isEmpty());
        assertEquals(nextPageToken, response.getNextPageToken());
    }

    @Test
    void testConstructor_WithNullNextPageToken() {
        // Arrange
        AmenityDTO amenity = createMockAmenity("place1", "Restaurant A");
        List<AmenityDTO> amenities = Arrays.asList(amenity);

        // Act
        AmenityResponse response = new AmenityResponse(amenities, null);

        // Assert
        assertNotNull(response);
        assertEquals(amenities, response.getAmenities());
        assertNull(response.getNextPageToken());
    }

    @Test
    void testConstructor_WithBothNull() {
        // Act
        AmenityResponse response = new AmenityResponse(null, null);

        // Assert
        assertNotNull(response);
        assertNull(response.getAmenities());
        assertNull(response.getNextPageToken());
    }

    @Test
    void testSetAmenities() {
        // Arrange
        AmenityResponse response = new AmenityResponse(null, null);
        AmenityDTO amenity = createMockAmenity("place1", "Cafe");
        List<AmenityDTO> newAmenities = Arrays.asList(amenity);

        // Act
        response.setAmenities(newAmenities);

        // Assert
        assertEquals(newAmenities, response.getAmenities());
        assertEquals(1, response.getAmenities().size());
        assertEquals("Cafe", response.getAmenities().get(0).getName());
    }

    @Test
    void testSetNextPageToken() {
        // Arrange
        AmenityResponse response = new AmenityResponse(null, null);
        String newToken = "newToken456";

        // Act
        response.setNextPageToken(newToken);

        // Assert
        assertEquals(newToken, response.getNextPageToken());
    }

    @Test
    void testGetAmenities() {
        // Arrange
        AmenityDTO amenity1 = createMockAmenity("place1", "Store A");
        AmenityDTO amenity2 = createMockAmenity("place2", "Store B");
        AmenityDTO amenity3 = createMockAmenity("place3", "Store C");
        List<AmenityDTO> amenities = Arrays.asList(amenity1, amenity2, amenity3);
        AmenityResponse response = new AmenityResponse(amenities, "token");

        // Act
        List<AmenityDTO> result = response.getAmenities();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Store A", result.get(0).getName());
        assertEquals("Store B", result.get(1).getName());
        assertEquals("Store C", result.get(2).getName());
    }

    @Test
    void testGetNextPageToken() {
        // Arrange
        String expectedToken = "expectedToken789";
        AmenityResponse response = new AmenityResponse(new ArrayList<>(), expectedToken);

        // Act
        String actualToken = response.getNextPageToken();

        // Assert
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testMultipleSettersAndGetters() {
        // Arrange
        AmenityResponse response = new AmenityResponse(null, null);

        AmenityDTO amenity1 = createMockAmenity("place1", "Hotel A");
        List<AmenityDTO> list1 = Arrays.asList(amenity1);

        AmenityDTO amenity2 = createMockAmenity("place2", "Hotel B");
        List<AmenityDTO> list2 = Arrays.asList(amenity2);

        // Act & Assert - First set
        response.setAmenities(list1);
        response.setNextPageToken("token1");
        assertEquals(list1, response.getAmenities());
        assertEquals("token1", response.getNextPageToken());

        // Act & Assert - Second set (overwrite)
        response.setAmenities(list2);
        response.setNextPageToken("token2");
        assertEquals(list2, response.getAmenities());
        assertEquals("token2", response.getNextPageToken());
        assertEquals("Hotel B", response.getAmenities().get(0).getName());
    }

    @Test
    void testAmenitiesList_Mutability() {
        // Arrange
        List<AmenityDTO> amenities = new ArrayList<>();
        amenities.add(createMockAmenity("place1", "Park A"));
        AmenityResponse response = new AmenityResponse(amenities, "token");

        // Act - Modify the list through the getter
        List<AmenityDTO> retrievedList = response.getAmenities();
        retrievedList.add(createMockAmenity("place2", "Park B"));

        // Assert - Verify the change is reflected (since we're not using defensive copying)
        assertEquals(2, response.getAmenities().size());
    }

    /**
     * Helper method to create a mock AmenityDTO for testing
     */
    private AmenityDTO createMockAmenity(String placeId, String name) {
        AmenityDTO amenity = new AmenityDTO();
        amenity.setPlaceId(placeId);
        amenity.setName(name);
        amenity.setVicinity("123 Test Street");
        amenity.setRating(4.5f);
        amenity.setUserRatingsTotal(100);
        amenity.setLatitude(40.0);
        amenity.setLongitude(-73.0);
        amenity.setTypes(new String[]{"restaurant"});
        return amenity;
    }
}
package edu.citadel.dal.keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class APIKeysTest {

    private APIKeys apiKeys;

    @BeforeEach
    void setUp() {
        apiKeys = new APIKeys();
    }

    @Test
    void testGetMapsApiKey_withValidKey() {
        // Arrange
        String expectedKey = "test-api-key-12345";
        ReflectionTestUtils.setField(apiKeys, "mapsApiKey", expectedKey);

        // Act
        String actualKey = apiKeys.getMapsApiKey();

        // Assert
        assertNotNull(actualKey);
        assertEquals(expectedKey, actualKey);
    }

    @Test
    void testGetMapsApiKey_withNullKey() {
        // Arrange
        ReflectionTestUtils.setField(apiKeys, "mapsApiKey", null);

        // Act
        String actualKey = apiKeys.getMapsApiKey();

        // Assert
        assertNull(actualKey);
    }

    @Test
    void testGetMapsApiKey_withEmptyKey() {
        // Arrange
        String emptyKey = "";
        ReflectionTestUtils.setField(apiKeys, "mapsApiKey", emptyKey);

        // Act
        String actualKey = apiKeys.getMapsApiKey();

        // Assert
        assertNotNull(actualKey);
        assertEquals("", actualKey);
        assertTrue(actualKey.isEmpty());
    }

    @Test
    void testGetMapsApiKey_withLongKey() {
        // Arrange
        String longKey = "AIzaSyD1234567890abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ReflectionTestUtils.setField(apiKeys, "mapsApiKey", longKey);

        // Act
        String actualKey = apiKeys.getMapsApiKey();

        // Assert
        assertNotNull(actualKey);
        assertEquals(longKey, actualKey);
        assertEquals(longKey.length(), actualKey.length());
    }

    @Test
    void testGetMapsApiKey_withSpecialCharacters() {
        // Arrange
        String keyWithSpecialChars = "test-key_123!@#$%^&*()";
        ReflectionTestUtils.setField(apiKeys, "mapsApiKey", keyWithSpecialChars);

        // Act
        String actualKey = apiKeys.getMapsApiKey();

        // Assert
        assertNotNull(actualKey);
        assertEquals(keyWithSpecialChars, actualKey);
    }

    @Test
    void testGetMapsApiKey_defaultValueIsNull() {
        // Act - get the value without setting it first
        String actualKey = apiKeys.getMapsApiKey();

        // Assert - default value should be null
        assertNull(actualKey);
    }

    @Test
    void testGetMapsApiKey_multipleRetrievals() {
        // Arrange
        String expectedKey = "consistent-api-key";
        ReflectionTestUtils.setField(apiKeys, "mapsApiKey", expectedKey);

        // Act - retrieve multiple times
        String firstRetrieval = apiKeys.getMapsApiKey();
        String secondRetrieval = apiKeys.getMapsApiKey();
        String thirdRetrieval = apiKeys.getMapsApiKey();

        // Assert - all retrievals should return the same value
        assertEquals(expectedKey, firstRetrieval);
        assertEquals(expectedKey, secondRetrieval);
        assertEquals(expectedKey, thirdRetrieval);
        assertEquals(firstRetrieval, secondRetrieval);
        assertEquals(secondRetrieval, thirdRetrieval);
    }

    @Test
    void testAPIKeys_isNotNull() {
        // Assert that we can instantiate the class
        assertNotNull(apiKeys);
    }
}
package edu.citadel.api.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorResponseTest {

    @Test
    void testConstructor_WithValidErrorMessage() {
        // Arrange
        String errorMessage = "Something went wrong";

        // Act
        ErrorResponse response = new ErrorResponse(errorMessage);

        // Assert
        assertNotNull(response);
        assertEquals(errorMessage, response.getError());
    }

    @Test
    void testConstructor_WithNullErrorMessage() {
        // Act
        ErrorResponse response = new ErrorResponse(null);

        // Assert
        assertNotNull(response);
        assertNull(response.getError());
    }

    @Test
    void testConstructor_WithEmptyString() {
        // Arrange
        String emptyError = "";

        // Act
        ErrorResponse response = new ErrorResponse(emptyError);

        // Assert
        assertNotNull(response);
        assertEquals("", response.getError());
        assertTrue(response.getError().isEmpty());
    }

    @Test
    void testConstructor_WithLongErrorMessage() {
        // Arrange
        String longError = "This is a very long error message that contains detailed information " +
                "about what went wrong in the system. It includes stack traces, method names, " +
                "and various other diagnostic information that might be useful for debugging.";

        // Act
        ErrorResponse response = new ErrorResponse(longError);

        // Assert
        assertNotNull(response);
        assertEquals(longError, response.getError());
    }

    @Test
    void testConstructor_WithSpecialCharacters() {
        // Arrange
        String errorWithSpecialChars = "Error: 'Invalid input' @line 42! #Exception: $NULL_POINTER";

        // Act
        ErrorResponse response = new ErrorResponse(errorWithSpecialChars);

        // Assert
        assertNotNull(response);
        assertEquals(errorWithSpecialChars, response.getError());
    }

    @Test
    void testConstructor_WithMultilineMessage() {
        // Arrange
        String multilineError = "Error occurred:\n" +
                "Line 1: Connection failed\n" +
                "Line 2: Retrying...\n" +
                "Line 3: Operation aborted";

        // Act
        ErrorResponse response = new ErrorResponse(multilineError);

        // Assert
        assertNotNull(response);
        assertEquals(multilineError, response.getError());
        assertTrue(response.getError().contains("\n"));
    }

    @Test
    void testGetError() {
        // Arrange
        String expectedError = "Database connection failed";
        ErrorResponse response = new ErrorResponse(expectedError);

        // Act
        String actualError = response.getError();

        // Assert
        assertEquals(expectedError, actualError);
    }

    @Test
    void testSetError() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Initial error");
        String newError = "Updated error message";

        // Act
        response.setError(newError);

        // Assert
        assertEquals(newError, response.getError());
    }

    @Test
    void testSetError_ToNull() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Initial error");

        // Act
        response.setError(null);

        // Assert
        assertNull(response.getError());
    }

    @Test
    void testSetError_ToEmptyString() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Initial error");

        // Act
        response.setError("");

        // Assert
        assertEquals("", response.getError());
        assertTrue(response.getError().isEmpty());
    }

    @Test
    void testMultipleSettersAndGetters() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Error 1");

        // Act & Assert - First set
        assertEquals("Error 1", response.getError());

        // Act & Assert - Second set
        response.setError("Error 2");
        assertEquals("Error 2", response.getError());

        // Act & Assert - Third set
        response.setError("Error 3");
        assertEquals("Error 3", response.getError());

        // Act & Assert - Set to null
        response.setError(null);
        assertNull(response.getError());

        // Act & Assert - Set back to non-null
        response.setError("Error 4");
        assertEquals("Error 4", response.getError());
    }

    @Test
    void testErrorMessage_Immutability() {
        // Arrange
        String originalError = "Original error";
        ErrorResponse response = new ErrorResponse(originalError);

        // Act - Get the error and try to modify it (strings are immutable in Java)
        String retrievedError = response.getError();
        retrievedError = "Modified error"; // This doesn't affect the ErrorResponse

        // Assert - Original error is unchanged
        assertEquals(originalError, response.getError());
        assertNotEquals(retrievedError, response.getError());
    }

    @Test
    void testConstructor_WithCommonErrorMessages() {
        // Test common HTTP error scenarios
        String[] commonErrors = {
                "404 Not Found",
                "500 Internal Server Error",
                "401 Unauthorized",
                "403 Forbidden",
                "400 Bad Request",
                "503 Service Unavailable"
        };

        for (String error : commonErrors) {
            ErrorResponse response = new ErrorResponse(error);
            assertEquals(error, response.getError());
        }
    }

    @Test
    void testConstructor_WithJSONFormattedError() {
        // Arrange
        String jsonError = "{\"error\": \"Invalid request\", \"code\": 400}";

        // Act
        ErrorResponse response = new ErrorResponse(jsonError);

        // Assert
        assertNotNull(response);
        assertEquals(jsonError, response.getError());
    }

    @Test
    void testConstructor_WithUnicodeCharacters() {
        // Arrange
        String unicodeError = "Error: 用户未找到 (User not found) - Ошибка";

        // Act
        ErrorResponse response = new ErrorResponse(unicodeError);

        // Assert
        assertNotNull(response);
        assertEquals(unicodeError, response.getError());
    }
}
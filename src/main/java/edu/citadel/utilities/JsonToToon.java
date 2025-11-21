package edu.citadel.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipestanzani.jtoon.JToon;
import com.felipestanzani.jtoon.EncodeOptions;
import edu.citadel.api.response.AIPOIsResponse;
import edu.citadel.api.response.AISuggestionToonResponse;

import java.util.HashMap;
import java.util.List;

public class JsonToToon {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertJsonToToon(HashMap jsonInput) {
        try {
            String jsonString = objectMapper.writeValueAsString(jsonInput);
            return JToon.encodeJson(jsonString, new EncodeOptions());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert HashMap to JSON", e);
        }
    }

    public static String convertJsonToToon(AISuggestionToonResponse jsonInput) {
        try {
            String jsonString = objectMapper.writeValueAsString(jsonInput);
            return JToon.encodeJson(jsonString, new EncodeOptions());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert HashMap to JSON", e);
        }
    }

    public static String convertJsonToToon(AIPOIsResponse jsonInput) {
        try {
            String jsonString = objectMapper.writeValueAsString(jsonInput);
            return JToon.encodeJson(jsonString, new EncodeOptions());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert HashMap to JSON", e);
        }
    }

}

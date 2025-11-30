package edu.citadel.dal.keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class APIKeys {
    @Value("${google.maps.key}")
    private String mapsApiKey;

    @Value("${google.gemini.key}")
    private String geminiApiKey;

    public String getMapsApiKey() {
        return mapsApiKey != null ? mapsApiKey.trim() : null;
    }

    public String getGeminiApiKey() {
        return geminiApiKey != null ? geminiApiKey.trim() : null;
    }
}

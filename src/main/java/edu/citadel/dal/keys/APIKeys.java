package edu.citadel.dal.keys;

import lombok.Getter;

// Add all API Keys for external APIs here
@Getter
public enum APIKeys {
    MAPS_API_KEY("AIzaSyBjy_tKlWZTAXv4WYra5dr7e-0wcPEzvec");

    private final String key;

    APIKeys(String key) {
        this.key = key;
    }

}
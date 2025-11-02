package edu.citadel.dal.keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
@Getter
public class APIKeys {
    @Value("${google.maps.key}")
    private String mapsApiKey;
}

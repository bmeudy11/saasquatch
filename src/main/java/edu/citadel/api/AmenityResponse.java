package edu.citadel.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AmenityResponse {
    private List<AmenityDTO> amenities;
    private String nextPageToken;

    public AmenityResponse(List<AmenityDTO> amenities, String nextPageToken) {
        this.amenities = amenities;
        this.nextPageToken = nextPageToken;
    }
}

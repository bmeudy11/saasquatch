package edu.citadel.api.request;

import lombok.Getter;
import lombok.Setter;

// Request DTOs
@Getter
@Setter
public class AmenityRequest {
    private double latitude;
    private double longitude;
    private int radius;
    private String type;
    private String keyword;

}
package edu.citadel.api.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MultiTypeRequest {
    private double latitude;
    private double longitude;
    private int radius;
    private List<String> types;

}
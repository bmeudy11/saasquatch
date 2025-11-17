package edu.citadel.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DestinationRequestBody {
    private String origin;
    private double radius; // radius in meters between 0.0 and 50000.0
}
package edu.citadel.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {
    private String origin;

    private String destination;

    private String distance;

    private String duration;

    private ArrayList<String> instructions;
}

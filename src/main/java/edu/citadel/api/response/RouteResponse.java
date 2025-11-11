package edu.citadel.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {
    private String origin;

    private String destination;

    private String distance;

    private String duration;

    private List<String> waypoints;

    private ArrayList<String> instructions;
}

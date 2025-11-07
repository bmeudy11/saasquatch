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
public class RouteRequestBody {
    private String origin;

    private String destination;

    // Optional parameter for waypoints
    private List<String> waypoints;
}
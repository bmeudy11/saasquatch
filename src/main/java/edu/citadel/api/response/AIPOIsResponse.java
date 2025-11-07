package edu.citadel.api.response;

import edu.citadel.dal.model.Poi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPOIsResponse {
    private ArrayList<Poi> pois;
}

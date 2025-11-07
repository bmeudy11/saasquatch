package edu.citadel.api.response;

import edu.citadel.dal.model.Poi;
import edu.citadel.dal.model.Suggestion;
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
    private ArrayList<Suggestion> suggestions;

    // Alias for backward compatibility - pois and suggestions are the same
    public ArrayList<Poi> getPois() {
        ArrayList<Poi> pois = new ArrayList<>();
        if (suggestions != null) {
            for (Suggestion suggestion : suggestions) {
                Poi poi = new Poi();
                poi.setName(suggestion.getName());
                poi.setType(suggestion.getType());
                poi.setAddress(suggestion.getAddress());
                poi.setReason(suggestion.getReason());
                pois.add(poi);
            }
        }
        return pois;
    }

    public void setPois(ArrayList<Poi> pois) {
        this.suggestions = new ArrayList<>();
        if (pois != null) {
            for (Poi poi : pois) {
                Suggestion suggestion = new Suggestion();
                suggestion.setName(poi.getName());
                suggestion.setType(poi.getType());
                suggestion.setAddress(poi.getAddress());
                suggestion.setReason(poi.getReason());
                this.suggestions.add(suggestion);
            }
        }
    }
}

package edu.citadel.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmenityDTO {
    private String placeId;
    private String name;
    private String vicinity;
    private float rating;
    private int userRatingsTotal;
    private double latitude;
    private double longitude;
    private String[] types;
    private String website;
    private String phoneNumber;
    private boolean openNow;
    private boolean wheelchairAccessibleEntrance;
    private String priceLevelString;
    private boolean hasRestroom;
    private boolean dineIn;
    private boolean takeout;
    private boolean delivery;
    private boolean servesBreakfast;
    private boolean servesLunch;
    private boolean servesDinner;
    private boolean vegetarianFood;
    private boolean music;
    private boolean reservable;
}

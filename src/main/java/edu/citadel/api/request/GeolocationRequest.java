package edu.citadel.api.request;

import lombok.Getter;
import lombok.Setter;

// Request DTOs
@Getter
@Setter
public class GeolocationRequest {
    // cellTowers input
    private int cellId;
    private int newRadioCellId;
    private int locationAreaCode;
    private int mobileCountryCode;
    private int mobileNetworkCode;
    /*private int ageCell;              optional fields
    private int signalStrengthCell;*/

    // wifiAccessPoints input
    private String macAddress1; // wifiAccessPoint array must contain
    private String macAddress2; // two or more WiFi access point objects
    /*private int signalStrengthWifi;   optional fields
    private int signalToNoiseRatio;
    private int channel;
    private int ageWifi;*/
}
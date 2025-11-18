package edu.citadel.api.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GeolocationRequest {

    private int cellId;
    private int newRadioCellId;
    private int locationAreaCode;
    private int mobileCountryCode;
    private int mobileNetworkCode;

    private String macAddress1;         // wifiAccessPoint array must contain
    private String macAddress2;         // two or more WiFi access point objects

}
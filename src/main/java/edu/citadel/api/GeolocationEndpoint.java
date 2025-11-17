package edu.citadel.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.citadel.api.request.GeolocationRequest;
import edu.citadel.dal.keys.APIKeys;
import edu.citadel.services.WifiScannerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/current")
public class GeolocationEndpoint {
    private final String apiKey;
    private final ObjectMapper objectMapper;
    //private static String GEOLOCATION_API_URL = "https://www.googleapis.com/geolocation/v1/geolocate?key=";
    //private float latitude;
    //private float longitude;
    private final String geolocationUrl;
    private final WifiScannerService wifiScannerService;

    public GeolocationEndpoint(APIKeys apiKeys, WifiScannerService wifiScannerService) {
        this.apiKey = apiKeys.getMapsApiKey();
        this.objectMapper = new ObjectMapper();
        //GEOLOCATION_API_URL = GEOLOCATION_API_URL + this.apiKey;
        this.geolocationUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + this.apiKey;
        this.wifiScannerService = wifiScannerService;
    }

    @PostMapping("/geolocation")
    public ResponseEntity<?> currentGeolocation(@RequestBody GeolocationRequest request){
        try{
            Map<String, Object> requestBody = buildGeolocationRequest(
                    request.getMacAddress1(),
                    request.getMacAddress2()
            );

            JsonNode response = makeGeolocationRequest(requestBody);

            if (response.has("location")) {
                JsonNode location = response.get("location");
                float latitude = (float) location.get("lat").asDouble();
                float longitude = (float) location.get("lng").asDouble();

                Map<String, Object> result = Map.of(
                        "latitude", latitude,
                        "longitude", longitude
                );

                return ResponseEntity.ok(result);
                //return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Location not found in response."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching geolocation: " + e.getMessage()));
        }
    }
    @PostMapping("/geolocation/auto")
    public ResponseEntity<?> autoGeolocation() {
        try {
            // scan nearby Wi-Fi
            List<String> macs = wifiScannerService.getNearbyWifiMacs();

            // pick the first 2 (Google accepts 1–100 APs)
            String mac1 = macs.size() > 0 ? macs.get(0) : null;
            String mac2 = macs.size() > 1 ? macs.get(1) : null;

            // build Google request
            Map<String, Object> requestBody = buildGeolocationRequest(mac1, mac2);

            JsonNode response = makeGeolocationRequest(requestBody);

            if (response.has("location")) {
                JsonNode location = response.get("location");

                Map<String, Object> result = Map.of(
                        "latitude", location.get("lat").asDouble(),
                        "longitude", location.get("lng").asDouble(),
                        "accessPointsUsed", macs
                );

                return ResponseEntity.ok(result);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Google did not return a location."));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed: " + e.getMessage()));
        }
    }

    public Map<String, Object> buildGeolocationRequest(String macAddress1, String macAddress2) {
        Map<String, Object> requestBody = new HashMap<>();

        // Google's API requires wifiAccessPoints array
        ArrayList<Map<String, Object>> wifiPoints = new ArrayList<>();

        if (macAddress1 != null && !macAddress1.isBlank()) {
            wifiPoints.add(Map.of("macAddress", macAddress1));
        }
        if (macAddress2 != null && !macAddress2.isBlank()) {
            wifiPoints.add(Map.of("macAddress", macAddress2));
        }

        requestBody.put("wifiAccessPoints", wifiPoints);
        return requestBody;
    }

    public JsonNode makeGeolocationRequest(Map<String, Object> requestBody) throws Exception {
        URL url = new URL(geolocationUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(objectMapper.writeValueAsBytes(requestBody));
        }

        return readResponse(conn);
    }

    private JsonNode readResponse(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return objectMapper.readTree(response.toString());
            }
        } else {
            throw new Exception("Geolocation API request failed with response code: " + responseCode);
        }
    }
}

package edu.citadel.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.citadel.api.request.GeolocationRequest;
import edu.citadel.dal.keys.APIKeys;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/current")
public class GeolocationEndpoint {
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private static String GEOLOCATION_API_URL = "https://www.googleapis.com/geolocation/v1/geolocate?key=";
    private float latitude;
    private float longitude;


    public GeolocationEndpoint(APIKeys apiKeys) {
        this.apiKey = apiKeys.getMapsApiKey();
        this.objectMapper = new ObjectMapper();
        GEOLOCATION_API_URL = GEOLOCATION_API_URL + this.apiKey;
    }
    @PostMapping("/geolocation")
    public ResponseEntity<?> currentGeolocation(@RequestBody GeolocationRequest request){
        try{
            Map<String, Object> requestBody = buildGeolocationRequest(
                    request.getMacAddress1(),
                    request.getMacAddress2()
            );

            JsonNode response = makeGeolocationRequest(requestBody);

            // TODO: add return statement here
            /*
            * What Ouroion did 11/14/2025:
            *   -   The code here is modeled after the code in AmenityEndpoints.java
            *   -   Geolocation request body from GeolocationRequest.java
            *   -   Tried to build request (see functions below), need to check
            *   -   Tried to get response (see functions below), need to check
            *
            *   -   Delete this comment
            * */

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching geolocation: " + e.getMessage()));
        }
    }

    public Map<String, Object> buildGeolocationRequest(String  macAddress1, String macAddress2) {
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> device1 = new HashMap<>();
        Map<String, Object> device2 = new HashMap<>();

        device1.put("macAddress", macAddress1);
        device2.put("macAddress", macAddress2);
        requestBody.put("device1", device1);
        requestBody.put("device2", device2);

        return requestBody;
    }

    public JsonNode makeGeolocationRequest(Map<String, Object> requestBody) throws Exception {
        URL url = new URL(GEOLOCATION_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = objectMapper.writeValueAsString(requestBody);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
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
            throw new Exception("API request failed with response code: " + responseCode);
        }
    }
}

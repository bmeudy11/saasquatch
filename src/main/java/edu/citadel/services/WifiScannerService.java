package edu.citadel.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class WifiScannerService {

    public List<String> getNearbyWifiMacs() throws Exception {
        String command;

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            command = "netsh wlan show networks mode=bssid";
        } else if (os.contains("mac")) {
            command = "/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -s";
        } else {
            command = "sudo iwlist wlan0 scanning";
        }

        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        List<String> macs = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.matches(".*([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}.*")) {
                String mac = line.replaceAll(".*(([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}).*", "$1");
                macs.add(mac);
            }
        }

        return macs;
    }
}
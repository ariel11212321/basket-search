package com.example.a12thproject.classes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapHelper {

    public static double calculateDistance(Location location1, Location location2) {
        float distanceInMeters = location1.distanceTo(location2);
        double distanceInKm = distanceInMeters / 1000.0;
        return distanceInKm;
    }
    public static Location createLocationFromString(String locationString) {
        String[] coordinates = locationString.split(",");
        double latitude = Double.parseDouble(coordinates[0]);
        double longitude = Double.parseDouble(coordinates[1]);

        Location location = new Location("Current");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }

    public static String getLocation(Context c, String location) {
        if(location == null || c == null) {
            return "";
        }

        Geocoder geocoder = new Geocoder(c, Locale.getDefault());

        int i = 0;
        String lat = "";
        while(location.charAt(i) != ',') {
            lat += location.charAt(i);
            i++;
        }
        i++;
        String log = "";
        while(i < location.length()) {
            log += location.charAt(i);
            i++;
        }

        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(log), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String street = address.getThoroughfare();
                String number = address.getSubThoroughfare();
                String city = address.getLocality();
                String country = address.getCountryName();
                String fullAddress = street + " " + number + ", " + city + ", " + country;

                return fullAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "no location";
    }
}

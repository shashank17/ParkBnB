package com.shashankavusali.parkbnb;

import java.util.ArrayList;

/**
 * Created by ShashankAvusali on 2/25/17.
 */

public class SearchResponse {
    public String parkwhiz_url;
    public double lat;
    public double lng;
    public int locations;
    public double min_price;
    public double max_price;
    public double min_distance;
    public double max_distance;
    public ArrayList<ParkingSpot> parking_listings;
    public Amenities amenities;

    public SearchResponse(){
        parking_listings = new ArrayList<ParkingSpot>();
        amenities = new Amenities();
    }
}

package com.shashankavusali.parkbnb;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ParkingMapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private String response="";
    private String search_api = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Button button = (Button) findViewById(R.id.search_button);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                        ParkingMapActivity.this.onSearch();
                                      }
                                  });
                mapFragment.getMapAsync(this);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu,menu);
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_profile:
//                Intent intent = new Intent(this,)
                break;
            case R.id.menu_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_search:
                String parkwhizkey = getString(R.string.parkwhiz_key);


        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        SearchResponse searchResponse=new SearchResponse();

        if(response != null && response != ""){
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            searchResponse = gson.fromJson(response,SearchResponse.class);
            LatLng coords = new LatLng(0,0);
            for(ParkingSpot spot : searchResponse.parking_listings){
                coords = new LatLng(spot.lat,spot.lng);
                mMap.addMarker(new MarkerOptions().position(coords).title(spot.location_name));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));
        }
        if(response!=null && response != "")
            Log.i(ParkingMapActivity.class.getSimpleName(), searchResponse.parkwhiz_url);
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onSearch() {
        EditText text = (EditText) findViewById(R.id.editText);
        Geocoder geocoder = new Geocoder(this);
        try{
            List<Address> addresses =  geocoder.getFromLocationName(text.getText().toString(),10);
            Address address = addresses.get(0);
            double longitude = address.getLongitude();
            double latitude = address.getLatitude();
            this.search_api= "https://api.parkwhiz.com/search/?lat="+ latitude +"&lng=" + longitude +"&start=1488069833&end=1488080633&key=914cf6c520e8726c5d0f495adfb21421d12327bd";
            (new ParkwhizAsyncTask()).execute();
            text.setText("");
            text.clearFocus();
        }catch (Exception ex){

        }

    }

    private class ParkwhizAsyncTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            String inputLine;
            StringBuilder response = null;
            try {

                URL url = new URL(ParkingMapActivity.this.search_api);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    response = new StringBuilder();
                    while((inputLine = reader.readLine()) != null){
                        response.append(inputLine);
                    }
                    connection.disconnect();
                    reader.close();
                    Log.i(ParkingMapActivity.class.getSimpleName(),response.toString());
                }else{
                    Log.e(ParkingMapActivity.class.getSimpleName(),"There was an error while calling api");
                }
                return response.toString();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String response) {
            ParkingMapActivity.this.response = response;
            OnMapReadyCallback callback  = (OnMapReadyCallback)ParkingMapActivity.this;
            callback.onMapReady(ParkingMapActivity.this.mMap);
        }
    }
}

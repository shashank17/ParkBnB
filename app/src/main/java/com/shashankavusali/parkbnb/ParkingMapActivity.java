package com.shashankavusali.parkbnb;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;


public class ParkingMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String response="";
    private String search_api = "https://api.parkwhiz.com/search/?lat=41.8857256&lng=-87.6369590&start=1488069833&end=1488080633&key=914cf6c520e8726c5d0f495adfb21421d12327bd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }


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
                (new ParkwhizAsyncTask()).execute();

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
//        if(ContextCompat.checkSelfPermission(this,Manifest.permission.))
//        mMap.setMyLocationEnabled(true);
        if(response != null){

        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng sydney = new LatLng(0, 0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return response.toString();
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

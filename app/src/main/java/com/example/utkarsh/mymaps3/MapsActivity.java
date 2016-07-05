package com.example.utkarsh.mymaps3;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, Listener {
public class MapsActivity extends AppCompatActivity implements Listener, OnMapReadyCallback{
    private GoogleMap mMap, map;
    CameraUpdate cup;
    ArrayList<LatLng> markerPoints;
    public static final String TAG = "MAP DEMO";
    public static final float DEFAULT_ZOOM_LEVEL = 9.0f;
    public static LatLng loc;
    Location location1;

    ArrayList<LatLng> points = null;
    PolylineOptions lineOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        markerPoints = new ArrayList<LatLng>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        replaceMapFragment();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //Moving to a sample location
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.61, 77.20), DEFAULT_ZOOM_LEVEL));
        //replaceMapFragment();
    }


    public void onSearch(View view) {

        map.clear();
        //EditText location_tf = (EditText) findViewById(R.id.tfaddress);
        EditText location_sf = (EditText) findViewById(R.id.sflocation);

        String slocation = location_sf.getText().toString();

        //String location = location_tf.getText().toString();
        List<Address> addressList = null, addressList1 = null;

        /*if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        //replaceMapFragment();
        //Address address = addressList.get(0);

        Location address=location1;

        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Source"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        if (slocation != null || slocation.equals("")) {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                addressList1 = geocoder.getFromLocationName(slocation, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Address address1 = addressList1.get(0);
        LatLng latLng1 = new LatLng(address1.getLatitude(), address1.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng1).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
        /**********************************************************/


        // Getting URL to the Google Directions API
        String url = GetDataFromUrl.getDirectionsUrl(latLng, latLng1);
        GetDirections getDirections = new GetDirections(MapsActivity.this);
        getDirections.startGettingDirections(url);
    }




    //The task for getting directions ends up here...
    @Override
    public void onSuccessfullRouteFetch(final List<List<HashMap<String, String>>> result) {

        //if it takes a long time, we will do it in a seperate thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                MarkerOptions markerOptions = new MarkerOptions();
                // Traversing through all the routes
                for (List<HashMap<String, String>> path : result) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    int size = path.size();
                    // Get all the points for this route
                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(Color.RED);
                }

                //Do all UI operations on the UI thread only...
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Drawing polyline in the Google Map for the this route
                        mMap.addPolyline(lineOptions);
                    }
                });

            }
        }).start();

    }

    @Override
    public void onFail() {
        Log.i(TAG, "Failed to get directions from Google...");
    }

    @Override
    public void replaceMapFragment() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        // Enable Zoom
        map.getUiSettings().setZoomGesturesEnabled(true);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);

        //set "listener" for changing my location
        map.setOnMyLocationChangeListener(myLocationChangeListener());
    }

    @Override
    public GoogleMap.OnMyLocationChangeListener myLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                location1=location;

                Marker marker;
                marker = map.addMarker(new MarkerOptions().position(loc));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                //locationText.setText("You are at [" + longitude + " ; " + latitude + " ]");

                //get current address by invoke an AsyncTask object

            }
        };
    }
}

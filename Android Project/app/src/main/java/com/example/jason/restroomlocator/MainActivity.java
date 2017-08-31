package com.example.jason.restroomlocator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class MainActivity extends AppCompatActivity {
    private ListView lstMain;
    private ArrayList<Restroom> listOfRestrooms = new ArrayList<Restroom>();
    private String json_url = "http://13.59.61.32";
    private RestroomAdapter adapter;
    private RequestQueue mRequestQueue;
    private boolean initializeList = true;
    /*
    * The following are to enable GPS-awareness in the app.
    * */
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000; //10 seconds
    private long FASTEST_INTERVAL = 2000; //2 seconds
    private static final int FINE_LOCATION_PERMISSIONS_REQUEST = 1;
    private double latitudeHolder = 0, longitudeHolder = 0;

    public void getPermissionToUseGPS() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

            }
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original ACCESS_FINE_LOCATION request
        if (requestCode == FINE_LOCATION_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.topbarRegister:
                goToRegister();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Boilerplate Code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Adapter Code
        adapter = new RestroomAdapter(this, listOfRestrooms);
        lstMain = (ListView) findViewById(R.id.lstMain);
        lstMain.setAdapter(adapter);
        lstMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                Restroom tempRestroom = (Restroom) lstMain.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), tempRestroom.name, Toast.LENGTH_SHORT).show();
                Intent commentIntent = new Intent(getApplicationContext(), MoreInfoActivity.class);
                commentIntent.putExtra("restroom", tempRestroom);
                startActivity(commentIntent);
            }
        });
        //Networking Code
        mRequestQueue = NetworkController.getInstance(this.getApplicationContext()).getRequestQueue();

        //GPS Code
        getPermissionToUseGPS();
        startLocationUpdates();
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 0, 0);
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        latitudeHolder = location.getLatitude();
        longitudeHolder = location.getLongitude();
        if (initializeList) {
            fetchJsonArrayResponse();
            initializeList = false;
        }
    }

    private void fetchJsonObjectResponse() {

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, json_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        adapter.add(new Restroom(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
		/* Add your Requests to the RequestQueue to execute */
        mRequestQueue.add(req);
    }

    private void fetchJsonArrayResponse() {
        /*This constructor of JsonArrayRequest only supports GET requests via Volley*/
        JsonArrayRequest req = new JsonArrayRequest(json_url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList tempList = Restroom.fromJson(response);
                        Restroom.sortRestrooms(tempList, latitudeHolder, longitudeHolder);
                        adapter.addAll(tempList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
		/* Add req to the RequestQueue to execute */
        mRequestQueue.add(req);
    }


    public void goToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        Bundle b = new Bundle();
        b.putDouble("lat", latitudeHolder);
        b.putDouble("long", longitudeHolder);
        intent.putExtras(b);
        startActivity(intent);
    }

}

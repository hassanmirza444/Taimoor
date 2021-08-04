package com.example.taimoor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.taimoor.utilities.Constants;
import com.example.taimoor.R;
import com.example.taimoor.adapters.PlaceRecyclerViewAdapter;
import com.example.taimoor.models.MyPlaces;
import com.example.taimoor.models.Results;
import com.example.taimoor.remote.GoogleApiService;
import com.example.taimoor.remote.RetrofitBuilder;
import com.example.taimoor.interfaces.SelectedMall;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MallSelectionActivity extends AppCompatActivity implements SelectedMall {

    private Context mContext;
    private RecyclerView mRecyclerView;
    double latitude;
    double longitude;
    ProgressDialog progressDialog;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager lm;
    LocationManager locationManager;
    LatLng currentLocation = null;
    double lat = 0;
    double lng = 0;
    private String placeType = Constants.SEARCH_BY_PLACE;
    private GoogleApiService googleApiService;
    private MyPlaces myPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_malls_selection);

        mContext = this;
        mRecyclerView = findViewById(R.id.mRecyclerView);
        if (getIntent().getExtras().containsKey("location")) {
            currentLocation = getIntent().getParcelableExtra("location");
        }
        findViewById(R.id.done).setOnClickListener(v -> {

        });
        if (currentLocation != null) {
            lat = currentLocation.latitude;
            lng = currentLocation.longitude;
            getNearbyPlaces();
        }
        locationService();


    }

    private void getNearbyPlaces() {

        if (lat != 0 && lng != 0) {
            final ProgressDialog dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.show();

            String apiKey = Constants.GOOGLE_BROWSER_API_KEY;
            String url = buildUrl(lat, lng, apiKey);
            Log.d("finalUrl", url);

            googleApiService = RetrofitBuilder.builder().create(GoogleApiService.class);

            Call<MyPlaces> call = googleApiService.getMyNearByPlaces(url);

            call.enqueue(new Callback<MyPlaces>() {
                @Override
                public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                    //Log.d("MyPlaces", response.body().toString());
                    myPlaces = response.body();
                    Log.d("MyPlaces", myPlaces.getResults().get(0).toString());

                    dialog.dismiss();


                    PlaceRecyclerViewAdapter adapter = new PlaceRecyclerViewAdapter(mContext, myPlaces, lat, lng, MallSelectionActivity.this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<MyPlaces> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String buildUrl(double latitude, double longitude, String API_KEY) {
        StringBuilder urlString = new StringBuilder("api/place/search/json?");

        urlString.append("&location=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&radius=5000"); // places between 5 kilometer
        urlString.append("&types=" + placeType.toLowerCase());
        urlString.append("&sensor=false&key=" + API_KEY);

        return urlString.toString();
    }

    private void locationService() {

        lm = (LocationManager) MallSelectionActivity.this.getSystemService(Context.LOCATION_SERVICE);

        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please wait while fetching data from GPS .......");
            progressDialog.setCancelable(false);
            progressDialog.show();


            locationManager = (LocationManager) MallSelectionActivity.this.getSystemService(Context.LOCATION_SERVICE);

            final LocationListener locationListener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                progressDialog.dismiss();

                return;
            }

            progressDialog.dismiss();

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(MallSelectionActivity.this, location -> {

                if (location != null) {

                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    getNearbyPlaces();

                } else {
                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                    } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
                    }
                }
            });
        } else {
            Toast.makeText(mContext, "GPS off", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void getSelectedMallName(Results results) {
        Intent i = new Intent();
        i.putExtra("list", results);
        setResult(RESULT_OK, i);
        finish();
    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            longitude = loc.getLongitude();
            latitude = loc.getLatitude();

            lat = loc.getLatitude();
            lng = loc.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
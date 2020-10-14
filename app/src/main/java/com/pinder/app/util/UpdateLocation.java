package com.pinder.app.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UpdateLocation {
    public LatLng loc;
    public MutableLiveData<LatLng> locLiveData = new MutableLiveData<>();
    public void updateLocation(Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference usersDb;
        String currentUID = mAuth.getCurrentUser().getUid();
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference myRef = usersDb.child(currentUID).child("location");
        //geofire
        DatabaseReference geofire = FirebaseDatabase.getInstance().getReference().child("Geofire");
        DatabaseReference geofireUser = geofire.child(currentUID);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                Log.d("updateLocation", "location: " + location);
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        loc = new LatLng((double) addresses.get(0).getLatitude(), (double) addresses.get(0).getLongitude());
                        myRef.child("longitude").setValue(addresses.get(0).getLongitude());
                        myRef.child("latitude").setValue(addresses.get(0).getLatitude());
                        Log.d("updateLocation", "myLatitude updateLocation: " + loc.latitude);
                        Log.d("updateLocation", "myLongitude updateLocation: " + loc.longitude);
                        GeoFire geoFire = new GeoFire(geofire);
                        geoFire.setLocation(geofireUser.getKey(), new GeoLocation(loc.latitude, loc.longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                            }
                        });
                        if (addresses.get(0).getCountryName() != null) {
                            myRef.child("countryName").setValue(addresses.get(0).getCountryName());
                        } else {
                            myRef.child("countryName").setValue("Not found");
                        }
                        if (addresses.get(0).getLocality() != null) {
                            myRef.child("locality").setValue(addresses.get(0).getLocality());
                        } else {
                            myRef.child("locality").setValue("Not found");
                        }
                        if (addresses.get(0).getAddressLine(0) != null) {
                            myRef.child("address").setValue(addresses.get(0).getAddressLine(0));
                        } else {
                            myRef.child("address").setValue("Not found");
                        }
                        locLiveData.postValue(loc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    geofireUser.child("l").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Log.d("updateLocation", "onDataChange: " + snapshot);
                                Log.d("updateLocation", "onDataChange: " + snapshot.getKey());
                                double lon = (Double.parseDouble(snapshot.child("0").getValue().toString()));
                                double lat = (Double.parseDouble(snapshot.child("1").getValue().toString()));
                                loc = new LatLng(lat,lon);
                                locLiveData.postValue(loc);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }
}

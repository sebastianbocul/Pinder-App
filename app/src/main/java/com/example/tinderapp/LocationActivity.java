package com.example.tinderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    Button btLocation;
    TextView textView1,textView2,textView3,textView4,textView5, name;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap myGoogleMap;

    private FirebaseAuth mAuth;
    private DatabaseReference usersDb;
    public String currentUID;
    private DatabaseReference myRef;
    FirebaseDatabase database;
    DatabaseReference usersReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();


//        btLocation = (Button) findViewById(R.id.bt_location);
//        textView1 =(TextView) findViewById(R.id.text_view1);
//        textView2 =(TextView) findViewById(R.id.text_view2);
//        textView3 =(TextView) findViewById(R.id.text_view3);
//        textView4 =(TextView) findViewById(R.id.text_view4);
//        textView5 =(TextView) findViewById(R.id.text_view5);
        name = (TextView) findViewById(R.id.name);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(LocationActivity.this);
//        btLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //checking permission
//
//
//            }
//        });

    }


    private boolean mapRdy= false;
    public void onMapReady(GoogleMap googleMap) {

        myGoogleMap = googleMap;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //get my location
                mAuth = FirebaseAuth.getInstance();
                currentUID = mAuth.getCurrentUser().getUid();
                String lat = dataSnapshot.child("Users").child(currentUID).child("location").child("latitude").getValue().toString();
                String lon = dataSnapshot.child("Users").child(currentUID).child("location").child("longitude").getValue().toString();
                final double myLatitude = Double.parseDouble(lat);
                final double myLongitude = Double.parseDouble(lon);
                LatLng latLng = new LatLng(myLatitude,myLongitude);
                if(mapRdy==false) {
                    myGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    myGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    mapRdy=true;
                    //
                }
                //set my marker at start
                MarkerOptions markerOptionsFB;
                markerOptionsFB= new MarkerOptions().position(latLng).title("I'm here").icon(BitmapDescriptorFactory.defaultMarker(300));
                Marker myMarker = myGoogleMap.addMarker(markerOptionsFB);
                myMarker.setTag(currentUID.trim());

              //  name.setText(dataSnapshot.child("Users").child(currentUID).child("name").getValue().toString());
//                textView1.setText(dataSnapshot.child("Users").child(currentUID).child("location").child("latitude").getValue().toString());
//                textView2.setText(dataSnapshot.child("Users").child(currentUID).child("location").child("longitude").getValue().toString());
//                textView3.setText(dataSnapshot.child("Users").child(currentUID).child("location").child("address").getValue().toString());
//                textView4.setText(dataSnapshot.child("Users").child(currentUID).child("location").child("countryName").getValue().toString());
//                textView5.setText(dataSnapshot.child("Users").child(currentUID).child("location").child("locality").getValue().toString());



                myGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                      //  name.setText(dataSnapshot.child("Users").child(marker.getTag().toString()).child("name").getValue().toString());
//                        textView1.setText(dataSnapshot.child("Users").child(marker.getTag().toString()).child("location").child("latitude").getValue().toString());
//                        textView2.setText(dataSnapshot.child("Users").child(marker.getTag().toString()).child("location").child("longitude").getValue().toString());
//                        textView3.setText(dataSnapshot.child("Users").child(marker.getTag().toString()).child("location").child("address").getValue().toString());
//                        textView4.setText(dataSnapshot.child("Users").child(marker.getTag().toString()).child("location").child("countryName").getValue().toString());
//                        textView5.setText(dataSnapshot.child("Users").child(marker.getTag().toString()).child("location").child("locality").getValue().toString());

                        return false;
                    }
                });
/*          //show everyone location
                for(DataSnapshot ds : dataSnapshot.child("Users").getChildren()){
                    String userName = ds.child("name").getValue().toString();
                    double latitude = Double.parseDouble(ds.child("location").child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(ds.child("location").child("longitude").getValue().toString());
                    double distance = distance(myLatitude,myLongitude,latitude,longitude);
                    LatLng latLngFB = new LatLng(latitude,longitude);

                    MarkerOptions markerOptionsFB;

                    if (ds.getKey().equals(currentUID)) {
                        markerOptionsFB= new MarkerOptions().position(latLngFB).title("I'm here").icon(BitmapDescriptorFactory.defaultMarker(300));
                    }
                    else {
                        markerOptionsFB= new MarkerOptions().position(latLngFB).title(userName + "\r" + Math.round(distance) + "km");
                    }
                    Marker myMarker = myGoogleMap.addMarker(markerOptionsFB);
                    myMarker.setTag(ds.getKey().trim());
                }

            }*/

            //matches only location
                for(final DataSnapshot ds : dataSnapshot.child("Users").child(currentUID).child("connections").child("matches").getChildren()){

                    String userId = ds.getKey().toString();
                    usersReference= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("showMyLocation").getValue().toString().equals("false")){
                                return;
                            }
                            String userName;
                            if(!dataSnapshot.child("name").exists()){
                              return;
                            }
                            userName = dataSnapshot.child("name").getValue().toString();

                            double latitude = Double.parseDouble(dataSnapshot.child("location").child("latitude").getValue().toString());
                            double longitude = Double.parseDouble(dataSnapshot.child("location").child("longitude").getValue().toString());
                            double distance = distance(myLatitude,myLongitude,latitude,longitude);
                            LatLng latLngFB = new LatLng(latitude,longitude);

                            MarkerOptions markerOptionsFB;
                            markerOptionsFB= new MarkerOptions().position(latLngFB).title(userName + "\r" + Math.round(distance) + "km");

                            Marker myMarker = myGoogleMap.addMarker(markerOptionsFB);
                            myMarker.setTag(dataSnapshot.getKey().trim());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 44:
                if(grantResults.length>0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED){
                //    getLocation();
                }
                break;


        }
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515* 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
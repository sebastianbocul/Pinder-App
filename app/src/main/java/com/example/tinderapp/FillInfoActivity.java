package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FillInfoActivity extends AppCompatActivity {

    private Button mRegister;

    private EditText mEmail, mPassword, mName,mRepeatPassword;

    private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private Context context=FillInfoActivity.this;

    private FusedLocationProviderClient fusedLocationProviderClient;
   // private  RadioButton radioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_info);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mRegister = (Button) findViewById(R.id.register);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mName = (EditText) findViewById(R.id.name);
       // int selectedId = mRadioGroup.getCheckedRadioButtonId();
       // radioButton = (RadioButton) findViewById(selectedId);

        mName.setText(getFirstName(user.getDisplayName()));


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                final String name = mName.getText().toString();
                final RadioButton radioButton = (RadioButton) findViewById(selectedId);


                if(mRadioGroup.getCheckedRadioButtonId()==-1){
                    Toast.makeText(FillInfoActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(name.isEmpty()){
                    Toast.makeText(FillInfoActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(ActivityCompat.checkSelfPermission(FillInfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    updateLocation();
                }
                else {
                    ActivityCompat.requestPermissions(FillInfoActivity.this, new  String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }
                updateDb();


                changeActivty();
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
       // mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        deleteUser();
        logoutUser();
    }

    private void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
    public void logoutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(FillInfoActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    private void changeActivty(){
        Intent intent = new Intent(FillInfoActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
        return;

    }


    private void updateDb() {
        /*
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            final DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
            currentUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("name").getValue()==null){

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }*/

        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        String name = mName.getText().toString();
        String gender = radioButton.getText().toString();

        Map userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("sex",gender);
        mUserDatabase.updateChildren(userInfo);
    }

    private String getFirstName(String displayName) {
        String[] words = displayName.split(" ");
        return words[0];
    }
    public void updateLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null){
                    try {
                        Geocoder geocoder = new Geocoder(FillInfoActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        String currentUID = mAuth.getCurrentUser().getUid();
                        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
                        DatabaseReference myRef = usersDb.child(currentUID).child("location");

                        myRef.child("longitude").setValue(addresses.get(0).getLongitude());
                        myRef.child("latitude").setValue(addresses.get(0).getLatitude());

                        if(addresses.get(0).getCountryName()!=null){
                            myRef.child("countryName").setValue(addresses.get(0).getCountryName());
                        }else {
                            myRef.child("countryName").setValue("Not found");
                        }

                        if(addresses.get(0).getLocality()!=null){
                            myRef.child("locality").setValue(addresses.get(0).getLocality());
                        }else {
                            myRef.child("locality").setValue("Not found");
                        }

                        if(addresses.get(0).getAddressLine(0)!=null){
                            myRef.child("address").setValue(addresses.get(0).getAddressLine(0));
                        }else {
                            myRef.child("address").setValue("Not found");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
}

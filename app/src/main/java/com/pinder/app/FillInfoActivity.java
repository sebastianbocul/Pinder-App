package com.pinder.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.pinder.app.MyFunctions.StringDateToAge;
import com.pinder.app.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FillInfoActivity extends AppCompatActivity {
    private Button mRegister;
    private EditText mEmail, mPassword, mName, mRepeatPassword;
    private RadioGroup mRadioGroup;
    private boolean dateValid = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private Context context = FillInfoActivity.this;
    private EditText date;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView title;
    private int dd, mm, yyyy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_info);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mRegister = findViewById(R.id.register);
        mRadioGroup = findViewById(R.id.radioGroup);
        mName = findViewById(R.id.name);
        mName.setText(getFirstName(user.getDisplayName()));
        date.addTextChangedListener(new TextWatcher() {
            String clean;
            String cleanC;
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().equals(current)) {
                        dateValid = false;
                        clean = s.toString().replaceAll("[^\\d.]", "");
                        cleanC = current.replaceAll("[^\\d.]", "");
                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;
                        if (clean.length() < 8) {
                            clean = clean + ddmmyyyy.substring(clean.length());
                        } else {
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day = Integer.parseInt(clean.substring(0, 2));
                            int mon = Integer.parseInt(clean.substring(2, 4));
                            int year = Integer.parseInt(clean.substring(4, 8));
                            if (mon > 12) mon = 12;
                            if (mon == 0) mon = 1;
                            cal.set(Calendar.MONTH, mon - 1);
                            year = (year < 1900) ? 1900 : (year > 2019) ? 2019 : year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012
                            dateValid = true;
                            day = (day < 1) ? 1 : (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                            dd = day;
                            mm = mon;
                            yyyy = year;
                            clean = String.format("%02d%02d%02d", day, mon, year);
                        }
                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));
                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        date.setText(current);
                        date.setSelection(sel < current.length() ? sel : current.length());
                    }
                } catch (Exception e) {
                    dateValid = false;
                    Toast.makeText(FillInfoActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    date.setText("");
                }
            }

            //set max lines in descriptions field
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                int age = new StringDateToAge().stringDateToAge(date.getText().toString());
                final String name = mName.getText().toString();
                if (!dateValid == true) {
                    Toast.makeText(FillInfoActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(age<18){
                    Toast.makeText(FillInfoActivity.this, "You must be 18+", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(FillInfoActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.isEmpty()) {
                    Toast.makeText(FillInfoActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ActivityCompat.checkSelfPermission(FillInfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                } else {
                    ActivityCompat.requestPermissions(FillInfoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
                try {
                    updateDb();
                    changeActivty();
                } catch (Exception e) {
                    Toast.makeText(FillInfoActivity.this, "Opps something went wrong", Toast.LENGTH_SHORT).show();
                }
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

    public void onBack(View view) {
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

    private void changeActivty() {
        Intent intent = new Intent(FillInfoActivity.this, MainFragmentMenager.class);
        startActivity(intent);
        finish();
        return;
    }

    private void updateDb() {
        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = findViewById(selectedId);
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        String name = mName.getText().toString();
        String gender = radioButton.getText().toString();
        String dateOfBirth = date.getText().toString();
        Map tagsMap = new HashMap<>();
        Map tagInfo = new HashMap<>();
        Map userInfo = new HashMap<>();
        tagInfo.put("minAge", "18");
        tagInfo.put("maxAge", "99");
        tagInfo.put("maxDistance", "100");
        if (radioButton.getText().toString().equals("Male")) {
            tagInfo.put("gender", "Female");
        } else tagInfo.put("gender", "Male");
        tagsMap.put("default", tagInfo);
        userInfo.put("name", name);
        userInfo.put("sex", gender);
        userInfo.put("dateOfBirth", dateOfBirth);
        userInfo.put("tags", tagsMap);
        userInfo.put("profileImageUrl", "default");
        userInfo.put("showMyLocation", true);
        mUserDatabase.updateChildren(userInfo);
        DatabaseReference tags = FirebaseDatabase.getInstance().getReference().child("Tags");
        tags.child("default").child(userId).setValue(true);
        Toast.makeText(FillInfoActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
    }

    private String getFirstName(String displayName) {
        String[] words = displayName.split(" ");
        return words[0];
    }

    public void updateLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(FillInfoActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String currentUID = mAuth.getCurrentUser().getUid();
                        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
                        DatabaseReference myRef = usersDb.child(currentUID).child("location");
                        myRef.child("longitude").setValue(addresses.get(0).getLongitude());
                        myRef.child("latitude").setValue(addresses.get(0).getLatitude());
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

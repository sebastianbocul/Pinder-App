package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Credentials;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.tinderapp.Matches.MatchesActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userId;
    private Context context=SettingsActivity.this;
    private Button logoutUser,deleteUser;
    private StorageReference filePath;
    private Switch mapLocationSwitch;
    private EditText date;
    private boolean showMapLocation,onStartShowMapLocation;
    private boolean dateValid = false;
    private int dd,mm,yyyy;
    private String dateOfBirth,onStartDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        userId= mAuth.getCurrentUser().getUid();

        DatabaseReference myDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        mapLocationSwitch = findViewById(R.id.mapLocationSwitch);


        logoutUser=findViewById(R.id.logoutUser);
        deleteUser=findViewById(R.id.deleteUser);

        date = (EditText) findViewById(R.id.date);


        myDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dateOfBirth = dataSnapshot.child("dateOfBirth").getValue().toString();
                    onStartDateOfBirth =dateOfBirth;
                    date.setText(dateOfBirth);

                    if (dataSnapshot.child("showMyLocation").getValue().toString().equals("true")) {
                        showMapLocation = true;
                        onStartShowMapLocation=true;
                        mapLocationSwitch.setChecked(showMapLocation);
                    }else {
                        showMapLocation = false;
                        onStartShowMapLocation=false;
                        mapLocationSwitch.setChecked(showMapLocation);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        date.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            String clean;
            String cleanC;

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
                    Toast.makeText(SettingsActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                    date.setText("");
                }

            }

            //set max lines in descriptions field
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        mapLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showMapLocation=true;
                }else {
                    showMapLocation=false;
                }
            }
        });

        logoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyDb();
                logoutUser();
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

    }



    public void logoutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void deleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete account?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // Toast.makeText(SettingsActivity.this,"Yes clicked",Toast.LENGTH_LONG).show();
                        deleteUser();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //  Toast.makeText(SettingsActivity.this,"No clicked",Toast.LENGTH_LONG).show();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this,"User account deleted.",Toast.LENGTH_LONG).show();
                            deleteUserTags();
                            deleteDatabaseAndStorage();
                            deleteMatches();
                           // logoutUser();

                        } else {
                        AlertDialog.Builder error = new AlertDialog.Builder(context);
                            error.setMessage("Due to safety reasons please re-login and try again").setCancelable(false)
                                    .setTitle("Credentials too old")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }

                                    });
                            AlertDialog alertDialog = error.create();
                            alertDialog.show();
                    }
                    }
                });
    }

    private void deleteMatches() {

        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mUserDatabase.removeValue();
        users.child(userId).child("connections").child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        users.child(ds.getKey()).child("connections").child("matches").child(userId).removeValue();
                        users.child(ds.getKey()).child("connections").child("yes").child(userId).removeValue();

                        deleteUserTags();

                    }catch (Exception e){
                        Toast.makeText(SettingsActivity.this,"Oooops something went wrong",Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteUserTags(){
        DatabaseReference usersTagReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("tags");
        DatabaseReference tagsReference = FirebaseDatabase.getInstance().getReference().child("Tags");

        //DatabaseReference mTagsRemoved = FirebaseDatabase.getInstance().getReference().child("Tags").child(removedTags.getTagName()).child(userId);
        //mTagsRemoved.removeValue();
        usersTagReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    tagsReference.child(ds.getKey()).child(userId).removeValue();
                }

                deleteDatabaseAndStorage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void deleteDatabaseAndStorage(){

        filePath = FirebaseStorage.getInstance().getReference().child("images").child(userId);
        StorageReference storageRef = filePath;
        // Delete the userStorage
        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            item.delete();
                        }
                        logoutUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


    }


    @Override
    public void onBackPressed() {
        updateMyDb();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    private void updateMyDb(){
        DatabaseReference myDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        if(showMapLocation!=onStartShowMapLocation){
            myDatabaseReference.child("showMyLocation").setValue(showMapLocation);
        }

        if(!dateValid==true) {
           // Toast.makeText(SettingsActivity.this, "Wrong date format", Toast.LENGTH_SHORT).show();
            return;
        }


        String dateOfBirth = date.getText().toString();
        if(!dateOfBirth.equals(onStartDateOfBirth)){
            myDatabaseReference.child("dateOfBirth").setValue(dateOfBirth);
        }
    }

}

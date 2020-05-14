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
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userId;
    private Context context=SettingsActivity.this;
    private Button logoutUser,deleteUser;
    private StorageReference filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        userId= mAuth.getCurrentUser().getUid();



        logoutUser=findViewById(R.id.logoutUser);
        deleteUser=findViewById(R.id.deleteUser);

        logoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            Log.d("del", "User account deleted.");
                            Toast.makeText(SettingsActivity.this,"User account deleted.",Toast.LENGTH_LONG).show();
                           // deleteDatabaseAndStorage();
                            deleteMatches();
                            //logoutUser();

                        } else {
                        Log.w("del","Something is wrong!");
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

        users.child(userId).child("connections").child("matches").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    try {
                        users.child(ds.getKey()).child("connections").child("matches").child(userId).removeValue();
                        users.child(ds.getKey()).child("connections").child("yes").child(userId).removeValue();
                        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        mUserDatabase.removeValue();

                        deleteDatabaseAndStorage();

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
}

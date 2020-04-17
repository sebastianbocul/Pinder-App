package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {


    private Uri resultUri;
    private ImageView mProfileImage;
    private static int RESULT_LOAD_IMAGE = 1;
    private EditText mNameField, mPhoneField;

    private Button mBack,mConfirm;


    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId, name , phone, profileImageUrl,userSex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mProfileImage= (ImageView) findViewById(R.id.profileImage);



        mNameField = (EditText) findViewById(R.id.name);

        mPhoneField = (EditText) findViewById(R.id.phone);

        mProfileImage= (ImageView) findViewById(R.id.profileImage);
        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);

        mAuth =  FirebaseAuth.getInstance();
        userId= mAuth.getCurrentUser().getUid();
        System.out.println("userSex: " +userSex);
        System.out.println("userId: "+userId);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();
        mProfileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
        //////////////////
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final Uri imageUri = data.getData();
        resultUri = imageUri;
        mProfileImage.setImageURI(resultUri);
    }



    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if(map.get("phone")!=null){
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
                    if(map.get("sex")!=null){
                        userSex = map.get("sex").toString();

                    }
                    Glide.with(mProfileImage).clear(mProfileImage);
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl = map.get("profileImageUrl").toString();

                        switch (profileImageUrl) {

                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
                                break;

                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                                break;


                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void saveUserInformation() {
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();

        Map userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("phone",phone);
        mUserDatabase.updateChildren(userInfo);
        if(resultUri!=null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mUserDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });




        }else {
            finish();
        }



    }
}
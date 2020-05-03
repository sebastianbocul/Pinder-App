package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tinderapp.Images.ImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    private Uri resultUri;
    private ImageView mProfileImage,mAddImage,mDeleteImage;

    private static int RESULT_LOAD_IMAGE = 1;
    private EditText mNameField, mPhoneField;

    private Button mBack,mConfirm,setDefaultButton;


    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId, name , phone,description, imageStorageKey,userSex,profileImageUrl;
    private int imagePosition=0;
    private EditText descriptionEditText;
    private ArrayList imagesList,mImages;
    DatabaseReference mImageDatabase;
    ViewPager viewPager;
    StorageReference filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProfileImage= (ImageView) findViewById(R.id.profileImage);
        descriptionEditText = (EditText)findViewById(R.id.description);
        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mAddImage = findViewById(R.id.addImage);
        mDeleteImage = findViewById(R.id.delImage);
        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);
        setDefaultButton= findViewById(R.id.setDefaultButton);
        mAuth =  FirebaseAuth.getInstance();
        userId= mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mImageDatabase = mUserDatabase.child("images");
        viewPager=findViewById(R.id.viewPager);

        //get user images
        loadImages();





        getUserInfo();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imagePosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        });
        setDefaultButton.setOnClickListener(new View.OnClickListener() {
            private String buffor,defaultBuffor;

            @Override
            public void onClick(View v) {
                if(viewPager.getAdapter().getCount()!=0){
                mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        buffor = dataSnapshot.child("0").getValue().toString();
                        defaultBuffor = dataSnapshot.child(String.valueOf(imagePosition)).getValue().toString();
                        mImageDatabase.child("0").setValue(defaultBuffor);
                        mImageDatabase.child(String.valueOf(imagePosition)).setValue(buffor);
                        Toast.makeText(ProfileActivity.this,"Profile picture has been changed",Toast.LENGTH_SHORT).show();
                        loadImages();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }
                else  Toast.makeText(ProfileActivity.this,"Add images first!",Toast.LENGTH_SHORT).show();

            }
        });
        mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imagePosition;
                mImageDatabase.child(String.valueOf(imagePosition)).removeValue();
                mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map imagesMap = new HashMap<>();
                        int i = 0;
                        String key;
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            key = String.valueOf(i);
                            imagesMap.put(key,ds.getValue().toString());
                            i++;
                        }
                        mImageDatabase.removeValue();
                        mImageDatabase.updateChildren(imagesMap);
                        getUserInfo();

                        imagePosition=0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                loadImages();
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

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
            //set max lines in descriptions field
            @Override
            public void afterTextChanged(Editable editable) {
                if (null != descriptionEditText.getLayout() && descriptionEditText.getLayout().getLineCount() > 5) {
                    descriptionEditText.getText().delete(descriptionEditText.getText().length() - 1, descriptionEditText.getText().length());
                }
            }
        });
    }


    private void sortImagesDatabase(){
        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map imagesMap = new HashMap<>();
                int i = 0;
                String key;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    key = String.valueOf(i);
                    imagesMap.put(key,ds.getValue().toString());
                    i++;
                }
                mImageDatabase.removeValue();

                mImageDatabase.updateChildren(imagesMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void loadImages() {

        imagesList = new ArrayList<String>();

        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0){
                    viewPager.setBackground(null);
                }
                ArrayList arrayList = new ArrayList();
                for(DataSnapshot ds:dataSnapshot.getChildren()){

                    arrayList.add(ds.getValue());
                  /*  if(ds.getValue().equals("default") && dataSnapshot.getChildrenCount()!=1){
                        arrayList.remove(0);
                    }*/
                }
                mImages=arrayList;ImageAdapter adapter = new ImageAdapter(ProfileActivity.this,mImages);
                viewPager.setAdapter(adapter);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Uri imageUri;
        if(data!=null){
            imageUri = data.getData();
            resultUri = imageUri;

            Toast.makeText(ProfileActivity.this,"Uploading image...",Toast.LENGTH_SHORT).show();
        }

        if(resultUri!=null){
            String size = String.valueOf(mImages.size());
            imageStorageKey  = mImageDatabase.push().getKey();
            filePath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId).child(imageStorageKey);
            Bitmap bitmap = null;
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20,baos);
            byte[] data2 = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data2);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Map newImage = new HashMap();
                            String size = String.valueOf(mImages.size());
                            newImage.put((size), uri.toString());
                            mImageDatabase.updateChildren(newImage);
                            Toast.makeText(ProfileActivity.this,"Upload successful",Toast.LENGTH_SHORT).show();
                            loadImages();
                            getUserInfo();
                           // finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ProfileActivity.this,"Upload unsuccessful",Toast.LENGTH_SHORT).show();
                            //finish();
                            return;
                        }
                    });
                }
            });




        }else {
            return;
            //finish();
        }


    }




    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                    if(map.get("description")!=null){
                        description = map.get("description").toString();
                        descriptionEditText.setText(description);
                    }


                    //dataSnapshot.child("images").exists()
                    if(map.get("images")!=null){
                        String imageDef =  ((ArrayList)map.get("images")).get(0).toString();

                        mUserDatabase.child("profileImageUrl").setValue(imageDef);
                        profileImageUrl = imageDef;
                    }
                    else {
                        mUserDatabase.child("profileImageUrl").setValue("default");
                        viewPager.setBackground(getDrawable(R.mipmap.ic_launcher));
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
        description = descriptionEditText.getText().toString().trim();

        Map userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("phone",phone);
        userInfo.put("description",description);

        mUserDatabase.updateChildren(userInfo);



    }



}
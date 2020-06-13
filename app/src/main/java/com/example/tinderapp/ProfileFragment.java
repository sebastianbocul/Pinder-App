package com.example.tinderapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final int RQS_READ_EXTERNAL_STORAGE = 2;
    private static int RESULT_LOAD_IMAGE = 1;
    DatabaseReference mImageDatabase;
    ViewPager viewPager;
    StorageReference filePath;
    private Uri resultUri;
    private ImageView mProfileImage, mAddImage, mDeleteImage, setDefaultButton;
    private EditText mNameField, mPhoneField;
    private Button mBack, mConfirm;
    private String imageName;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private String userId, name, phone, description, imageStorageKey, userSex, profileImageUrl;
    private int imagePosition = 0;
    private EditText descriptionEditText;
    private ArrayList imagesList, mImages;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void sortImagesDatabase() {
        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map imagesMap = new HashMap<>();
                int i = 0;
                String key;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    key = String.valueOf(i);
                    imagesMap.put(key, ds.getValue().toString());
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mProfileImage =  getView().findViewById(R.id.profileImage);
        descriptionEditText =  getView().findViewById(R.id.description);
        mNameField =  getView().findViewById(R.id.name);
        mAddImage =  getView().findViewById(R.id.addImage);
        mDeleteImage =  getView().findViewById(R.id.delImage);
        setDefaultButton =  getView().findViewById(R.id.setDefaultButton);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mImageDatabase = mUserDatabase.child("images");
        viewPager =  getView().findViewById(R.id.viewPager);
        //get user images
        loadImages();
        getUserInfo();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                imagePosition = position;
                mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        imageName = dataSnapshot.child(String.valueOf(imagePosition)).child("name").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
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
            private String buffor, defaultBuffor;

            @Override
            public void onClick(View v) {
                if (viewPager.getAdapter().getCount() != 0) {
                    mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            DataSnapshot bufor, defaultBufor;
                            int i = 0;
                            bufor = dataSnapshot.child("0");
                            defaultBufor = dataSnapshot.child(String.valueOf(imagePosition));
                            mImageDatabase.child("0").setValue(defaultBufor.getValue());
                            mImageDatabase.child(String.valueOf(imagePosition)).setValue(bufor.getValue());
                            mUserDatabase.child("profileImageUrl").setValue(defaultBufor.child("uri").getValue());
                            Toast.makeText(getContext(), "Profile picture has been changed", Toast.LENGTH_SHORT).show();
                            loadImages();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else
                    Toast.makeText(getContext(), "Add images first!", Toast.LENGTH_SHORT).show();
            }
        });
        mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imagePosition;
                if (viewPager.getAdapter().getCount() != 0) {
                    mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // String imageName = dataSnapshot.child(String.valueOf(imagePosition)).child("name").getValue().toString();
                            filePath = FirebaseStorage.getInstance().getReference().child("images").child(userId);
                            StorageReference storageRef = filePath;
                            // Create a reference to the file to delete
                            StorageReference desertRef = storageRef.child(imageName);
                            // storageRef.getAl
                            // Delete the file
                            Toast.makeText(getContext(), "File from Storage deleted successfully", Toast.LENGTH_SHORT).show();
                            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    mImageDatabase.child(String.valueOf(imagePosition)).removeValue();
                    mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        String imageKey;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map imagesMap = new HashMap<>();
                            int i = 0;
                            String key;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                key = String.valueOf(i);
                                imagesMap.put(key, ds.getValue());
                                i++;
                            }
                            mImageDatabase.removeValue();
                            mImageDatabase.updateChildren(imagesMap);
                            getUserInfo();
                            imagePosition = 0;
                            loadImages();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else
                    Toast.makeText(getContext(), "Add images first!", Toast.LENGTH_SHORT).show();
            }
        });
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            //set max lines in descriptions field
            @Override
            public void afterTextChanged(Editable editable) {
                if (null != descriptionEditText.getLayout() && descriptionEditText.getLayout().getLineCount() > 5) {
                    descriptionEditText.getText().delete(descriptionEditText.getText().length() - 1, descriptionEditText.getText().length());
                }
            }
        });
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;
        try {
            /**
             * if your are targeting only api level >= 5
             * ExifInterface exif = new ExifInterface(src);
             * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            if (Build.VERSION.SDK_INT >= 5) {
                Class<?> exifClass = Class.forName("android.media.ExifInterface");
                Constructor<?> exifConstructor = exifClass.getConstructor(String.class);
                Object exifInstance = exifConstructor.newInstance(src);
                Method getAttributeInt = exifClass.getMethod("getAttributeInt", String.class, int.class);
                Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
                String tagOrientation = (String) tagOrientationField.get(null);
                orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[]{tagOrientation, 1});
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    private void loadImages() {
        imagesList = new ArrayList<String>();
        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    viewPager.setBackground(null);
                    imageName = dataSnapshot.child(String.valueOf(imagePosition)).child("name").getValue().toString();
                }
                ArrayList arrayList = new ArrayList();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    arrayList.add(ds.child("uri").getValue());
                }
                mImages = arrayList;
                ImageAdapter adapter = new ImageAdapter(getActivity(), mImages);
                viewPager.setAdapter(adapter);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = null;
        resultUri = null;
        if (data != null) {
            imageUri = data.getData();
            resultUri = imageUri;
            Toast.makeText(getContext(), "Uploading image...", Toast.LENGTH_SHORT).show();
        }
        if (resultUri != null) {
            String size = String.valueOf(mImages.size());
            imageStorageKey = mImageDatabase.push().getKey();
            filePath = FirebaseStorage.getInstance().getReference().child("images").child(userId).child(imageStorageKey);
            Bitmap bitmap = null;
            Bitmap rotatedBitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplication().getContentResolver(), resultUri);
                ExifInterface exifInterface;
                InputStream in = getActivity().getContentResolver().openInputStream(resultUri);
                try {
                    exifInterface = new ExifInterface(in);
                    // Now you can extract any Exif tag you want
                    // Assuming the image is a JPEG or supported raw format
                    int orientation = exifInterface.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(bitmap, 90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(bitmap, 180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(bitmap, 270);
                            break;
                        case ExifInterface.ORIENTATION_NORMAL:
                            rotatedBitmap = bitmap;
                        default:
                            rotatedBitmap = bitmap;
                    }
                } catch (IOException e) {
                    // Handle any errors
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = rotatedBitmap;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
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
                            Map imgInfo = new HashMap();
                            imgInfo.put("uri", uri.toString());
                            imgInfo.put("name", imageStorageKey);
                            newImage.put((size), imgInfo);
                            mImageDatabase.updateChildren(newImage);
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                            loadImages();
                            getUserInfo();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getContext(), "Upload unsuccessful", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                }
            });
        } else {
            return;
        }
    }

    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if (map.get("sex") != null) {
                        userSex = map.get("sex").toString();
                    }
                    if (map.get("description") != null) {
                        description = map.get("description").toString();
                        descriptionEditText.setText(description);
                    }
                    try {
                        if (map.get("images") != null) {
                            ArrayList imageArray;
                            imageArray = (ArrayList) map.get("images");
                            Map imageMap;
                            imageMap = (Map) imageArray.get(0);
                            String imageDef = imageMap.get("uri").toString();
                            mUserDatabase.child("profileImageUrl").setValue(imageDef);
                            profileImageUrl = imageDef;
                        } else {
                            mUserDatabase.child("profileImageUrl").setValue("default");
                            viewPager.setBackground(getActivity().getDrawable(R.drawable.profile_default));
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Opps something went wrong", Toast.LENGTH_SHORT).show();
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
        description = descriptionEditText.getText().toString().trim();
        Map userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("description", description);
        mUserDatabase.updateChildren(userInfo);
    }


    public void onBackPressed() {
        saveUserInformation();
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
    }

    public void onBack(View view) {
        saveUserInformation();
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
    }

//    private boolean CheckPermission_READ_EXTERNAL_STORAGE() {
//        // return true: have permission
//        // return false: no permission
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    RQS_READ_EXTERNAL_STORAGE);
//            return false;
//        } else {
//            return true;
//        }
//    }
}

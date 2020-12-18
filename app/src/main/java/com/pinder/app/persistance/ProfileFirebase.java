package com.pinder.app.persistance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.MutableLiveData;

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
import com.pinder.app.util.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFirebase implements ProfileDao {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = mAuth.getCurrentUser().getUid();
    private DatabaseReference mImageDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("images");
    private DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
    private MutableLiveData<Resource<ArrayList<String>>> mImages = new MutableLiveData<>();
    private MutableLiveData<Resource<String>> name = new MutableLiveData<>();
    private MutableLiveData<Resource<String>> description = new MutableLiveData<>();
    private MutableLiveData<String> imageName = new MutableLiveData<>();
    private MutableLiveData<Integer> imagePosition = new MutableLiveData<>();
    private MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>(false);

    public ProfileFirebase() {
        Log.d("ProfileFirebase", "constructor: ");
        imagePosition.setValue(0);
        sortImagesDatabase();
        getUserInfo();
    }
    ArrayList arrayNameList = new ArrayList();
    public void loadImages() {
        try {
            arrayNameList.clear();
            ArrayList arrayList = new ArrayList();
            mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        imageName.postValue(dataSnapshot.child(String.valueOf(imagePosition.getValue())).child("name").getValue().toString());
                    }
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        arrayList.add(ds.child("uri").getValue());
                        arrayNameList.add(ds.child("name").getValue());
                    }
                    mImages.setValue(Resource.success(arrayList));
                    return;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
        }
    }

    private void getUserInfo() {
        Log.d("ProfileFirebase", "getUserInfo: ");
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = null;
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        name.setValue(Resource.success(map.get("name").toString()));
                    }
                    if (map.get("description") != null) {
                        description.setValue(Resource.success(map.get("description").toString()));
                    }
                    try {
                        if (map.get("images") != null) {
                            ArrayList imageArray;
                            imageArray = (ArrayList) map.get("images");
                            Map imageMap;
                            imageMap = (Map) imageArray.get(0);
                            String imageDef = imageMap.get("uri").toString();
                            mUserDatabase.child("profileImageUrl").setValue(imageDef);
                        } else {
                            mUserDatabase.child("profileImageUrl").setValue("default");
                        }
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void saveUserInformation(String nameEdt, String descriptionEdt) {
        if (name != null || description != null) {
            name.postValue(Resource.success(nameEdt));
            description.postValue(Resource.success(descriptionEdt));
            Map userInfo = new HashMap<>();
            userInfo.put("name", nameEdt);
            userInfo.put("description", descriptionEdt);
            mUserDatabase.updateChildren(userInfo);
        }
    }

    public void deleteImage(Context context) {
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images").child(userId);
        // Create a reference to the file to delete
        if (imageName.getValue() == null) {
            return;
        }
        StorageReference desertRef = filePath.child(imageName.getValue());
        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show();
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Uh-oh, an error occurred!", Toast.LENGTH_SHORT).show();
                // Uh-oh, an error occurred!
            }
        });
        mImageDatabase.child(String.valueOf(imagePosition.getValue())).removeValue();
        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
                imagePosition.postValue(0);
                loadImages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void setDefault(Context context) {
        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot bufor, defaultBufor;
                bufor = dataSnapshot.child("0");
                defaultBufor = dataSnapshot.child(String.valueOf(imagePosition.getValue()));
                mImageDatabase.child("0").setValue(defaultBufor.getValue());
                mImageDatabase.child(String.valueOf(imagePosition.getValue())).setValue(bufor.getValue());
                mUserDatabase.child("profileImageUrl").setValue(defaultBufor.child("uri").getValue());
                Toast.makeText(context, "Profile picture has been changed", Toast.LENGTH_SHORT).show();
                setImagePosition(0);
                loadImages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void addImage(Context context, Uri resultUri) {
        String imageStorageKey = mImageDatabase.push().getKey();
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images").child(userId).child(imageStorageKey);
        Bitmap bitmap = null;
        Bitmap rotatedBitmap = null;
        showProgressBar.setValue(true);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), resultUri);
            ExifInterface exifInterface;
            InputStream in = context.getContentResolver().openInputStream(resultUri);
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
                showProgressBar.setValue(false);
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap newImage = new HashMap();
                        String size = "0";
                        if(mImages.getValue()!=null){
                            if (mImages.getValue().data != null) {
                                size = String.valueOf(mImages.getValue().data.size());
                            }
                        }
                        Map imgInfo = new HashMap();
                        imgInfo.put("uri", uri.toString());
                        imgInfo.put("name", imageStorageKey);
                        newImage.put((size), imgInfo);
                        mImageDatabase.updateChildren(newImage);
                        Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show();
                        loadImages();
                        getUserInfo();
                        showProgressBar.setValue(false);
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(context, "Upload unsuccessful", Toast.LENGTH_SHORT).show();
                        showProgressBar.setValue(false);
                        return;
                    }
                });
            }
        });
    }

    @Override
    public MutableLiveData<Resource<String>> getName() {
        Log.d("ProfileFirebase", "getName: ");
        return name;
    }

    @Override
    public MutableLiveData<Resource<String>> getDescription() {
        return description;
    }

    @Override
    public MutableLiveData<Resource<ArrayList<String>>> getImages() {
        return mImages;
    }

    @Override
    public void setImagePosition(int position) {
        try {
            imagePosition.setValue(position);
            imageName.setValue(arrayNameList.get(position).toString());
        } catch (Exception e) {
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void sortImagesDatabase() {
        mImageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map imagesMap = new HashMap<>();
                boolean update = false;
                int i = 0;
                String key;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!ds.getKey().equals(String.valueOf(i))) {
                        Map nameanduri = new HashMap<>();
                        nameanduri.put("name", ds.child("name").getValue().toString());
                        nameanduri.put("uri", ds.child("uri").getValue().toString());
                        key = String.valueOf(i);
                        imagesMap.put(key, nameanduri);
                        update = true;
                    }
                    i++;
                }
                if (update == true) {
                    mImageDatabase.removeValue();
                    mImageDatabase.updateChildren(imagesMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public MutableLiveData<Boolean> getShowProgressBar() {
        return showProgressBar;
    }
}

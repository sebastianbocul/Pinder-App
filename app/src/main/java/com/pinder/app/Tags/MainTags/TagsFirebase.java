package com.pinder.app.Tags.MainTags;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TagsFirebase implements TagsFirebaseDao {

    public static TagsFirebase instance=null;
    private ArrayList<TagsObject> tagsList = new ArrayList<>();
    private MutableLiveData<List<TagsObject>> result = new MutableLiveData<List<TagsObject>>();

    public static TagsFirebase getInstance(){
        if(instance==null){
            instance=new TagsFirebase();
            instance.loadDataFromDb();
        }
        return instance;
    }

    private void loadDataFromDb(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId;
        currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("tags");

        ds.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String tagName = snapshot.getKey();
                        String gender = snapshot.child("gender").getValue().toString();
                        String mAgeMax = snapshot.child("maxAge").getValue().toString();
                        String mAgeMin = snapshot.child("minAge").getValue().toString();
                        String mDistance = snapshot.child("maxDistance").getValue().toString();
                        TagsObject obj = new TagsObject(tagName, gender, mAgeMin, mAgeMax, mDistance);
                        tagsList.add(obj);
                        result.postValue(tagsList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public MutableLiveData<List<TagsObject>> getAllTags()
    {
        return result;
    }

    @Override
    public TagsObject deleteTag(int position) {
        return null;
    }

    @Override
    public TagsObject addTag() {
        return null;
    }
}

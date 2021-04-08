package com.pinder.app.persistance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pinder.app.models.TagsObject;
import com.pinder.app.util.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagsFirebase implements TagsFirebaseDao {
    String TAG = "TagsFirebase";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final ArrayList<TagsObject> tagsList = new ArrayList<>();
    private final MutableLiveData<Resource<List<TagsObject>>> result = new MutableLiveData<>();

    public TagsFirebase() {
        loadDataFromDb();
    }

    private void loadDataFromDb() {
        String currentUserId = mAuth.getCurrentUser().getUid();
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
                result.postValue(Resource.success(tagsList));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < tagsList.size(); i++) {
                    if (tagsList.get(i).getTagName().equals(snapshot.getKey())) {
                        String gender = snapshot.child("gender").getValue().toString();
                        tagsList.get(i).setGender(gender);
                        String mAgeMax = snapshot.child("maxAge").getValue().toString();
                        tagsList.get(i).setmAgeMax(mAgeMax);
                        String mAgeMin = snapshot.child("minAge").getValue().toString();
                        tagsList.get(i).setmAgeMin(mAgeMin);
                        String mDistance = snapshot.child("maxDistance").getValue().toString();
                        tagsList.get(i).setmDistance(mDistance);
                        result.postValue(Resource.success(tagsList));
                    }
                }
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
    public MutableLiveData<Resource<List<TagsObject>>> getAllTags() {
        return result;
    }

    @Override
    public void deleteTag(TagsObject tag) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("tags").child(tag.getTagName());
        ds.removeValue();
        DatabaseReference mTagsRemoved = FirebaseDatabase.getInstance().getReference().child("Tags").child(tag.getTagName()).child(currentUserId);
        mTagsRemoved.removeValue();
        tagsList.remove(tag);
        result.postValue(Resource.success(tagsList));
    }

    @Override
    public void addTag(TagsObject tag) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference tagDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("tags").child(tag.getTagName());
        Map tagInfo = new HashMap<>();
        tagInfo.put("minAge", tag.getmAgeMin());
        tagInfo.put("maxAge", tag.getmAgeMax());
        tagInfo.put("maxDistance", tag.getmDistance());
        tagInfo.put("gender", tag.getGender());
        tagDb.updateChildren(tagInfo);
        DatabaseReference tags = FirebaseDatabase.getInstance().getReference().child("Tags");
        tags.child(tag.getTagName()).child(currentUserId).setValue(true);
    }
}

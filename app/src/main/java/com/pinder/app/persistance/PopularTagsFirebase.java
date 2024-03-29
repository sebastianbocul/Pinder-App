package com.pinder.app.persistance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class PopularTagsFirebase implements PopularTagsFirebaseDao {
    private ArrayList<PopularTagsObject> popularTagsList = new ArrayList<>();
    private MutableLiveData<Resource<List<PopularTagsObject>>> result = new MutableLiveData<>();

    public PopularTagsFirebase() {
        loadDataFromDb();
    }

    private void loadDataFromDb() {
        DatabaseReference tagsDatabase = FirebaseDatabase.getInstance().getReference().child("Tags");
        ///downloads data once
//        tagsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<PopularTagsObject> popularTagsList = new ArrayList<>();
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        String tag_name = ds.getKey();
//                        int tag_popularity = (int) ds.getChildrenCount();
//                        PopularTagsObject popular_tag = new PopularTagsObject(tag_name, tag_popularity);
//                        popularTagsList.add(popular_tag);
//                    }
//                }
//                result.postValue(popularTagsList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
        //real time update
        tagsDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String tag_name = snapshot.getKey();
                int tag_popularity = (int) snapshot.getChildrenCount();
                PopularTagsObject popular_tag = new PopularTagsObject(tag_name, tag_popularity);
                popularTagsList.add(popular_tag);
                result.postValue(Resource.success(popularTagsList));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String tag_name = snapshot.getKey();
                int tag_popularity = (int) snapshot.getChildrenCount();
                PopularTagsObject popular_tag = new PopularTagsObject(tag_name, tag_popularity);
                for (int i = 0; i < popularTagsList.size(); i++) {
                    if (popularTagsList.get(i).getTagName().equals(popular_tag.getTagName())) {
                        popularTagsList.remove(i);
                    }
                }
                popularTagsList.add(popular_tag);
                result.postValue(Resource.success(popularTagsList));
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
    public MutableLiveData<Resource<List<PopularTagsObject>>> getAllPopularTags() {
        return result;
    }
}

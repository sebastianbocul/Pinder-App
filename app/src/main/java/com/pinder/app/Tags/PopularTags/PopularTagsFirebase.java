package com.pinder.app.Tags.PopularTags;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PopularTagsFirebase implements PopularTagsFirebaseDAO {
    private static PopularTagsFirebase instance=null;

    public static synchronized  PopularTagsFirebase getInstance() {
        if (instance == null) {
            Log.d("PopularTagsFragment", "Firebase getInstance: ");
            instance = new PopularTagsFirebase();
        }
        return instance;
    }

    @Override
    public MutableLiveData<List<PopularTagsObject>> getAllPopularTags() {
        MutableLiveData<List<PopularTagsObject>> result = new MutableLiveData<List<PopularTagsObject>>();
        Log.d("PopularTagsFragment", "getAllPopularTags: ");
        DatabaseReference tagsDatabase = FirebaseDatabase.getInstance().getReference().child("Tags");
        tagsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<PopularTagsObject> popularTagsList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String tag_name = ds.getKey();
                        int tag_popularity = (int) ds.getChildrenCount();
                        PopularTagsObject popular_tag = new PopularTagsObject(tag_name, tag_popularity);
                        popularTagsList.add(popular_tag);
                    }
                }
                Log.d("PopularTagsFragment", "onDataChange: ");
                popularTagsList = sortCollection(popularTagsList);
                result.postValue(popularTagsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return result;
    }

    private ArrayList<PopularTagsObject> sortCollection(ArrayList<PopularTagsObject> popularTagsList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(popularTagsList, Comparator.comparing(PopularTagsObject::getTagPopularity).reversed());
        } else {
            Collections.sort(popularTagsList, new Comparator<PopularTagsObject>() {
                public int compare(PopularTagsObject o1, PopularTagsObject o2) {
                    if (o1.getTagPopularity() == o2.getTagPopularity())
                        return 0;
                    return o1.getTagPopularity() < o2.getTagPopularity() ? -1 : 1;
                }
            });
            Collections.reverse(popularTagsList);
        }
        return popularTagsList;
    }

}

package com.pinder.app.Tags;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.R;
import com.pinder.app.Tags.MainTags.TagsFragment;
import com.pinder.app.Tags.MainTags.TagsObject;
import com.pinder.app.Tags.PopularTags.PopularTagsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagsManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagsManagerFragment extends Fragment implements TagsFragment.OnFragmentInteractionListener, PopularTagsFragment.OnFragmentInteractionListener, MyInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private ArrayList<TagsObject> myTagsList;
    private ArrayList<TagsObject> removedTags;

    public TagsManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagsManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagsManagerFragment newInstance(String param1, String param2) {
        TagsManagerFragment fragment = new TagsManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("naviagionLog", "TagsManagerFragment - onCreate() ");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tags_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        myTagsList = new ArrayList<>();
        removedTags = new ArrayList<>();
        Log.d("naviagionLog", "TagsManagerFragment - onViewCreated() ");
        TabLayout tabLayout = getActivity().findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("My tags"));
        tabLayout.addTab(tabLayout.newTab().setText("Popular tags"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = getActivity().findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void updateDb() {
        Log.d("naviagionLog", "TagsManagerFragment - updateDb()");
        Log.d("naviagionLog", "TagsManagerFragment - updateDb()     myTagsList:" + myTagsList + "removedTags:" + removedTags);
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        ArrayList<String> myTagsListStr = new ArrayList<>();
        for (TagsObject tgo : myTagsList) {
            myTagsListStr.add(tgo.getTagName());
            DatabaseReference mTagsDatabase = FirebaseDatabase.getInstance().getReference().child("Tags").child(tgo.getTagName()).child(userId);
            mTagsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        return;
                    } else {
                        mTagsDatabase.setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        for (TagsObject removedTags : removedTags) {
            if (!myTagsListStr.contains(removedTags.getTagName())) {
                DatabaseReference mTagsRemoved = FirebaseDatabase.getInstance().getReference().child("Tags").child(removedTags.getTagName()).child(userId);
                mTagsRemoved.removeValue();
            }
        }
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        Map userInfo = new HashMap<>();
        Map tagsMap = new HashMap<>();
        for (TagsObject tgo : myTagsList) {
            Map tagInfo = new HashMap<>();
            tagInfo.put("minAge", tgo.getmAgeMin());
            tagInfo.put("maxAge", tgo.getmAgeMax());
            tagInfo.put("maxDistance", tgo.getmDistance());
            tagInfo.put("gender", tgo.getGender());
            tagsMap.put(tgo.getTagName(), tagInfo);
        }
        userInfo.put("tags", tagsMap);
        mUserDatabase.updateChildren(userInfo);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void doSomethingWithData(ArrayList<TagsObject> myTagsList2, ArrayList<TagsObject> removedTags2) {
        myTagsList = myTagsList2;
        removedTags = removedTags2;
        updateDb();
        Log.d("naviagionLog", "TagsManagerFragment - doSomethingWithData()     myTagsList:" + myTagsList + "removedTags:" + removedTags);
    }
}

package com.example.tinderapp.Tags;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.example.tinderapp.FillInfoActivity;
import com.example.tinderapp.LoginActivity;
import com.example.tinderapp.MainActivity;
import com.example.tinderapp.Matches.MatchesActivity;
import com.example.tinderapp.Matches.MatchesAdapter;
import com.example.tinderapp.Matches.MatchesObject;
import com.example.tinderapp.Matches.MatchesTagsAdapter;
import com.example.tinderapp.R;
import com.example.tinderapp.SettingsActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TagsManagerActivity extends AppCompatActivity implements TagsManagerFragment.OnFragmentInteractionListener, PopularTagsFragment.OnFragmentInteractionListener,MyInterface{


    private FirebaseAuth mAuth;
    private String currentUserId;
    private ArrayList<TagsManagerObject> myTagsList;
    private ArrayList<TagsManagerObject> removedTags;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_manager);
        mAuth = FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();

        myTagsList = new ArrayList<TagsManagerObject>();
        removedTags = new ArrayList<TagsManagerObject>();

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("My tags"));
        tabLayout.addTab(tabLayout.newTab().setText("Popular tags"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
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


    @Override
    public void onBackPressed() {


        Log.d("TMAlog", "onBackPressed myTags: " + myTagsList);
        Log.d("TMAlog", "onBackPressed removedTags: " + removedTags);
        if(myTagsList.size()==0){
            Toast.makeText(this,"Add at least one tag!",Toast.LENGTH_SHORT).show();
            return;
        }


        updateDb();
        Intent startMain = new Intent(this.getApplicationContext(), MainActivity.class);
        startActivity(startMain);
    }

    public void onBack(View view) {


        Log.d("TMAlog", "onBackPressed myTags: " + myTagsList);
        Log.d("TMAlog", "onBackPressed removedTags: " + removedTags);
        if(myTagsList.size()==0){
            Toast.makeText(this,"Add at least one tag!",Toast.LENGTH_SHORT).show();
            return;
        }


        updateDb();
        Intent startMain = new Intent(this.getApplicationContext(), MainActivity.class);
        startActivity(startMain);
    }


  private void updateDb() {
        String userId = mAuth.getCurrentUser().getUid();

        ArrayList<String> myTagsListStr=new ArrayList<>();

        for (TagsManagerObject tgo:myTagsList){
            myTagsListStr.add(tgo.getTagName());
            DatabaseReference mTagsDatabase = FirebaseDatabase.getInstance().getReference().child("Tags").child(tgo.getTagName()).child(userId);
            mTagsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        return;
                    }
                    else {
                        mTagsDatabase.setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

        for (TagsManagerObject removedTags:removedTags){
            if(!myTagsListStr.contains(removedTags.getTagName())){
                DatabaseReference mTagsRemoved = FirebaseDatabase.getInstance().getReference().child("Tags").child(removedTags.getTagName()).child(userId);
                mTagsRemoved.removeValue();
            }
        }



        //userDb
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        Map userInfo = new HashMap<>();
        Map tagsMap = new HashMap<>();
        for (TagsManagerObject tgo:myTagsList){
            Map tagInfo = new HashMap<>();
            tagInfo.put("minAge",tgo.getmAgeMin());
            tagInfo.put("maxAge",tgo.getmAgeMax());
            tagInfo.put("maxDistance",tgo.getmDistance());
            tagInfo.put("gender",tgo.getGender());
            tagsMap.put(tgo.getTagName(),tagInfo);

        }
        userInfo.put("tags",tagsMap);

        mUserDatabase.updateChildren(userInfo);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void doSomethingWithData(ArrayList<TagsManagerObject> myTagsList2,ArrayList<TagsManagerObject> removedTags2) {
        myTagsList = myTagsList2;
        removedTags=removedTags2;
    }
}

package com.example.tinderapp.Tags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.Map;

public class TagsManagerActivity extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private String currentUserId;
    private Context context= TagsManagerActivity.this;
    private StorageReference filePath;
    // get seekbar from view
    private TextView ageRangeTextView,maxDistanceTextView;
    private CrystalRangeSeekbar ageRangeSeeker;
    private CrystalSeekbar maxDistanceSeeker;
    private TagsManagerAdapter adapter;
    private Button addTagButton;
    private EditText tagsEditText;
    private RadioGroup mRadioGroup;
    private String ageMin,ageMax,distanceMax;
    private ArrayList<TagsManagerObject> myTagsList;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_manager);
        mAuth = FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();
        ageRangeSeeker = findViewById(R.id.ageRangeSeeker);
        ageRangeTextView = findViewById(R.id.ageRangeTextView);
        maxDistanceSeeker = findViewById(R.id.distanceSeeker);
        maxDistanceTextView = findViewById(R.id.maxDistanceTextView);
        fillTagsAdapter();
        addTagButton=findViewById(R.id.addButton);
        tagsEditText = findViewById(R.id.tagsEditText);
        mRadioGroup=findViewById(R.id.radioGroup);
        // set listener

        ///RecyclerView
        myTagsList = new ArrayList<TagsManagerObject>();
        recyclerView = findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(TagsManagerActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new TagsManagerAdapter(myTagsList);
        recyclerView.setAdapter(adapter);



        ageRangeSeeker.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ageRangeTextView.setText("Age range: " + minValue.toString() + " - " +maxValue.toString());
                ageMin=minValue.toString();
                ageMax=maxValue.toString();
            }
        });

        // set listener
        maxDistanceSeeker.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue) {
                maxDistanceTextView.setText("Max distance: " + String.valueOf(minValue) + " km");
                distanceMax=minValue.toString();
            }
        });

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTagButtonFunction();
            }
        });


        adapter.setOnItemClickListener(new TagsManagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                System.out.println("Activity pos: " + position);
                removeItem(position);
            }
        });


    }

    private void addTagButtonFunction() {
        if(tagsEditText.getText().toString().length()==0){
            Toast.makeText(TagsManagerActivity.this,"Fill tag name",Toast.LENGTH_SHORT).show();
            return;
        }


        if(mRadioGroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(TagsManagerActivity.this, "Choose gender to find", Toast.LENGTH_SHORT).show();
            return;
        }

        for(TagsManagerObject tmo:myTagsList){
            if(tmo.getTagName().equals(tagsEditText.getText().toString().toLowerCase())){
                Toast.makeText(TagsManagerActivity.this, "Duplicate tag", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String tagName = tagsEditText.getText().toString().toLowerCase();
        String gender= radioButton.getText().toString();
        String mAgeMax = ageMax;
        String mAgeMin=ageMin;
        String mDistance=distanceMax;
        TagsManagerObject obj = new TagsManagerObject(tagName,gender,mAgeMin,mAgeMax,mDistance);
        myTagsList.add(obj);
        System.out.println("myTagsList:   " + myTagsList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    private void fillTagsAdapter() {

        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("tags");
        ArrayList<String> myTags = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(TagsManagerActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        System.out.println("DDDDDDDDD" + ds);
                        String tagName = ds.getKey();
                        String gender = ds.child("gender").getValue().toString();
                        String mAgeMax = ds.child("maxAge").getValue().toString();
                        String mAgeMin = ds.child("minAge").getValue().toString();
                        String mDistance = ds.child("maxDistance").getValue().toString();
                        TagsManagerObject obj = new TagsManagerObject(tagName, gender, mAgeMin, mAgeMax, mDistance);
                        myTagsList.add(obj);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);

                    }


                } else {
                    TagsManagerObject obj = new TagsManagerObject("default", "default", "default", "default", "default");
                    myTagsList.add(obj);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeItem(int position) {
        myTagsList.remove(position);
        adapter.notifyItemRemoved(position);
    }
    @Override
    public void onBackPressed() {
        if(myTagsList.size()==0){
            Toast.makeText(TagsManagerActivity.this,"Add at least one tag!",Toast.LENGTH_SHORT).show();
            return;
        }


        updateDb();
        Intent startMain = new Intent(this, MainActivity.class);
        startActivity(startMain);
    }


    private void updateDb() {

        String userId = mAuth.getCurrentUser().getUid();
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

}

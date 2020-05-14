package com.example.tinderapp.Matches;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tinderapp.LocationActivity;
import com.example.tinderapp.MainActivity;
import com.example.tinderapp.R;
import com.example.tinderapp.Tags.TagsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String currentUserID;
    private Button locationButton;
    private String lastMessage, createdByUser;
    private int matchesCount;
    private String sortBy;
    private TextView sortByTextView;
    private Button allMatches;
    RecyclerView recyclerView;
    MatchesTagsAdapter adapter;
    ArrayList<String> myTags = new ArrayList<>();
    private ArrayList<MatchesObject> resultMatches = new ArrayList<MatchesObject>();
    private ArrayList<MatchesObject> oryginalMatches = new ArrayList<MatchesObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        locationButton = (Button) findViewById(R.id.locationButton);
        allMatches=findViewById(R.id.allMatches);
        sortByTextView = findViewById(R.id.sortByText);

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        myRecyclerView.setNestedScrollingEnabled(false);
        myRecyclerView.setHasFixedSize(true);

        mMatchesLayoutManager= new LinearLayoutManager(MatchesActivity.this);
        myRecyclerView.setLayoutManager(mMatchesLayoutManager);
        recyclerView = findViewById(R.id.tagsRecyclerViewMatches);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(),MatchesActivity.this);
        myRecyclerView.setAdapter(mMatchesAdapter);




        getUserMatchId();
        loadTagsRecyclerView();

        allMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               allMatchesFunction();
            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLocationActivity();
            }
        });

    }


    private void allMatchesFunction() {
        fillRecyclerViewByTags("AllButtonClicked");
    }

    private void goToLocationActivity() {
        Intent intent = new Intent(MatchesActivity.this, LocationActivity.class);
        startActivity(intent);
    }

    private void getUserMatchId() {

        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");


        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){


                    for(DataSnapshot match: dataSnapshot.getChildren()){
                         matchesCount=(int)dataSnapshot.getChildrenCount();
                         getLastMessage(match);
                        //fetchMatchInformation(match.getKey(), match.child("ChatId").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String sortId;
    private void getLastMessage(DataSnapshot match) {
        sortId="00";
        String chatId = match.child("ChatId").getValue().toString();
        DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);
        final DataSnapshot mMatch= match;


        chatDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean createdByMe=true;
                String message = "No messages...";
                sortId=chatId;
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        message = ds.child("text").getValue().toString();
                        createdByUser = ds.child("createdByUser").getValue().toString();
                        sortId=ds.getKey();
                    }
                    if (createdByUser.equals(currentUserID.toString())){
                        createdByMe = true;
                    }else createdByMe=false;
                }
                fetchMatchInformation(mMatch.getKey(), mMatch.child("ChatId").getValue().toString(),createdByMe,message,sortId);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void fetchMatchInformation(String key, String chatId, final boolean createdByMe, final String message, String mSortId) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);


        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    matchesCount--;
                    if(resultMatches.size()==matchesCount){
                        Collections.sort(resultMatches, Comparator.comparing(MatchesObject ::getSortId).reversed());
                        mMatchesAdapter.notifyDataSetChanged();
                    }
                }
                if(dataSnapshot.exists()){
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl= "";
                    String lastMessageM = message;
                    StringBuilder stringBuilder=new StringBuilder();
                    String s = StringUtils.left(lastMessageM, 15);
                    stringBuilder.append(s);

                    ArrayList<String> mutualTags = new ArrayList<>();
                    Map tagMap = new HashMap();

                    for(DataSnapshot ds : dataSnapshot.child("connections").child("matches").child(currentUserID).child("mutualTags").getChildren()){
                        mutualTags.add(ds.getKey());
                    }
                   // tagMap = dataSnapshot.child("tags").getChildren();



                    if(lastMessageM.length()>=15) stringBuilder.append("...");
                    String mLastMessage = stringBuilder.toString();

                    if(dataSnapshot.child("name").getValue()!=null){
                        name=dataSnapshot.child("name").getValue().toString();
                    }

                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        profileImageUrl=dataSnapshot.child("profileImageUrl").getValue().toString();
                    }


                    
                    MatchesObject obj = new MatchesObject(userId,name,profileImageUrl,mLastMessage,createdByMe,mSortId,mutualTags);
                    oryginalMatches.add(obj);
                    resultMatches.add(obj);
                    if(resultMatches.size()==matchesCount){
                        Collections.sort(resultMatches, Comparator.comparing(MatchesObject ::getSortId).reversed());
                        mMatchesAdapter.notifyDataSetChanged();
                    }

                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(this, MainActivity.class);
        startActivity(startMain);
    }

    private List<MatchesObject> getDataSetMatches() {
        return resultMatches;
    }

    int iterator=0;


    private void loadTagsRecyclerView() {
//  super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // data to populate the RecyclerView with
        DatabaseReference matchesReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");


        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(MatchesActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);

        matchesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                     iterator = (int)dataSnapshot.getChildrenCount();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        //myTags.add("#"+ ds.getKey());
                        iterator--;
                        singeMatchTags(matchesReference.child(ds.getKey()));

                    }
                }
                else {
                    myTags.add("No matches");
                    adapter = new MatchesTagsAdapter(MatchesActivity.this, myTags);
                    recyclerView.setAdapter(adapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void singeMatchTags(DatabaseReference  databaseReference){
        databaseReference.child("mutualTags").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren())
                myTags.add(ds.getKey());
                Set<String> set = new HashSet<>(myTags);
                myTags.clear();
                myTags.addAll(set);
                if(iterator==0){
                    adapter = new MatchesTagsAdapter(MatchesActivity.this, myTags);
                    recyclerView.setAdapter(adapter);

                    adapter.setClickListener(new MatchesTagsAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            sortBy =  myTags.get(position);
                            sortByTextView.setText("#" + sortBy);
                            fillRecyclerViewByTags(sortBy);
                        }




                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void fillRecyclerViewByTags(String tag){
        ArrayList mutualTags = new ArrayList();
        ArrayList<MatchesObject> bufforMatches = new ArrayList<MatchesObject>();

        if(tag.equals("AllButtonClicked")){
            sortByTextView.setText("#" + "all");
            mMatchesAdapter = new MatchesAdapter(oryginalMatches,MatchesActivity.this);
            myRecyclerView.setAdapter(mMatchesAdapter);
            return;
        }



        for(MatchesObject mo : oryginalMatches){
            mutualTags = mo.getMutualTags();
       //     boolean contains  = mutualTags.contains(tag);
            if(mutualTags.contains(tag)) {
                bufforMatches.add(mo);
            }
            }

        if(bufforMatches.size()!=0){
            resultMatches.clear();
            resultMatches=bufforMatches;
            mMatchesAdapter = new MatchesAdapter(resultMatches,MatchesActivity.this);
            myRecyclerView.setAdapter(mMatchesAdapter);
        }


        }



}

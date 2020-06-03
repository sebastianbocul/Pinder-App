package com.example.tinderapp.Matches;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tinderapp.LocationActivity;
import com.example.tinderapp.MainActivity;
import com.example.tinderapp.R;
import com.example.tinderapp.Tags.TagsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ImageView locationButton;
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
    private ArrayList<String> usersID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        locationButton =  findViewById(R.id.locationButton);
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


        DatabaseReference matchDbAd = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");

        matchDbAd.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    matchesCount=(int)dataSnapshot.getChildrenCount();
                    getLastMessage(dataSnapshot);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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


        DataSnapshot mMatch= match;
        chatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                boolean createdByMe=true;
                String message = "No messages...";
                sortId=chatId;
                if(dataSnapshot.exists()){
                    DataSnapshot ds= dataSnapshot;
                        message = ds.child("text").getValue().toString();
                        createdByUser = ds.child("createdByUser").getValue().toString();
                        sortId=ds.getKey();
                    if (createdByUser.equals(currentUserID.toString())){
                        createdByMe = true;
                    }else createdByMe=false;
                    fetchMatchInformation(mMatch.getKey(), chatId,createdByMe,message,sortId);

                }else {
                    fetchMatchInformation(mMatch.getKey(), chatId,false,"No messages...",chatId);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    fetchMatchInformation(mMatch.getKey(), chatId,false,"No messages...",chatId);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void fetchMatchInformation(String key, String chatId, final boolean createdByMe, final String message, String mSortId) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(!dataSnapshot.exists()){
                    if(resultMatches.size()==matchesCount){
                        resultMatches = sortCollection(resultMatches);
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


                    if(lastMessageM.length()>=15) stringBuilder.append("...");
                    String mLastMessage = stringBuilder.toString();

                    if(dataSnapshot.child("name").getValue()!=null){
                        name=dataSnapshot.child("name").getValue().toString();
                    }

                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        profileImageUrl=dataSnapshot.child("profileImageUrl").getValue().toString();
                    }



                    MatchesObject obj = new MatchesObject(userId,name,profileImageUrl,mLastMessage,createdByMe,mSortId,mutualTags);



                    if(!usersID.contains(obj.getUserId())){
                        usersID.add(obj.getUserId());
                        resultMatches.add(obj);
                        oryginalMatches.add(obj);
                    }else {
                        resultMatches = sortCollection(resultMatches);
                        oryginalMatches = sortCollection(oryginalMatches);

                        for(MatchesObject m : resultMatches){
                        }
                        for(int i =0;i<resultMatches.size();i++){
                          if(usersID.get(i).equals(obj.getUserId())){
                              oryginalMatches.get(i).setLastMessage(obj.getLastMessage());
                              oryginalMatches.get(i).setSortId(obj.getSortId());
                              oryginalMatches.get(i).setCreatedByMe(obj.isCreatedByMe());
                              resultMatches.get(i).setLastMessage(obj.getLastMessage());
                              resultMatches.get(i).setSortId(obj.getSortId());
                              resultMatches.get(i).setCreatedByMe(obj.isCreatedByMe());
                          }
                        }
                    }

                   // Collections.sort(usersID, Comparator.comparing(String ::getSortId).reversed());
                    resultMatches = sortCollection(resultMatches);
                    mMatchesAdapter.notifyDataSetChanged();
                    if(resultMatches.size()==matchesCount){
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
    public void onBack(View view) {
        Intent startMain = new Intent(this, MainActivity.class);
        startActivity(startMain);
    }

    private List<MatchesObject> getDataSetMatches() {
        return resultMatches;
    }

    int iterator=0;


    private void loadTagsRecyclerView() {

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

            oryginalMatches = sortCollection(oryginalMatches);

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
            resultMatches = sortCollection(resultMatches);
            mMatchesAdapter = new MatchesAdapter(resultMatches,MatchesActivity.this);
            myRecyclerView.setAdapter(mMatchesAdapter);
        }


        }

    private ArrayList<MatchesObject> sortCollection(ArrayList<MatchesObject> matchesList) {

//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
//            Collections.sort(matchesList, Comparator.comparing(MatchesObject::getSortId).reversed());
//
//        }
//        else {
//            Collections.sort(matchesList, new Comparator<MatchesObject>(){
//                @Override
//                public int compare(MatchesObject o1, MatchesObject o2) {
//                    return o1.getSortId().compareTo(o2.getSortId());
//                }
//            });
//            Collections.reverse(matchesList);
//        }

        Collections.sort(matchesList, new Comparator<MatchesObject>(){
            @Override
            public int compare(MatchesObject o1, MatchesObject o2) {
                return o1.getSortId().compareTo(o2.getSortId());
            }
        });
        Collections.reverse(matchesList);

        return matchesList;
    }


}

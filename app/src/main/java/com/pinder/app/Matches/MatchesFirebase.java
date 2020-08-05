package com.pinder.app.Matches;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class MatchesFirebase implements MatchesDao {
    public static MatchesFirebase instance = null;
    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    MutableLiveData<ArrayList<String>> myTagsLiveData = new MutableLiveData<ArrayList<String>>();
    ArrayList<String> myTags = new ArrayList<>();
    MutableLiveData<ArrayList<MatchesObject>> oryginalMatchesLiveData = new MutableLiveData<ArrayList<MatchesObject>>();

    public static MatchesFirebase getInstance() {
        if (instance == null) {
            instance = new MatchesFirebase();
            instance.loadMatches();
            instance.loadTags();
        }
        return instance;
    }

    public void loadMatches() {
        getUserMatchId();
    }

    public void loadTags() {
        myTags.clear();
        DatabaseReference matchesReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        matchesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        singeMatchTags(matchesReference.child(ds.getKey()));
                    }
                } else {
                    myTags.add("No matches");
                    myTagsLiveData.postValue(myTags);
                    myTags.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void singeMatchTags(DatabaseReference databaseReference) {
        databaseReference.child("mutualTags").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                    myTags.add(ds.getKey());
                Set<String> set = new HashSet<>(myTags);
                myTags.clear();
                myTags.addAll(set);
                myTagsLiveData.postValue(myTags);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getUserMatchId() {
        DatabaseReference matchDbAd = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        matchDbAd.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    getLastMessage(dataSnapshot);
                    loadTags();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<MatchesObject> buffor = new ArrayList<>();
                try {
                    if (usersID.contains(dataSnapshot.getKey())) {
                        usersID.remove(dataSnapshot.getKey());
                    }
                    for (MatchesObject mo : oryginalMatches) {
                        if (mo.getUserId().equals(dataSnapshot.getKey())) {
                            oryginalMatches.remove(mo);
                            oryginalMatchesLiveData.postValue(oryginalMatches);
                        }
                    }

                } catch (Exception e) {
                }
                loadTags();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    String sortId = "00";

    private void getLastMessage(DataSnapshot match) {
        String chatId = match.child("ChatId").getValue().toString();
        DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);
        DataSnapshot mMatch = match;
        chatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                boolean createdByMe = true;
                String message = "No messages...";
                sortId = chatId;
                if (dataSnapshot.exists()) {
                    DataSnapshot ds = dataSnapshot;
                    message = ds.child("text").getValue().toString();
                    String createdByUser = ds.child("createdByUser").getValue().toString();
                    sortId = ds.getKey();
                    createdByMe = createdByUser.equals(currentUserID);
                    fetchMatchInformation(mMatch.getKey(), chatId, createdByMe, message, sortId);
                } else {
                    fetchMatchInformation(mMatch.getKey(), chatId, false, "No messages...", chatId);
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
                if (!dataSnapshot.exists()) {
                    fetchMatchInformation(mMatch.getKey(), chatId, false, "No messages...", chatId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private ArrayList<String> usersID = new ArrayList<>();
    private ArrayList<MatchesObject> oryginalMatches = new ArrayList<>();

    private void fetchMatchInformation(String key, String chatId, final boolean createdByMe, final String message, String mSortId) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    String lastMessageM = message;
                    StringBuilder stringBuilder = new StringBuilder();
                    String s = StringUtils.left(lastMessageM, 20);
                    stringBuilder.append(s);
                    ArrayList<String> mutualTags = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.child("connections").child("matches").child(currentUserID).child("mutualTags").getChildren()) {
                        mutualTags.add(ds.getKey());
                    }
                    if (lastMessageM.length() >= 20) stringBuilder.append("...");
                    String mLastMessage = stringBuilder.toString();
                    if (dataSnapshot.child("name").getValue() != null) {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue() != null) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    MatchesObject obj = new MatchesObject(userId, name, profileImageUrl, mLastMessage, createdByMe, mSortId, mutualTags);
                    if (!usersID.contains(obj.getUserId())) {
                        usersID.add(obj.getUserId());
                        oryginalMatches.add(obj);
                    } else {
                        oryginalMatches = sortCollection(oryginalMatches);
                        for (int i = 0; i < oryginalMatches.size(); i++) {
                            if (oryginalMatches.get(i).getUserId().equals(obj.getUserId())) {
                                oryginalMatches.get(i).setLastMessage(obj.getLastMessage());
                                oryginalMatches.get(i).setSortId(obj.getSortId());
                                oryginalMatches.get(i).setCreatedByMe(obj.isCreatedByMe());
                            }
                        }
                    }

                    oryginalMatches = sortCollection(oryginalMatches);
                    oryginalMatchesLiveData.postValue(oryginalMatches);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private ArrayList<MatchesObject> sortCollection(ArrayList<MatchesObject> matchesList) {
        Collections.sort(matchesList, new Comparator<MatchesObject>() {
            @Override
            public int compare(MatchesObject o1, MatchesObject o2) {
                return o1.getSortId().compareTo(o2.getSortId());
            }
        });
        Collections.reverse(matchesList);
        return matchesList;
    }

    @Override
    public LiveData<ArrayList<MatchesObject>> getOryginalMatches() {
        return oryginalMatchesLiveData;
    }

    @Override
    public LiveData<ArrayList<String>> getTags() {
        return myTagsLiveData;
    }
}

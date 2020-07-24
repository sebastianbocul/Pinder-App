package com.pinder.app.Matches;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.LocationActivity;
import com.pinder.app.MainFragmentMenager;
import com.pinder.app.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    MatchesTagsAdapter adapter;
    ArrayList<String> myTags = new ArrayList<>();
    int iterator = 0;
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
    private ArrayList<MatchesObject> resultMatches = new ArrayList<MatchesObject>();
    private ArrayList<MatchesObject> oryginalMatches = new ArrayList<MatchesObject>();
    private ArrayList<String> usersID = new ArrayList<>();
    private String sortId;

    public MatchesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MatchesFragment newInstance(String param1, String param2) {
        MatchesFragment fragment = new MatchesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        locationButton = getView().findViewById(R.id.locationButton);
        allMatches = getView().findViewById(R.id.allMatches);
        sortByTextView = getView().findViewById(R.id.sortByText);
        myRecyclerView = getView().findViewById(R.id.recyclerView);
        myRecyclerView.setNestedScrollingEnabled(false);
        myRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(mMatchesLayoutManager);
        recyclerView = getView().findViewById(R.id.tagsRecyclerViewMatches);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), getContext());
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
        Intent intent = new Intent(getContext(), LocationActivity.class);
        startActivity(intent);
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference matchDbAd = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        matchDbAd.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    matchesCount = (int) dataSnapshot.getChildrenCount();
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

    private void getLastMessage(DataSnapshot match) {
        sortId = "00";
        String chatId = match.child("ChatId").getValue().toString();
        DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);
        DataSnapshot mMatch = match;
        chatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                boolean createdByMe = true;
                String message = "No messages...";
                sortId = chatId;
                Log.d("matchesactivity", "onChildAdded ds : " + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    DataSnapshot ds = dataSnapshot;
                    message = ds.child("text").getValue().toString();
                    createdByUser = ds.child("createdByUser").getValue().toString();
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

    private void fetchMatchInformation(String key, String chatId, final boolean createdByMe, final String message, String mSortId) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (resultMatches.size() == matchesCount) {
                        resultMatches = sortCollection(resultMatches);
                        mMatchesAdapter.notifyDataSetChanged();
                    }
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
                    Map tagMap = new HashMap();
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
                        resultMatches.add(obj);
                        oryginalMatches.add(obj);
                    } else {

                        resultMatches = sortCollection(resultMatches);
                        oryginalMatches = sortCollection(oryginalMatches);
                        for (int i = 0; i < resultMatches.size(); i++) {
                            if (resultMatches.get(i).getUserId().equals(obj.getUserId())) {
                                oryginalMatches.get(i).setLastMessage(obj.getLastMessage());
                                oryginalMatches.get(i).setSortId(obj.getSortId());
                                oryginalMatches.get(i).setCreatedByMe(obj.isCreatedByMe());
                                resultMatches.get(i).setLastMessage(obj.getLastMessage());
                                resultMatches.get(i).setSortId(obj.getSortId());
                                resultMatches.get(i).setCreatedByMe(obj.isCreatedByMe());
                            }
                        }
                    }
                    resultMatches = sortCollection(resultMatches);
                    mMatchesAdapter.notifyDataSetChanged();
                    if (resultMatches.size() == matchesCount) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void onBackPressed() {
        Intent startMain = new Intent(getContext(), MainFragmentMenager.class);
        startActivity(startMain);
    }

    public void onBack(View view) {
        Intent startMain = new Intent(getContext(), MainFragmentMenager.class);
        startActivity(startMain);
    }

    private List<MatchesObject> getDataSetMatches() {
        return resultMatches;
    }

    private void loadTagsRecyclerView() {
        DatabaseReference matchesReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        matchesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    iterator = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        iterator--;
                        singeMatchTags(matchesReference.child(ds.getKey()));
                    }
                } else {
                    myTags.add("No matches");
                    adapter = new MatchesTagsAdapter(getContext(), myTags);
                    recyclerView.setAdapter(adapter);
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
                if (iterator == 0) {
                    adapter = new MatchesTagsAdapter(getContext(), myTags);
                    recyclerView.setAdapter(adapter);
                    adapter.setClickListener(new MatchesTagsAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            sortBy = myTags.get(position);
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

    public void fillRecyclerViewByTags(String tag) {
        ArrayList mutualTags = new ArrayList();
        ArrayList<MatchesObject> bufforMatches = new ArrayList<MatchesObject>();
        if (tag.equals("AllButtonClicked")) {
            sortByTextView.setText("#" + "all");
            oryginalMatches = sortCollection(oryginalMatches);
            mMatchesAdapter = new MatchesAdapter(oryginalMatches, getContext());
            myRecyclerView.setAdapter(mMatchesAdapter);
            return;
        }
        for (MatchesObject mo : oryginalMatches) {
            mutualTags = mo.getMutualTags();
            //     boolean contains  = mutualTags.contains(tag);
            if (mutualTags.contains(tag)) {
                bufforMatches.add(mo);
            }
        }
        if (bufforMatches.size() != 0) {
            resultMatches.clear();
            resultMatches = bufforMatches;
            resultMatches = sortCollection(resultMatches);
            mMatchesAdapter = new MatchesAdapter(resultMatches, getContext());
            myRecyclerView.setAdapter(mMatchesAdapter);
        }
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
}

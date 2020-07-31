package com.pinder.app.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.LocationActivity;
import com.pinder.app.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String currentUserID;
    private ImageView locationButton;
    private String createdByUser;
    private int matchesCount;
    private String sortBy;
    private TextView sortByTextView;
    private Button allMatches;

    private ArrayList<String> usersID = new ArrayList<>();


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
        //mMatchesAdapter = new MatchesAdapter(resultMatches, getContext());
        myRecyclerView.setAdapter(mMatchesAdapter);
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);







        adapter = new MatchesTagsAdapter(getActivity(), myTags);
        recyclerView.setAdapter(adapter);
        MatchesViewModel matchesViewModel = new ViewModelProvider(getActivity()).get(MatchesViewModel.class);

        matchesViewModel.getTags().observe(getActivity(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                myTags.clear();
                myTags.addAll(strings);
                adapter.notifyDataSetChanged();
                adapter.setClickListener(new MatchesTagsAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Log.d("MatchesFragmentLog" , "myTags name : " + myTags.get(position).toString() +  "   pos: " + position);
//                                sortBy = myTags.get(position);
//                                sortByTextView.setText("#" + sortBy);
//                                fillRecyclerViewByTags(sortBy);
                            }
                        });
            }
        });



//        getUserMatchId();
//        loadTagsRecyclerView();
        allMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillRecyclerViewByTags("AllButtonClicked",myTags);
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
      //
    }

    private void goToLocationActivity() {
        Intent intent = new Intent(getContext(), LocationActivity.class);
        startActivity(intent);
    }

    public void fillRecyclerViewByTags(String tag, ArrayList<MatchesObject> oryginalMatches) {
        ArrayList<MatchesObject> resultMatches = new ArrayList<MatchesObject>();
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

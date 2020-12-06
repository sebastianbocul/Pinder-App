package com.pinder.app.ui;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pinder.app.LocationActivity;
import com.pinder.app.R;
import com.pinder.app.adapters.MatchesAdapter;
import com.pinder.app.adapters.MatchesTagsAdapter;
import com.pinder.app.models.MatchesObject;
import com.pinder.app.util.Resource;
import com.pinder.app.viewmodels.MatchesViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
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
    private ImageView locationButton;
    private String sortBy;
    private TextView sortByTextView;
    private Button allMatches;
    ArrayList<MatchesObject> oryginalMatches = new ArrayList<>();
    @Inject
    MatchesViewModel matchesViewModel;

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
        locationButton = getView().findViewById(R.id.locationButton);
        allMatches = getView().findViewById(R.id.allMatches);
        sortByTextView = getView().findViewById(R.id.sortByText);
        myRecyclerView = getView().findViewById(R.id.recyclerView);
        myRecyclerView.setNestedScrollingEnabled(false);
        myRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(mMatchesLayoutManager);
        recyclerView = getView().findViewById(R.id.tagsRecyclerViewMatches);
        myRecyclerView.setAdapter(mMatchesAdapter);
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        adapter = new MatchesTagsAdapter(getActivity(), myTags);
        recyclerView.setAdapter(adapter);
        matchesViewModel.tagLD.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                sortByTextView.setText("#" + s);
            }
        });
        matchesViewModel.getOryginalMatches().observe(getActivity(), new Observer<Resource<ArrayList<MatchesObject>>>() {
            @Override
            public void onChanged(Resource<ArrayList<MatchesObject>> matchesObjects) {
                if (matchesObjects != null) {
                    if (matchesObjects.data != null) {
                        ArrayList<MatchesObject> sortedArray = new ArrayList<>();
                        sortedArray = matchesViewModel.getSortedMatches();
                        oryginalMatches.clear();
                        oryginalMatches.addAll(sortedArray);
                        mMatchesAdapter = new MatchesAdapter(oryginalMatches, getContext());
                        myRecyclerView.setAdapter(mMatchesAdapter);
                    }
                }
            }
        });
        matchesViewModel.getTags().observe(getActivity(), new Observer<Resource<ArrayList<String>>>() {
            @Override
            public void onChanged(Resource<ArrayList<String>> strings) {
                if (strings.data !=null) {
                    myTags.clear();
                    myTags.addAll(strings.data);
                    adapter.notifyDataSetChanged();
                    adapter.setClickListener(new MatchesTagsAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            sortBy = myTags.get(position);
                            mMatchesAdapter = new MatchesAdapter(matchesViewModel.setTag(sortBy), getContext());
                            myRecyclerView.setAdapter(mMatchesAdapter);
                        }
                    });
                }
            }
        });
        allMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMatchesAdapter = new MatchesAdapter(matchesViewModel.setTag("all"), getContext());
                myRecyclerView.setAdapter(mMatchesAdapter);
            }
        });
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLocationActivity();
            }
        });
    }

    private void goToLocationActivity() {
        Intent intent = new Intent(getContext(), LocationActivity.class);
        startActivity(intent);
    }
}

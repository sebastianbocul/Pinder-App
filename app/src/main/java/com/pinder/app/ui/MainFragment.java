package com.pinder.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.pinder.app.MainFragmentManager;
import com.pinder.app.R;
import com.pinder.app.UsersProfilesActivity;
import com.pinder.app.adapters.CardsAdapter;
import com.pinder.app.adapters.TagsManagerAdapter;
import com.pinder.app.models.Card;
import com.pinder.app.util.Resource;
import com.pinder.app.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView likeButton, dislikeButton;
    private TextView noMoreEditText;
    private SwipeFlingAdapterView flingContainer;
    private CardsAdapter arrayAdapter;
    MainViewModel mainViewModel;
    private static final String TAG = "MainFragment";
    private ProgressBar progressBar;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        noMoreEditText = getView().findViewById(R.id.noMore);
        likeButton = getView().findViewById(R.id.likeButton);
        dislikeButton = getView().findViewById(R.id.dislikeButton);
        flingContainer = getView().findViewById(R.id.frame);
        progressBar = getView().findViewById(R.id.progress_bar);
        List<Card> cardsArray = new ArrayList<Card>();
        arrayAdapter = new CardsAdapter(getContext(), R.layout.item, cardsArray);
        flingContainer.setAdapter(arrayAdapter);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.fetchDataOrUpdateLocationAndFetchData();
        mainViewModel.getCardsArrayLD().observe(getActivity(), new Observer<Resource<ArrayList<Card>>>() {
            @Override
            public void onChanged(Resource<ArrayList<Card>> cards) {
                noMoreEditText.setText("");
                if (cards != null) {
                    noMoreEditText.setOnClickListener(null);
                    switch (cards.status) {
                        case LOADING: {
                            Log.d("ResourceMainFragment", "LOADING: ");
                            noMoreEditText.setText(R.string.loadingUsers);
                            showProgressBar(true);
                            break;
                        }
                        case SUCCESS: {
                            Log.d("ResourceMainFragment", "SUCCESS: ");
                            if (cards.data.size() == 0) {
                                noMoreEditText.setText(R.string.noMoreUsers);
                                setChangeFragmentTextListener();
                            }
                            showProgressBar(false);
                            break;
                        }
                        case EMPTY: {
                            Log.d("ResourceMainFragment", "EMPTY: ");
                            noMoreEditText.setText(R.string.noMoreUsers);
                            showProgressBar(false);
                            setChangeFragmentTextListener();
                            break;
                        }
                        case ERROR: {
                            Log.d("ResourceMainFragment", "ERROR: ");
                            Toast.makeText(getActivity(), "Error loading users", Toast.LENGTH_SHORT).show();
                            showProgressBar(false);
                            break;
                        }
                    }
                    cardsArray.clear();
                    cardsArray.addAll(cards.data);
                    arrayAdapter.notifyDataSetChanged();
                    for (Card ccc : cardsArray) {
                        Log.d(TAG, "Row items : " + ccc.getName() + " dist: " + ccc.getDistance() + " UID: " + ccc.getUserId());
                    }
                }
            }
        });
        ArrayList<String> myTags = new ArrayList<>();
        TagsManagerAdapter adapter = new TagsManagerAdapter(getContext(), myTags);
        RecyclerView recyclerView = getView().findViewById(R.id.mainTagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(adapter);
        mainViewModel.getMyTagsAdapterLD().observe(getActivity(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                Log.d(TAG, "onChanged: LoadTags" + strings);
                ArrayList<String> strings2 = mainViewModel.getMyTagsAdapterLD().getValue();
                Log.d(TAG, "onChanged: LoadTags" + strings2);
                myTags.clear();
                myTags.addAll(strings);
                adapter.notifyDataSetChanged();
            }
        });
        mainViewModel.checkUserStatus(getActivity());
        mainViewModel.updateToken();
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flingContainer.getChildCount() != 0)
                    flingContainer.getTopCardListener().selectRight();
                else
                    Toast.makeText(getContext(), "There is no more users", Toast.LENGTH_SHORT).show();
            }
        });
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flingContainer.getChildCount() != 0)
                    flingContainer.getTopCardListener().selectLeft();
                else
                    Toast.makeText(getContext(), "There is no more users", Toast.LENGTH_SHORT).show();
            }
        });
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                mainViewModel.removeFirstObjectInAdapter();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                mainViewModel.onLeftCardExit((Card) dataObject);
                Toast.makeText(getContext(), "Disliked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                mainViewModel.isConnectionMatch((Card) dataObject, getContext());
                Toast.makeText(getContext(), "Liked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                /*al.add("XML ".concat(String.valueOf(i)));
                CardsAdapter.notifyDataSetChanged();
                i++;*/
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Card user = (Card) dataObject;
                Intent intent = new Intent(getContext(), UsersProfilesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        swipeIfButtonClickedInUserProfile();
    }

    private void swipeIfButtonClickedInUserProfile() {
        //created delay so flingContainer is loaded
        //while we click like/dislike in userprofile we need wait to load Card so swipe left/right will run
        //also delay makes swipe card when fragment is loaded not while loading so it looks nicer.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                Intent intent = getActivity().getIntent();
                if (flingContainer != null) {
                    if (intent.getStringExtra("fromUsersProfilesActivity") != null) {
                        String s = intent.getStringExtra("fromUsersProfilesActivity");
                        if (s.equals("likeButtonClicked")) {
                            if (flingContainer.getChildCount() != 0)
                                flingContainer.getTopCardListener().selectRight();
                            else
                                Toast.makeText(getContext(), "There is no more users", Toast.LENGTH_SHORT).show();
                        }
                        if (s.equals("dislikeButtonClicked")) {
                            if (flingContainer.getChildCount() != 0)
                                flingContainer.getTopCardListener().selectLeft();
                            else
                                Toast.makeText(getContext(), "There is no more users", Toast.LENGTH_SHORT).show();
                        }
                        intent.putExtra("fromUsersProfilesActivity", "finished");
                    }
                }
            }
        }, 500);
    }

    private void showProgressBar(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private void setChangeFragmentTextListener() {
        noMoreEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainFragmentManager) getActivity()).replaceTabPage(R.id.nav_tags);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        //fillTagsAdapter();
        //mainViewModel.updateMyTagsAndSortBydDist();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        mainViewModel.checkUserStatus(getActivity());
        super.onResume();
    }
}

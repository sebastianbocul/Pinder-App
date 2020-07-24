package com.pinder.app.Tags;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopularTagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopularTagsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private RecyclerView popularTagsRecyclerView;
    private ArrayList<TagsPopularObject> popularTagsList = new ArrayList<>();
    private TagsPopularAdapter tagsPopularAdapter;

    public PopularTagsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopularTagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularTagsFragment newInstance(String param1, String param2) {
        PopularTagsFragment fragment = new PopularTagsFragment();
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
        return inflater.inflate(R.layout.fragment_popular_tags, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("tagsManagerFragment", "filling popular tags");
        popularTagsRecyclerView = getView().findViewById(R.id.popularTagsRecyclerView);
        DatabaseReference tagsDatabase = FirebaseDatabase.getInstance().getReference().child("Tags");
        tagsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String tag_name = ds.getKey();
                        int tag_popularity = (int) ds.getChildrenCount();
                        TagsPopularObject popular_tag = new TagsPopularObject(tag_name, tag_popularity);
                        popularTagsList.add(popular_tag);
                    }
                    sortCollection();
                    popularTagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                    tagsPopularAdapter = new TagsPopularAdapter(getActivity().getApplicationContext(), popularTagsList);
                    popularTagsRecyclerView.setAdapter(tagsPopularAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sortCollection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(popularTagsList, Comparator.comparing(TagsPopularObject::getTagPopularity).reversed());
        } else {
            Collections.sort(popularTagsList, new Comparator<TagsPopularObject>() {
                public int compare(TagsPopularObject o1, TagsPopularObject o2) {
                    if (o1.getTagPopularity() == o2.getTagPopularity())
                        return 0;
                    return o1.getTagPopularity() < o2.getTagPopularity() ? -1 : 1;
                }
            });
            Collections.reverse(popularTagsList);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

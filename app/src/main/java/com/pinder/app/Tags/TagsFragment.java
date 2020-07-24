package com.pinder.app.Tags;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth;
    private String currentUserId;
    private TextView ageRangeTextView, maxDistanceTextView;
    private CrystalRangeSeekbar ageRangeSeeker;
    private CrystalSeekbar maxDistanceSeeker;
    private TagsAdapter adapter;
    private Button addTagButton;
    private EditText tagsEditText;
    private RadioGroup mRadioGroup;
    private String ageMin, ageMax, distanceMax;
    private ArrayList<TagsObject> myTagsList;
    private ArrayList<TagsObject> removedTags;
    private RecyclerView recyclerView;
    private MyInterface listener;

    public TagsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagsFragment newInstance(String param1, String param2) {
        TagsFragment fragment = new TagsFragment();
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
        return inflater.inflate(R.layout.fragment_tags, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        ageRangeSeeker = getView().findViewById(R.id.ageRangeSeeker);
        ageRangeTextView = getView().findViewById(R.id.ageRangeTextView);
        maxDistanceSeeker = getView().findViewById(R.id.distanceSeeker);
        maxDistanceTextView = getView().findViewById(R.id.maxDistanceTextView);
        listener = (MyInterface) new TagsManagerFragment();
        Log.d("tagsManager", "getParentFragment " + getParentFragment());
        fillTagsAdapter();
        addTagButton = getView().findViewById(R.id.addButton);
        tagsEditText = getView().findViewById(R.id.tagsEditText);
        mRadioGroup = getView().findViewById(R.id.radioGroup);
        maxDistanceSeeker.setMinStartValue(100);
        maxDistanceSeeker.apply();
        myTagsList = new ArrayList<TagsObject>();
        removedTags = new ArrayList<TagsObject>();
        recyclerView = getView().findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new TagsAdapter(myTagsList);
        recyclerView.setAdapter(adapter);
        ageRangeSeeker.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ageRangeTextView.setText("Age range: " + minValue.toString() + " - " + maxValue.toString());
                ageMin = minValue.toString();
                ageMax = maxValue.toString();
            }
        });
        maxDistanceSeeker.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue) {
                if (minValue.intValue() == 1000) {
                    minValue = 100000;
                    maxDistanceTextView.setText("Max distance: " + "∞");
                } else {
                    maxDistanceTextView.setText("Max distance: " + minValue + " km");
                }
                distanceMax = minValue.toString();
            }
        });
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTagButtonFunction();
            }
        });
        adapter.setOnItemClickListener(new TagsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void addTagButtonFunction() {
        if (tagsEditText.getText().toString().length() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "Fill tag name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity().getApplicationContext(), "Choose gender to find", Toast.LENGTH_SHORT).show();
            return;
        }
        for (TagsObject tmo : myTagsList) {
            if (tmo.getTagName().equals(tagsEditText.getText().toString().toLowerCase())) {
                Toast.makeText(getActivity().getApplicationContext(), "Duplicate tag", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = getView().findViewById(selectedId);
        String tagName = tagsEditText.getText().toString().toLowerCase();
        String gender = radioButton.getText().toString();
        String mAgeMax = ageMax;
        String mAgeMin = ageMin;
        String mDistance = distanceMax;
        TagsObject obj = new TagsObject(tagName, gender, mAgeMin, mAgeMax, mDistance);
        myTagsList.add(obj);
        listener.doSomethingWithData(myTagsList, removedTags);
        Toast.makeText(getActivity().getApplicationContext(), "Tag added!", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void fillTagsAdapter() {
        Log.d("tagsManagerFragment", "FillingTagsAdapter");
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("tags");
        ArrayList<String> myTags = new ArrayList<>();
        RecyclerView recyclerView = getView().findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String tagName = ds.getKey();
                        String gender = ds.child("gender").getValue().toString();
                        String mAgeMax = ds.child("maxAge").getValue().toString();
                        String mAgeMin = ds.child("minAge").getValue().toString();
                        String mDistance = ds.child("maxDistance").getValue().toString();
                        TagsObject obj = new TagsObject(tagName, gender, mAgeMin, mAgeMax, mDistance);
                        myTagsList.add(obj);
                        listener.doSomethingWithData(myTagsList, removedTags);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void removeItem(int position) {
        removedTags.add(myTagsList.get(position));
        myTagsList.remove(position);
        Toast.makeText(getActivity().getApplicationContext(), "Tag removed!", Toast.LENGTH_SHORT).show();
        listener.doSomethingWithData(myTagsList, removedTags);
        adapter.notifyItemRemoved(position);
    }
}

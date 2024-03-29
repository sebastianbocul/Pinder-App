package com.pinder.app.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pinder.app.R;
import com.pinder.app.adapters.TagsAdapter;
import com.pinder.app.models.TagsObject;
import com.pinder.app.ui.dialogs.AddEditTagDialog;
import com.pinder.app.util.Resource;
import com.pinder.app.viewmodels.TagsViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class TagsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TagsAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<TagsObject> arrayList = new ArrayList<>();
    @Inject
    public TagsViewModel tagsViewModel;
    private FloatingActionButton addTagButton;
    //public static final int LOAD = 1;
    public static final int ADD = 1;
    public static final int REMOVE = -1;
    private static final String TAG = "TagsFragment";
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
        recyclerView = view.findViewById(R.id.tagsRecyclerView);
        addTagButton = getView().findViewById(R.id.button_add_tag);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new TagsAdapter(arrayList);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        fillTagsAdapter();
        adapter.setOnItemClickListener(new TagsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AddEditTagDialog addEditTagDialog = new AddEditTagDialog(arrayList.get(position));
                addEditTagDialog.show(getActivity().getSupportFragmentManager(), "Edit tag");
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditTagDialog addEditTagDialog = new AddEditTagDialog(arrayList);
                addEditTagDialog.show(getActivity().getSupportFragmentManager(), "Add tag");
            }
        });
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            removeItem(viewHolder.getAdapterPosition());
        }
    };


    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void fillTagsAdapter() {
        tagsViewModel.getAllTags().observe(getViewLifecycleOwner(), new Observer<Resource<List<TagsObject>>>() {
            @Override
            public void onChanged(Resource<List<TagsObject>> tagsObjects) {
                if(tagsObjects != null){
                    switch (tagsObjects.status){
                        case SUCCESS:
                            Log.d(TAG, "onChanged: SUCCESS");
                            if(tagsObjects.data!=null){
                                int request = tagsObjects.data.size() - arrayList.size();
                                switch (request) {
                                    case ADD: {
                                        arrayList.clear();
                                        arrayList.addAll(tagsObjects.data);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    }
                                    case REMOVE: {
                                        arrayList.clear();
                                        arrayList.addAll(tagsObjects.data);
                                        adapter.notifyItemRemoved(tagsViewModel.position);
                                        break;
                                    }
                                    default: {
                                        arrayList.clear();
                                        arrayList.addAll(tagsObjects.data);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }      
                            }
                            break;
                        case LOADING:
                            Log.d(TAG, "onChanged: LOADING");
                            if(tagsObjects.data!=null){
                                int request = tagsObjects.data.size() - arrayList.size();
                                switch (request) {
                                    case ADD: {
                                        arrayList.clear();
                                        arrayList.addAll(tagsObjects.data);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    }
                                    case REMOVE: {
                                        arrayList.clear();
                                        arrayList.addAll(tagsObjects.data);
                                        adapter.notifyItemRemoved(tagsViewModel.position);
                                        break;
                                    }
                                    default: {
                                        arrayList.clear();
                                        arrayList.addAll(tagsObjects.data);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }   
                            }
                            break;
                        case ERROR:
                            Log.d(TAG, "onChanged: ERROR");
                            break;
                    }
                }
                
            }
        });
    }

    public void removeItem(int position) {
        TagsObject tagToDel = adapter.getItem(position);
        tagsViewModel.removeTag(tagToDel);
        tagsViewModel.position = position;
//        tagsViewModel.REQUEST_MODE=REMOVE;
    }
}

package com.pinder.app.Home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.pinder.app.MyFunctions.CalculateDistance;
import com.pinder.app.MyFunctions.StringDateToAge;
import com.pinder.app.Notifications.APIService;
import com.pinder.app.Notifications.Client;
import com.pinder.app.Notifications.Data;
import com.pinder.app.Notifications.Sender;
import com.pinder.app.Notifications.Token;
import com.pinder.app.PopActivity;
import com.pinder.app.Profile.ProfileFragment;
import com.pinder.app.R;
import com.pinder.app.Settings.SettingsViewModel;
import com.pinder.app.Tags.MainTags.TagsFragmentViewModel;
import com.pinder.app.Tags.MainTags.TagsObject;
import com.pinder.app.Tags.TagsManagerAdapter;
import com.pinder.app.UsersProfilesActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

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
    private String currentUID;
    private TextView noMoreEditText;
    private SwipeFlingAdapterView flingContainer;

    String mUID;
    APIService apiService;
    boolean notify = false;
    private com.pinder.app.Home.arrayAdapter arrayAdapter;
 //   private FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    private FirebaseAuth mAuth;
  //  private DatabaseReference usersDb;
    private TagsManagerAdapter adapter;
 //   private ArrayList<TagsObject> myTagsList = new ArrayList<>();
    private double myLatitude, myLongitude;
    MainViewModel mainViewModel;
    private String sortByDistance = "true";
    List<cards> rowItems;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        try {
            currentUID = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
        }
        noMoreEditText = getView().findViewById(R.id.noMore);

        List<cards>  rowItemsRxJava = new ArrayList<cards>();


        likeButton = getView().findViewById(R.id.likeButton);
        dislikeButton = getView().findViewById(R.id.dislikeButton);


        flingContainer = getView().findViewById(R.id.frame);
        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(getActivity(), R.layout.item, rowItems);
        flingContainer.setAdapter(arrayAdapter);
        Context ctx= getActivity();
        SettingsViewModel settingViewModel  = new ViewModelProvider(getActivity()).get(SettingsViewModel.class);
        TagsFragmentViewModel tagsFragmentViewModel  = new ViewModelProvider(getActivity()).get(TagsFragmentViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        mainViewModel.getRowItemsLD().observe(getActivity(), new Observer<ArrayList<cards>>() {
            @Override
            public void onChanged(ArrayList<cards> cards) {
                rowItems.clear();
                rowItems.addAll(cards);
               // arrayAdapter =;
                Log.d("MainFragmentLog", "cards: " + cards.size() +"   row: " +  rowItems.size());
//                flingContainer.setAdapter( new arrayAdapter(ctx, R.layout.item, rowItems));
             //   for()
                arrayAdapter.notifyDataSetChanged();
                if(cards!=null && cards.size()!=0 && getContext()!=null){


                }

//                rowItems=cards;
//                arrayAdapter.notifyDataSetChanged();
            }
        });




        //check location permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


       // flingContainer.setAdapter(arrayAdapter);
        //create APISERVICE
        Client client = new Client();
        apiService = client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        checkUserStatus();
        //update token
        updateToken(FirebaseInstanceId.getInstance().getToken());
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
        swipeIfButtonClickedInUserProfile();
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUID).setValue(true);
                Toast.makeText(getContext(), "Disliked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yes").child(currentUID).setValue(true);
                isConnectionMatch(userId, obj);
                Toast.makeText(getContext(), "Liked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                /*al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
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
                cards objec = (cards) dataObject;
                String userId = objec.getUserId();
                Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), UsersProfilesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("userId", userId);
                Log.d("mainActivity", "Number of cards : " + rowItems);
                ArrayList<String> stringList = new ArrayList(objec.getMutualTagsMap().keySet());
                Log.d("main", stringList.toString());
                intent.putStringArrayListExtra("mutualTags", stringList);
                startActivity(intent);
            }
        });
    }

    private void swipeIfButtonClickedInUserProfile() {
        //created delay so flingContainer is loaded - coudnt find other solutin
        //while we click like/dislike in userprofile we need wait to load cards so swipe left/right will run
        //perhaps if we switch from activities to fragments we wont need to load data and delay will be useless
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
                    }
                }
            }
        }, 500);
    }

    private void isConnectionMatch(final String userId, cards obj) {

        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUID).child("connections").child("yes").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "New connection!", Toast.LENGTH_LONG).show();
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("ChatId").setValue(key);
                    usersDb.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("mutualTags").updateChildren(obj.getMutualTagsMap());
                    usersDb.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("mutualTags").updateChildren(obj.getMutualTagsMap());
                    String matchId = dataSnapshot.getKey();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String myName = currentUID;
                            if (notify) {
                                sendNotification(matchId, myName, " ");
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    notify = true;
                    //popactivity when matched
                    Intent i = new Intent(getActivity().getApplicationContext(), PopActivity.class);
                    i.putExtra("matchId", matchId);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }





    @Override
    public void onStart() {
        super.onStart();
        updateMyInfo();
        //fillTagsAdapter();
        Log.d("mainActivity", "rowItems on Start: " + rowItems.size());
        Log.d("mainfragmentfunctions", "onStart: " + rowItems.size());
         mainViewModel.getUsersFromDb();

//        if (mainViewModel.getRowItemsLD().getValue().size() == 0) {
//
//        }
    }

    private void updateMyInfo() {
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myLatitude = Double.parseDouble(dataSnapshot.child("location").child("latitude").getValue().toString());
                    myLongitude = Double.parseDouble(dataSnapshot.child("location").child("longitude").getValue().toString());
                    if (dataSnapshot.child("sortByDistance").exists()) {
                        sortByDistance = dataSnapshot.child("sortByDistance").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    @Override
    public void onResume() {
        Log.d("mainfragmentfunctions", "onResume: " + rowItems.size());
        checkUserStatus();
        super.onResume();
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();
            //save UID of current signin user in shared preferences
            SharedPreferences sp = getActivity().getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        }
    }

    private void sendNotification(String matchId, String myName, String sendMessageText) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(matchId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(currentUID, R.drawable.login_photo, "Check out now!", "New match!", matchId);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

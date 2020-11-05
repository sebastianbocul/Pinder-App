package com.pinder.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.login.LoginManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.BaseApplication;
import com.pinder.app.LoginActivity;
import com.pinder.app.MainFragmentManager;
import com.pinder.app.R;
import com.pinder.app.ui.dialogs.BugsAndImprovementsDialog;
import com.pinder.app.ui.dialogs.LicencesDialog;
import com.pinder.app.ui.dialogs.PrivacyDialog;
import com.pinder.app.ui.dialogs.TermsDialog;
import com.pinder.app.util.Resource;
import com.pinder.app.util.StringDateToAge;
import com.pinder.app.utils.DisableButton;
import com.pinder.app.viewmodels.SettingsViewModel;

import java.util.Calendar;

import static com.pinder.app.BaseApplication.*;
import static com.pinder.app.BaseApplication.LoginEnum.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId = mAuth.getCurrentUser().getUid();
    private Button logoutUser, deleteUser, privacyPolicyButton, termsButton, licenceButton;
    private Switch mapLocationSwitch, sortUsersByDistanceSwitch;
    private EditText date;
    private boolean dateValid = false;
    private Button bugsAndImprovements;
    private Context context;
    SharedPreferences prefs;
    ProgressBar progressBar;

    int i = 0;
    SettingsViewModel settingsViewModel;
    int logoutFlag = 0;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context=view.getContext();
        sortUsersByDistanceSwitch = getView().findViewById(R.id.sortUsersByDistance);
        mapLocationSwitch = getView().findViewById(R.id.mapLocationSwitch);
        logoutUser = getView().findViewById(R.id.logoutUser);
        deleteUser = getView().findViewById(R.id.deleteUser);
        date = getView().findViewById(R.id.date);
        privacyPolicyButton = getView().findViewById(R.id.privacyPolicyButton);
        termsButton = getView().findViewById(R.id.termsButton);
        licenceButton = getView().findViewById(R.id.licenceButton);
        bugsAndImprovements = getView().findViewById(R.id.bugs_improvement);
        progressBar = getView().findViewById(R.id.progress_bar);
        settingsViewModel = new ViewModelProvider(getActivity()).get(SettingsViewModel.class);
        logoutFlag = 0;
        setObservers();
        date.addTextChangedListener(new TextWatcher() {
            String clean;
            String cleanC;
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().equals(current)) {
                        dateValid = false;
                        clean = s.toString().replaceAll("[^\\d.]", "");
                        cleanC = current.replaceAll("[^\\d.]", "");
                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;
                        if (clean.length() < 8) {
                            clean = clean + ddmmyyyy.substring(clean.length());
                        } else {
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day = Integer.parseInt(clean.substring(0, 2));
                            int mon = Integer.parseInt(clean.substring(2, 4));
                            int year = Integer.parseInt(clean.substring(4, 8));
                            if (mon > 12) mon = 12;
                            if (mon == 0) mon = 1;
                            cal.set(Calendar.MONTH, mon - 1);
                            year = (year < 1900) ? 1900 : (year > 2019) ? 2019 : year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012
                            dateValid = true;
                            day = (day < 1) ? 1 : (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                            clean = String.format("%02d%02d%02d", day, mon, year);
                            String strToAge = String.format("%s/%s/%s", clean.substring(0, 2),
                                    clean.substring(2, 4),
                                    clean.substring(4, 8));
                            int age = new StringDateToAge().stringDateToAge(strToAge);
                            if (age < 18) {
                                Calendar today = Calendar.getInstance();
                                int curYear = today.get(Calendar.YEAR);
                                year = curYear - 18;
                                int curMon = today.get(Calendar.MONTH);
                                mon = curMon;
                                int curDay = today.get(Calendar.DAY_OF_MONTH);
                                day = curDay;
                                clean = String.format("%02d%02d%02d", day, mon, year);
                                Toast.makeText(getContext(), "You must be 18+", Toast.LENGTH_SHORT).show();
                            }
                        }
                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));
                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        date.setText(current);
                        //    settingsViewModel.setDate(current);
                        date.setSelection(sel < current.length() ? sel : current.length());
                    }
                } catch (Exception e) {
                    dateValid = false;
                    Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                    date.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mapLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsViewModel.setShowMyLocation(isChecked);
            }
        });
        sortUsersByDistanceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("sortByDistance", isChecked).apply();
                settingsViewModel.setSortByDistance(isChecked, prefs);
//                Boolean sortByDistanceTemp = prefs.getBoolean("sortByDistance", false);
//                settingsViewModel.setSortByDistance(isChecked);
            }
        });
        logoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyDb(1);
            }
        });
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        privacyPolicyButton.setOnClickListener(v -> {
            PrivacyDialog pd = new PrivacyDialog();
            pd.show(getActivity().getSupportFragmentManager(), "Privacy dialog");
        });
        termsButton.setOnClickListener(v -> {
            TermsDialog td = new TermsDialog();
            td.show(getActivity().getSupportFragmentManager(), "Terms Dialog");
        });
        licenceButton.setOnClickListener(v -> {
            LicencesDialog ld = new LicencesDialog();
            ld.show(getActivity().getSupportFragmentManager(), "Licences Dialog");
        });
        bugsAndImprovements.setOnClickListener(v -> {
            openReportDialog();
        });

        helperButtons();
    }

    private void helperButtons() {
        Button restartMatches,moveUsersLocToGeoFire;
        restartMatches = getView().findViewById(R.id.restartMatches);
        moveUsersLocToGeoFire = getView().findViewById(R.id.moveUsersLocToGeoFire);
        DisableButton.disableButton(restartMatches);
        DisableButton.disableButton(moveUsersLocToGeoFire);
        DisableButton.disableButtonInDebug(deleteUser);
        restartMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // settingsViewModel.clearInstances();
                restartMatchesFun();
            }
        });
        moveUsersLocToGeoFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                DatabaseReference geoFireReference = FirebaseDatabase.getInstance().getReference().child("Geofire");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            geoFireReference.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ds) {
                                    if(ds.getValue()==null){
                                        if(dataSnapshot.child("location").exists()){
                                            LatLng loc = new LatLng(Double.parseDouble(dataSnapshot.child("location").child("latitude").getValue().toString()),Double.parseDouble(dataSnapshot.child("location").child("longitude").getValue().toString()));
                                            Log.d("moveUsersToGeoloc", "users in geoLoc: " + dataSnapshot.getKey() + "latlng:"+ loc.toString());
                                            DatabaseReference geofire = FirebaseDatabase.getInstance().getReference().child("Geofire");
                                            GeoFire geoFire = new GeoFire(geofire);
                                            geoFire.setLocation(dataSnapshot.getKey(), new GeoLocation(loc.latitude, loc.longitude), new GeoFire.CompletionListener() {
                                                @Override
                                                public void onComplete(String key, DatabaseError error) {
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    @Override
    public void onDetach() {
            updateMyDb(0);
        super.onDetach();
    }

    private void restartMatchesFun() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to restart your matches?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingsViewModel.restartMatches();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setObservers(){
        settingsViewModel.getLogoutLiveData().observe(getActivity(), new Observer<Resource<Integer>>() {
            @Override
            public void onChanged(Resource<Integer> integerResource) {
                if(integerResource!= null){
                    switch (integerResource.status){
                        case LOADING:
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            if(integerResource.data!=null){
                                if(integerResource.data==1){
                                    logoutUser();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            break;
                        default:
                            progressBar.setVisibility(View.GONE);
                            break;
                    }
                }
            }
        });
        settingsViewModel.getDate().observe(getActivity(), new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String s) {
                date.setText(s);
                i++;
            }
        });
        settingsViewModel.getShowMyLocation().observe(getActivity(), new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mapLocationSwitch.setChecked(aBoolean);
            }
        });
        settingsViewModel.getSortByDistance().observe(getActivity(), new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                sortUsersByDistanceSwitch.setChecked(aBoolean);
            }
        });
    }

    public void logoutUser() {
        Intent intent = new Intent(context, LoginActivity.class);
        LoginManager.getInstance().logOut();
        //settingsViewModel.clearInstances();
        mAuth.signOut();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        UserStatus = NOT_LOGGED;
        startActivity(intent);
    }

    public void deleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete account?").setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "User account deleted.", Toast.LENGTH_LONG).show();
                            settingsViewModel.deleteWithRxJava(userId);
                        } else {
                            AlertDialog.Builder error = new AlertDialog.Builder(getContext());
                            error.setMessage("Due to safety reasons please re-login and try again").setCancelable(false)
                                    .setTitle("Credentials too old")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            AlertDialog alertDialog = error.create();
                            alertDialog.show();
                        }
                    }
                });
    }

    private void updateMyDb(int logoutFlag) {
        if(UserStatus == LOGGED){
            settingsViewModel.setDate(date.getText().toString());
            settingsViewModel.updateMyDb(dateValid,logoutFlag);
        }
    }

    private void openReportDialog() {
        String myId = mAuth.getCurrentUser().getUid();
        BugsAndImprovementsDialog bugsAndImprovementsDialog = new BugsAndImprovementsDialog(myId);
        bugsAndImprovementsDialog.show(getActivity().getSupportFragmentManager(), "Bugs and improvements");
    }
}

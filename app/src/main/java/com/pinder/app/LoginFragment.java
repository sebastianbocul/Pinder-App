package com.pinder.app;

import android.Manifest;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.persistance.MainFirebase;
import com.pinder.app.persistance.MatchesFirebase;
import com.pinder.app.persistance.PopularTagsFirebase;
import com.pinder.app.persistance.ProfileFirebase;
import com.pinder.app.persistance.SettingsFirebase;
import com.pinder.app.persistance.TagsFirebase;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.repository.MatchesRepository;
import com.pinder.app.repository.PopularTagsRepository;
import com.pinder.app.repository.ProfileRepository;
import com.pinder.app.repository.SettingsRepository;
import com.pinder.app.repository.TagsRepository;
import com.pinder.app.ui.dialogs.PrivacyDialog;
import com.pinder.app.ui.dialogs.TermsDialog;
import com.pinder.app.util.ExpandCollapseView;
import com.pinder.app.util.PaintText;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.WithFragmentBindings;

import static com.pinder.app.util.ExpandCollapseView.decideExpandOrCollapse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@WithFragmentBindings
@AndroidEntryPoint
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String EMAIL = "email";
    private static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private Button mLogin;
    private int RC_SIGN_IN = 1001;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private TextView regulationsTextView, registerTextView;
    private LinearLayout myLayout;
    private Button continueFacebook, continueGoogle;
    private ViewGroup continueEmailLayout, continuePhoneLayout, phoneVerificationLayout;
    private Button continueEmail, continuePhone;
    private Button sendPhoneAuth, phoneVerificationButton;
    private EditText phoneEditText, phoneVerificationEditText;
    private String phoneAuthCode;
    private ProgressBar progressBar;
    private TextView logoTextView;
    private String registerProvider = "external";
    private ProgressBar progressBarGoogle, progressBarEmail, progressBarPhone, progressBarFacebook;
    @Inject
    Application application;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setObjectsById();
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(application);
        AppEventsLogger.activateApp(application);
        callbackManager = CallbackManager.Factory.create();
        setOnClickListeners();
        clearInstances();
        PaintText.paintLogo(logoTextView, "Pinder");
        clearNotifications();
        prepareGoogleSignIn();
        setRegulationsClickable();
        setRegisterClickable();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myLayout.setVisibility(View.VISIBLE);
        authStateListener();
    }

    private void setObjectsById() {
        myLayout = getView().findViewById(R.id.mainLayout);
        continueFacebook = getView().findViewById(R.id.continue_facebook);
        mLogin = getView().findViewById(R.id.login);
        mEmail = getView().findViewById(R.id.email);
        mPassword = getView().findViewById(R.id.password);
        continueEmail = getView().findViewById(R.id.continue_email);
        continueGoogle = getView().findViewById(R.id.continue_google);
        registerTextView = getView().findViewById(R.id.registerTextView);
        regulationsTextView = getView().findViewById(R.id.regulationsTextView);
        continueEmailLayout = getView().findViewById(R.id.continue_email_layout);
        continuePhoneLayout = getView().findViewById(R.id.continue_with_phone_layout);
        continuePhone = getView().findViewById(R.id.continue_with_phone);
        sendPhoneAuth = getView().findViewById(R.id.send_phone_auth);
        phoneEditText = getView().findViewById(R.id.phone_edit_text);
        phoneVerificationEditText = getView().findViewById(R.id.phone_verification_edit_text);
        phoneVerificationButton = getView().findViewById(R.id.phone_verification_button);
        phoneVerificationLayout = getView().findViewById(R.id.phone_verification_layout);
        progressBar = getView().findViewById(R.id.progress_bar);
        logoTextView = getView().findViewById(R.id.logo_text_view);
        progressBarGoogle = getView().findViewById(R.id.progress_bar_google);
        progressBarEmail = getView().findViewById(R.id.progress_bar_email);
        progressBarPhone = getView().findViewById(R.id.progress_bar_phone);
        progressBarFacebook = getView().findViewById(R.id.progress_bar_facebook);
    }

    private void setOnClickListeners() {
        continueEmail.setOnClickListener(v -> {
            if (continuePhoneLayout.getVisibility() == View.VISIBLE) {
                ExpandCollapseView.collapse(continuePhoneLayout);
            }
            decideExpandOrCollapse(continueEmailLayout);
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEmailPassword();
            }
        });
        continueGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInGoogle();
            }
        });
        continueFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: facebook");
                LoginManager.getInstance().logInWithReadPermissions(LoginFragment.this, Arrays.asList(EMAIL));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAuth(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        continuePhone.setOnClickListener(v -> {
            if (continueEmailLayout.getVisibility() == View.VISIBLE) {
                ExpandCollapseView.collapse(continueEmailLayout);
            }
            decideExpandOrCollapse(continuePhoneLayout);
        });
        sendPhoneAuth.setOnClickListener(v -> {
            String phoneNumber = phoneEditText.getText().toString().trim();
            // Configure faking the auto-retrieval with the whitelisted numbers.
//            FirebaseAuthSettings firebaseAuthSettings = mAuth.getFirebaseAuthSettings();
//            firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+48790712419", "123123");
            if (phoneNumber.length() == 0) {
                return;
            }
            Log.d("PhoneAuth", "onButtonClicked");
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(getActivity())                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
            progressBar.setVisibility(View.VISIBLE);
        });
        phoneVerificationButton.setOnClickListener(v -> {
            String userVerificationId = phoneVerificationEditText.getText().toString().trim();
            if (userVerificationId.length() == 0) {
                return;
            }
            validatePhoneCode(userVerificationId);
        });
    }

    private void authStateListener() {
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    dr.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    disableProgressBars();
                                    if (dataSnapshot.child("Users").child(user.getUid()).exists()) {
                                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                            Intent intent = new Intent(getActivity(), MainFragmentManager.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        } else {
                                            Intent requestLocationActivity = new Intent(getActivity(), RequestLocationPermissionActivity.class);
                                            requestLocationActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(requestLocationActivity);
                                        }
                                    } else {
                                        Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                                        intent.putExtra("register_type", registerProvider);
                                        startActivity(intent);
                                    }
                                }
                            }, 300);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        };
    }

    private void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void prepareGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.d("PhoneAuth", "onVerificationCompleted:" + credential);
            progressBar.setVisibility(View.GONE);
            phoneVerificationEditText.setText("Auto-fill");
            handlePhoneAuth(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            progressBar.setVisibility(View.GONE);
            Log.w("PhoneAuth", "onVerificationFailed", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(getActivity(), "Please check phone number format", Toast.LENGTH_SHORT).show();
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getActivity(), "Too many requests! Please try again later.", Toast.LENGTH_SHORT).show();
                // The SMS quota for the project has been exceeded
                // ...
            }
            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId, token);
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            ExpandCollapseView.expand(phoneVerificationLayout);
            phoneAuthCode = verificationId;
            Log.d("PhoneAuth", "onCodeSent:" + verificationId);
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            ExpandCollapseView.collapse(phoneVerificationLayout);
            Toast.makeText(getActivity(), "Oops, something went wrong!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    };

    private void setRegisterClickable() {
        String text = registerTextView.getText().toString();
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                registerProvider = "email";
                Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                intent.putExtra("register_type", registerProvider);
                startActivity(intent);
            }
        };
        ss.setSpan(clickableSpan, 27, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerTextView.setText(ss);
        registerTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setRegulationsClickable() {
        String text = regulationsTextView.getText().toString();
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                TermsDialog rg = new TermsDialog();
                rg.show(getChildFragmentManager(), "Terms Dialog");
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                PrivacyDialog pd = new PrivacyDialog();
                pd.show(getChildFragmentManager(), "Privacy Dialog");
            }
        };
        ss.setSpan(clickableSpan1, 29, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 39, 53, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        regulationsTextView.setHighlightColor(Color.TRANSPARENT);
        regulationsTextView.setText(ss);
        regulationsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void validateEmailPassword() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(getActivity(), "Wrong email", Toast.LENGTH_SHORT).show();
            mEmail.setError("Enter email!");
        }
        if (password.isEmpty()) {
            mPassword.setError("Enter password!");
            Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_SHORT).show();
        }
        if (!email.isEmpty() && !password.isEmpty()) {
            handleEmailAuth(email, password);
        }
    }

    private void validatePhoneCode(String userVerificationId) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneAuthCode, userVerificationId);
        handlePhoneAuth(credential);
    }

    private void logInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            handleGoogleAuth(account);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("myLog", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void handlePhoneAuth(PhoneAuthCredential credential) {
        progressBarPhone.setVisibility(View.VISIBLE);
        registerProvider = "phone";
        authCredentials(credential);
    }

    private void handleEmailAuth(String email, String password) {
        progressBarEmail.setVisibility(View.VISIBLE);
        registerProvider = "email";
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        authCredentials(credential);
    }

    private void handleFacebookAuth(AccessToken token) {
        progressBarFacebook.setVisibility(View.VISIBLE);
        registerProvider = "external";
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        authCredentials(credential);
    }

    private void handleGoogleAuth(GoogleSignInAccount account) {
        progressBarGoogle.setVisibility(View.VISIBLE);
        registerProvider = "external";
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        authCredentials(authCredential);
    }

    private void authCredentials(AuthCredential credential) {
        Log.d(TAG, "linkWithCredential: credential" + credential);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential: SUCCESS" + credential);
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            Log.w("myLog", "linkWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                           disableProgressBars();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void clearInstances() {
        SettingsFirebase.instance = null;
        SettingsRepository.instance = null;
        TagsFirebase.instance = null;
        TagsRepository.instance = null;
        PopularTagsFirebase.instance = null;
        PopularTagsRepository.instance = null;
        MatchesRepository.instance = null;
        MatchesFirebase.instance = null;
        ProfileFirebase.instance = null;
        ProfileRepository.instance = null;
        MainFirebase.instance = null;
        MainRepository.instance = null;
    }
    public void disableProgressBars(){
        progressBarPhone.setVisibility(View.GONE);
        progressBarEmail.setVisibility(View.GONE);
        progressBarFacebook.setVisibility(View.GONE);
        progressBarGoogle.setVisibility(View.GONE);
    }
}
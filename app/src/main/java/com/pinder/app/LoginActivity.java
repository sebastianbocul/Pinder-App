package com.pinder.app;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
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

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.pinder.app.util.ExpandCollapseView.collapse;
import static com.pinder.app.util.ExpandCollapseView.decideExpandOrCollapse;
import static com.pinder.app.util.ExpandCollapseView.expand;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL = "email";
    private static final String TAG = "LoginActivity";
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    private Button mLogin;
    private int RC_SIGN_IN = 0;
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private TextView regulationsTextView, registerTextView;
    private LinearLayout myLayout, logoLayout;
    private Button continueFacebook,continueGoogle;
    private ViewGroup continueEmailLayout,continuePhoneLayout,phoneVerificationLayout;
    private Button continueEmail,continuePhone;
    private Button sendPhoneAuth, phoneVerificationButton;
    private EditText phoneEditText, phoneVerificationEditText;
    private String phoneAuthCode;
    private boolean show = false;
    ImageView image;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setObjectsById();
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        callbackManager = CallbackManager.Factory.create();
        setOnClickListeners();
        clearInstances();
        paintLogo();
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        setRegulationsClickable();
        setRegisterClickable();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            myLayout.setVisibility(View.VISIBLE);
            logoLayout.setVisibility(View.INVISIBLE);
        } else {
            logoLayout.setVisibility(View.VISIBLE);
            myLayout.setVisibility(View.INVISIBLE);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
            anim.setRepeatCount(Animation.INFINITE);
            image.startAnimation(anim);
        }
        authStateListener();
    }

    private void setObjectsById() {
        myLayout = findViewById(R.id.mainLayout);
        logoLayout = findViewById(R.id.logoLayout);
        continueFacebook = findViewById(R.id.continue_facebook);
        mLogin = findViewById(R.id.login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        image = findViewById(R.id.bigLogo);
        continueEmail = findViewById(R.id.continue_email);
        continueGoogle = findViewById(R.id.continue_google);
        registerTextView = findViewById(R.id.registerTextView);
        regulationsTextView = findViewById(R.id.regulationsTextView);
        continueEmailLayout = findViewById(R.id.continue_email_layout);
        continuePhoneLayout = findViewById(R.id.continue_with_phone_layout);
        continuePhone = findViewById(R.id.continue_with_phone);
        sendPhoneAuth = findViewById(R.id.send_phone_auth);
        phoneEditText = findViewById(R.id.phone_edit_text);
        phoneVerificationEditText = findViewById(R.id.phone_verification_edit_text);
        phoneVerificationButton = findViewById(R.id.phone_verification_button);
        phoneVerificationLayout = findViewById(R.id.phone_verification_layout);
    }

    private void setOnClickListeners() {
        continueEmail.setOnClickListener(v -> {
            decideExpandOrCollapse(continueEmailLayout);
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInEmailPassword();
            }
        });
        continueGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        continueFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,Arrays.asList(EMAIL));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });
            }
        });
        continuePhone.setOnClickListener(v->{
            decideExpandOrCollapse(continuePhoneLayout);
        });
        sendPhoneAuth.setOnClickListener(v -> {
            String phoneNumber = phoneEditText.getText().toString().trim();
            if(phoneNumber.length()==0){
                return;
            }
            ExpandCollapseView.expand(phoneVerificationLayout);
            Log.d("PhoneAuth", "onButtonClicked");
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });
        phoneVerificationButton.setOnClickListener(v -> {
            verifyCode(phoneVerificationEditText.getText().toString().trim());
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("PhoneAuth", "onVerificationCompleted:" + credential);
            authCredentials(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("PhoneAuth", "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verificationId,token);

            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            phoneAuthCode = verificationId;
            Log.d("PhoneAuth", "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
//            mVerificationId = verificationId;
//            mResendToken = token;

            // ...
        }
    };

    private void verifyCode(String userVerificationId){
        if(userVerificationId==null || userVerificationId.length()==0){
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneAuthCode,userVerificationId);
        authCredentials(credential);
    }


    private void setRegisterClickable() {
        String text = registerTextView.getText().toString();
        SpannableString ss = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
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
                rg.show(getSupportFragmentManager(), "Terms Dialog");
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                PrivacyDialog pd = new PrivacyDialog();
                pd.show(getSupportFragmentManager(), "Privacy Dialog");
            }
        };
        ss.setSpan(clickableSpan1, 29, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 39, 53, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        regulationsTextView.setText(ss);
        regulationsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void logInEmailPassword() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Wrong email", Toast.LENGTH_SHORT).show();
            mEmail.setError("Enter email!");
        }
        if (password.isEmpty()) {
            mPassword.setError("Enter password!");
            Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
        }
        if (!email.isEmpty() && !password.isEmpty()) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
            authCredentials(credential);
        }
    }

    private void handleFacebookToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        authCredentials(credential);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(account);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("myLog", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        authCredentials(authCredential);
    }

    private void authCredentials(AuthCredential credential) {
        Log.d(TAG, "linkWithCredential: credential" + credential);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            Log.w("myLog", "linkWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                        }
                    }
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
                            if (logoLayout.getVisibility() == View.VISIBLE) {
                                Animation animscale = AnimationUtils.loadAnimation(getApplication(), R.anim.scale);
                                logoLayout.startAnimation(animscale);
                            }
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    logoLayout.setVisibility(View.GONE);
                                    logoLayout.clearAnimation();
                                    image.clearAnimation();
                                    if (dataSnapshot.child("Users").child(user.getUid()).exists()) {
                                        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                            Intent intent = new Intent(LoginActivity.this, MainFragmentManager.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        } else {
                                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                                        }
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, FillInfoActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {
                case 44:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(LoginActivity.this, MainFragmentManager.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "You need to accept permission!", Toast.LENGTH_SHORT).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkLocationPermission();
                            }
                        }, 2000);
                        return;
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Opps something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
    public void paintLogo(){
        TextView logoTextView = findViewById(R.id.logo_text_view);
        TextPaint paint = logoTextView.getPaint();
        float width = paint.measureText("Pinder");
        Shader textShader = new LinearGradient(0, 0, width, logoTextView.getTextSize(),
                new int[]{
                        Color.parseColor("#E0232B"),
                        Color.parseColor("#F4427E"),
                        Color.parseColor("#713471"),
                        Color.parseColor("#213487"),
                        Color.parseColor("#2299F8"),
                        Color.parseColor("#20B89C"),
                        Color.parseColor("#54C634"),
                        Color.parseColor("#FFC107"),
                }, null, Shader.TileMode.CLAMP);
        logoTextView.getPaint().setShader(textShader);
        logoTextView.setTextColor( Color.parseColor("#E0232B"));
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
}

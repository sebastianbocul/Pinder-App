package com.pinder.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.adapters.ChatAdapter;
import com.pinder.app.models.ChatObject;
import com.pinder.app.ui.dialogs.SharedPreferencesHelper;
import com.pinder.app.util.SendFirebaseNotification;
import com.pinder.app.utils.BuildVariantsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatActivity extends AppCompatActivity {
    DatabaseReference mDatabaseUserChat, mDatabaseChat, mDatabaseUser;
    boolean notify = false;
    private RecyclerView myRecyclerView;
    private ChatAdapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private EditText mSendEditText;
    private Button mSendButton;
    private ImageView profileImage;
    private TextView userNameTextView;
    private String currentUserID;
    private String matchId;
    private String chatId;
    private String profileImageUrl, myProfileImageUrl;
    private String myName;
    private String fromActivity = "";
    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();
    private ImageView backArrowImage;
    private static final String TAG = "ChatActivity";
    private boolean chatRoomExists = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        profileImage = findViewById(R.id.profileImage);
        userNameTextView = findViewById(R.id.userName);
        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.button_send);
        matchId = getIntent().getExtras().getString("matchId");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseUserChat = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        fillImagesAndName();
        if (getIntent().getExtras().getString("fromActivity") != null) {
            fromActivity = getIntent().getExtras().getString("fromActivity");
        }
        setRecyclerViewAndAdapter();
        handleBackArrow();
        userNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUsersProfile();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUsersProfile();
            }
        });
        //this functions helps fix recyclerView while opening keyboards
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                notify = true;
            }
        });
    }

    private void setRecyclerViewAndAdapter() {
        myRecyclerView = findViewById(R.id.recyclerView);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        myRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        myRecyclerView.setAdapter(mChatAdapter);
        myRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    myRecyclerView.scrollBy(0, oldBottom - bottom);
                }
            }
        });
        mChatAdapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onProfileClick(int position) {

            }

            @Override
            public void onMessageClick(int position) {
            }
        });
    }

    private void goToUsersProfile() {
        Intent intent = new Intent(ChatActivity.this, UsersProfilesActivity.class);
        intent.putExtra("userId", matchId);
        intent.putExtra("fromActivity", "ChatActivity");
        startActivity(intent);
    }

    private void fillImagesAndName() {
        String matchName = "";
        String matchImageUrl = "default";
        SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        myProfileImageUrl = SharedPreferencesHelper.getCurrentProfilePicture(this);
        if (getIntent().getExtras() != null) {
            matchName = getIntent().getExtras().getString("matchName");
            matchImageUrl = getIntent().getExtras().getString("matchImageUrl");
        } else myProfileImageUrl = "default";
        profileImageUrl = matchImageUrl;
        userNameTextView.setText(matchName);
        switch (matchImageUrl) {
            case "default":
                Glide.with(getApplication()).load(R.drawable.ic_profile_hq).into(profileImage);
                break;
            default:
                Glide.with(profileImage).clear(profileImage);
                Glide.with(getApplication()).load(matchImageUrl).into(profileImage);
                break;
        }
        getChatId();
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString().trim();
        if (!sendMessageText.isEmpty()) {
            if (chatRoomExists) {
                DatabaseReference newMessageDb = mDatabaseChat.push();
                Map newMessage = new HashMap<>();
                newMessage.put("createdByUser", currentUserID);
                newMessage.put("text", sendMessageText);
                newMessageDb.setValue(newMessage);
                String msg = sendMessageText;
                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myName = dataSnapshot.child("name").getValue().toString();
                        if (notify) {
                            SendFirebaseNotification.sendNotification(matchId, currentUserID, myProfileImageUrl, myName, getApplicationContext().getString(R.string.notification_body_message));
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            } else {
                Toast.makeText(this, "Looks like you are not a match anymore :(", Toast.LENGTH_SHORT).show();
            }
        }
        mSendEditText.setText(null);
    }

    private void getChatId() {
        mDatabaseUserChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.toString() + "    s: " + s);
                if (dataSnapshot.exists()) {
                    String message = null;
                    String createdByUser = null;
                    if (dataSnapshot.child("text").getValue() != null) {
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("createdByUser").getValue() != null) {
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if (message != null && createdByUser != null) {
                        Boolean currentUserBoolean = false;
                        String imageUrl = profileImageUrl;
                        if (createdByUser.equals(currentUserID)) {
                            currentUserBoolean = true;
                            imageUrl = myProfileImageUrl;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean, imageUrl);
                        resultChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
                myRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged: " + dataSnapshot.toString() + "    s: " + s);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                mDatabaseChat.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "Checker :");
                        if (snapshot.exists()) {
                            Log.d(TAG, "Checker onDataChange: " + dataSnapshot.toString());
                        } else {
                            chatRoomExists = false;
                            Log.d(TAG, "Checker onDataChange: data not exists");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                Log.d(TAG, "onChildRemoved: " + dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildMoved: " + dataSnapshot.toString() + "    s: " + s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }

    private List<ChatObject> getDataSetChat() {
        return resultChat;
    }

    @Override
    public void onBackPressed() {
        if (fromActivity.equals("notification")) {
            Intent intent = new Intent(this, MainFragmentManager.class);
            intent.putExtra("fromActivity", "chatActivity");
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void handleBackArrow() {
        backArrowImage = findViewById(R.id.back_arrow);
        BuildVariantsHelper.disableButton(backArrowImage);
        backArrowImage.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}

package com.pinder.app.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pinder.app.MainFragmentMenager;
import com.pinder.app.notifications.APIService;
import com.pinder.app.notifications.Client;
import com.pinder.app.notifications.Data;
import com.pinder.app.notifications.Sender;
import com.pinder.app.notifications.Token;
import com.pinder.app.R;
import com.pinder.app.UsersProfilesActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    DatabaseReference mDatabaseUserChat, mDatabaseChat, mDatabaseUser;
    APIService apiService;
    boolean notify = false;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        profileImage = findViewById(R.id.profileImage);
        userNameTextView = findViewById(R.id.userName);
        matchId = getIntent().getExtras().getString("matchId");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseUserChat = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        fillImagesAndName();
        if (getIntent().getExtras().getString("fromActivity") != null) {
            fromActivity = getIntent().getExtras().getString("fromActivity");
        }
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //create APISERVICE
        Client client = new Client();
        apiService = client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        myRecyclerView = findViewById(R.id.recyclerView);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        myRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        myRecyclerView.setAdapter(mChatAdapter);
        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);
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
        myRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    myRecyclerView.scrollBy(0, oldBottom - bottom);
                }
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                notify = true;
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
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String name = dataSnapshot.child(matchId).child("name").getValue().toString();
                    if (!dataSnapshot.child(matchId).child("profileImageUrl").exists()) {
                        profileImageUrl = "default";
                    } else
                        profileImageUrl = dataSnapshot.child(matchId).child("profileImageUrl").getValue().toString().trim();
                    myProfileImageUrl = dataSnapshot.child(currentUserID).child("profileImageUrl").getValue().toString().trim();
                    if (!name.isEmpty()) {
                        userNameTextView.setText(name);
                    }
                    userNameTextView.setText(name);
                    switch (profileImageUrl) {
                        case "default":
                            Glide.with(getApplication()).load(R.drawable.ic_profile_hq).into(profileImage);
                            break;
                        default:
                            Glide.with(profileImage).clear(profileImage);
                            Glide.with(getApplication()).load(profileImageUrl).into(profileImage);
                            break;
                    }
                    getChatId();
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, "Oooops something went wrong ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChatActivity.this, MainFragmentMenager.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString().trim();
        if (!sendMessageText.isEmpty()) {
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
                        sendNotification(matchId, myName, sendMessageText);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        mSendEditText.setText(null);
    }

    private void sendNotification(String matchId, String myName, String sendMessageText) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(matchId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(currentUserID, R.drawable.login_photo, sendMessageText, myName, matchId);
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
                        for (ChatObject card : resultChat) {
                        }
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
                myRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private List<ChatObject> getDataSetChat() {
        return resultChat;
    }
}
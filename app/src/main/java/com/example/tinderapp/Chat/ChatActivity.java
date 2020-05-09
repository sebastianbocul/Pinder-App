package com.example.tinderapp.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tinderapp.MainActivity;
import com.example.tinderapp.Matches.MatchesActivity;
import com.example.tinderapp.Matches.MatchesAdapter;
import com.example.tinderapp.Matches.MatchesObject;
import com.example.tinderapp.R;
import com.example.tinderapp.UsersProfilesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;
    private Button mSendButton,backButton;
    private ImageView profileImage;
    private TextView userNameTextView;
    private String currentUserID;
    private String matchId;
    private String chatId;
    private String profileImageUrl,myProfileImageUrl;
    DatabaseReference mDatabaseUserChat,mDatabaseChat,mDatabaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

       // backButton = (Button) findViewById(R.id.backButton);

        profileImage = (ImageView) findViewById(R.id.profileImage);
        userNameTextView = (TextView) findViewById(R.id.userName);
        matchId = getIntent().getExtras().getString("matchId");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseUserChat= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat= FirebaseDatabase.getInstance().getReference().child("Chat");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        getChatId();
        fillImagesAndName();



        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //myRecyclerView.setNestedScrollingEnabled(true);
       // myRecyclerView.setHasFixedSize(false);
        mChatLayoutManager= new LinearLayoutManager(ChatActivity.this);
        myRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(),ChatActivity.this);
        myRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = (EditText) findViewById(R.id.message);
        mSendButton = (Button) findViewById(R.id.send);



        userNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUsersProfile();
            }
        });
        //on profileimage click
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUsersProfile();
            }
        });
        //on back button click
/*        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,MatchesActivity.class);
                startActivity(intent);
            }
        });*/
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
            }
        });


    }

    private void goToUsersProfile() {
        Intent intent = new Intent(ChatActivity.this, UsersProfilesActivity.class);
        intent.putExtra("userId", matchId);
        intent.putExtra("fromActivity","ChatActivity");
        startActivity(intent);
    }

    private void fillImagesAndName() {
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child(matchId).child("name").getValue().toString();
                profileImageUrl = dataSnapshot.child(matchId).child("profileImageUrl").getValue().toString().trim();
                myProfileImageUrl = dataSnapshot.child(currentUserID).child("profileImageUrl").getValue().toString().trim();

                if(!name.isEmpty()){
                    userNameTextView.setText(name);
                }
                userNameTextView.setText(name);
                switch(profileImageUrl){
                    case "default":
                        Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(profileImage);
                        break;
                    default:
                        Glide.with(profileImage).clear(profileImage);
                        Glide.with(getApplication()).load(profileImageUrl).into(profileImage);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();
            Map newMessage = new HashMap<>();
            newMessage.put("createdByUser",currentUserID);
            newMessage.put("text",sendMessageText);
            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void getChatId(){
        mDatabaseUserChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
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
                if(dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;


                    if(dataSnapshot.child("text").getValue()!=null){
                        message=dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser=dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(message!=null && createdByUser!=null){
                        Boolean currentUserBoolean = false;
                        String imageUrl=profileImageUrl;
                        if(createdByUser.equals(currentUserID)){
                            currentUserBoolean = true;
                            imageUrl=myProfileImageUrl;
                        }
                        ChatObject newMessage = new ChatObject(message,currentUserBoolean,imageUrl);
                        resultChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
                myRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
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


    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return resultChat;
    }
}

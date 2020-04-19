package com.example.tinderapp.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.tinderapp.Matches.MatchesActivity;
import com.example.tinderapp.Matches.MatchesAdapter;
import com.example.tinderapp.Matches.MatchesObject;
import com.example.tinderapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        myRecyclerView.setNestedScrollingEnabled(false);
        myRecyclerView.setHasFixedSize(true);
        mChatLayoutManager= new LinearLayoutManager(ChatActivity.this);
        myRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(),ChatActivity.this);
        myRecyclerView.setAdapter(mChatAdapter);


    }


    private ArrayList<ChatObject> resultChat = new ArrayList<ChatObject>();
    private List<ChatObject> getDataSetChat() {
        return resultChat;
    }
}

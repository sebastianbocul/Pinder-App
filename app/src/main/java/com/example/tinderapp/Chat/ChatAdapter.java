package com.example.tinderapp.Chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinderapp.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private List<ChatObject> chatList;
    private Context context;

    private float scale;

    private int dpAsPixels;
    private int dpAsPixels2;
    private int dpAsPixels3;

    private LinearLayout.LayoutParams paramsText;
    private LinearLayout.LayoutParams paramsContainer,paramsContainer2;


    public ChatAdapter(List<ChatObject>matchesList, Context context){
        this.chatList=matchesList;
        this.context = context;
    }
    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders(layoutView);


        paramsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsContainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsContainer2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
        holder.mMessaage.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser()==true){
            paramsContainer2.setMargins(100,30,20,0);
            paramsText.setMargins(0,0,10,0);
            holder.mMessaage.setGravity(Gravity.RIGHT);
            holder.mContainer.setGravity(Gravity.RIGHT);
            holder.mMessaage.setTextColor(Color.parseColor("#404040"));
            holder.mContainer.setLayoutParams(paramsContainer2);
            holder.mMessaage.setLayoutParams(paramsText);
            holder.mContainer.setBackgroundResource(R.drawable.my_chat);
            if(chatList.get(position).getProfileImageUrl().equals("default")){
                Glide.with(context).load(R.drawable.picture_default).into(holder.mChatImage);
            }else {
                Glide.with(context).load(chatList.get(position).getProfileImageUrl()).into(holder.mChatImage);
            }
        }
        else {
            paramsContainer.setMargins(20,30,100,0);
            paramsText.setMargins(10,0,0,0);
            holder.mMessaage.setGravity(Gravity.LEFT);
            holder.mContainer.setGravity(Gravity.LEFT);
            holder.mMessaage.setTextColor(Color.parseColor("#404040"));
            holder.mContainer.setLayoutParams(paramsContainer);
            holder.mMessaage.setLayoutParams(paramsText);
            holder.mContainer.setBackgroundResource(R.drawable.other_chat);

            if(chatList.get(position).getProfileImageUrl().equals("default")){
                Glide.with(context).load(R.drawable.picture_default).into(holder.mChatImage);
            }else {
                Glide.with(context).load(chatList.get(position).getProfileImageUrl()).into(holder.mChatImage);
            }

        }
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}

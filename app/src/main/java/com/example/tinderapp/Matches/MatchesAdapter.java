package com.example.tinderapp.Matches;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinderapp.R;
import android.view.View;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {
    private List<MatchesObject> matchesList;
    private Context context;

    public MatchesAdapter(List<MatchesObject>matchesList, Context context){
        this.matchesList=matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {

         holder.mMatchUserId = matchesList.get(position).getUserId();
        if(matchesList.get(position).isCreatedByMe()==true){
            holder.mLastMessage.setTypeface(null, Typeface.NORMAL);
            holder.mMatchName.setTypeface(null, Typeface.NORMAL);
            holder.mMatchImage.setBorderColor(Color.parseColor("#00000000"));
            holder.mLastMessage.setTextColor(Color.parseColor("#404040"));
        }
        else {
            holder.mLastMessage.setTypeface(null, Typeface.BOLD);
            holder.mMatchName.setTypeface(null, Typeface.BOLD);
            holder.mMatchImage.setBorderColor(Color.parseColor("#D3D3D3"));
            holder.mLastMessage.setTextColor(Color.parseColor("#404040"));
        }
        holder.mMatchName.setText(matchesList.get(position).getName());
        holder.mLastMessage.setText(matchesList.get(position).getLastMessage());

        if(matchesList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(R.drawable.picture_default).into(holder.mMatchImage);
        }else {
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).into(holder.mMatchImage);
        }


    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}

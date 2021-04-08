package com.pinder.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pinder.app.R;
import com.pinder.app.models.MatchesObject;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchesViewHolders> {
    private final List<MatchesObject> matchesList;
    private final Context context;

    public MatchesAdapter(List<MatchesObject> matchesList, Context context) {
        this.matchesList = matchesList;
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
        holder.bundleMatchUserId = matchesList.get(position).getUserId();
        holder.bundleMatchName = matchesList.get(position).getName();
        holder.bundleMatchImageUrl = matchesList.get(position).getProfileImageUrl();
        if (matchesList.get(position).isCreatedByMe() == true) {
            holder.mLastMessage.setTypeface(null, Typeface.NORMAL);
            holder.mMatchName.setTypeface(null, Typeface.NORMAL);
            holder.mMatchImage.setBorderColor(Color.parseColor("#00000000"));
            holder.mLastMessage.setTextColor(Color.parseColor("#404040"));
        } else {
            holder.mLastMessage.setTypeface(null, Typeface.BOLD);
            holder.mMatchName.setTypeface(null, Typeface.BOLD);
            holder.mMatchImage.setBorderColor(Color.parseColor("#D3D3D3"));
            holder.mLastMessage.setTextColor(Color.parseColor("#404040"));
        }
        holder.mMatchName.setText(matchesList.get(position).getName());
        holder.mLastMessage.setText(matchesList.get(position).getLastMessage());
        if (matchesList.get(position).getProfileImageUrl().equals("default")) {
            Glide.with(context).load(R.drawable.ic_profile_hq).into(holder.mMatchImage);
        } else {
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).into(holder.mMatchImage);
        }
    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }

    public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mMatchName, mLastMessage;
        public CircularImageView mMatchImage;
        public String bundleMatchUserId;
        public String bundleMatchName;
        public String bundleMatchImageUrl;

        public MatchesViewHolders(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mLastMessage = itemView.findViewById(R.id.lastMessage);
            mMatchName = itemView.findViewById(R.id.MatchName);
            mMatchImage = itemView.findViewById(R.id.MatchImage);
        }

        @Override
        public void onClick(View view) {
            NavController navController = Navigation.findNavController(view);
            if (bundleMatchUserId != null) {
                Bundle bundle = new Bundle();
                bundle.putString("matchId", bundleMatchUserId);
                bundle.putString("matchName", bundleMatchName);
                bundle.putString("matchImageUrl", bundleMatchImageUrl);
                bundle.putString("fromActivity", "MatchesFragment");
                navController.navigate(R.id.action_mainFragmentManager_to_chatFragment, bundle);
            }
        }
    }
}

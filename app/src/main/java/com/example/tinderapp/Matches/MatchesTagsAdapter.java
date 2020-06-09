package com.example.tinderapp.Matches;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinderapp.R;

import java.util.List;

public class MatchesTagsAdapter extends RecyclerView.Adapter<MatchesTagsAdapter.ViewHolder> {
    private List<String> mData;
    private LayoutInflater mInflater;
    private MatchesTagsAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    public MatchesTagsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public MatchesTagsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_matches_tags, parent, false);
        return new MatchesTagsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesTagsAdapter.ViewHolder holder, int position) {
        String tag = mData.get(position);
        holder.myTagButton.setText(tag);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(MatchesTagsAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button myTagButton;
        TextView sortByTextView;
        String sortBy;

        ViewHolder(View itemView) {
            super(itemView);
            myTagButton = itemView.findViewById(R.id.itemTagButton);
            sortByTextView = itemView.findViewById(R.id.sortByText);
            myTagButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}


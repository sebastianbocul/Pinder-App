package com.pinder.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pinder.app.R;
import com.pinder.app.models.Card;

import java.util.List;

public class CardsAdapter extends ArrayAdapter<Card> {
    Context context;
    public CardsAdapter(Context context, int resourceId, List<Card> items) {
        super(context, resourceId, items);
        this.context=context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Card card_item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        TextView tags = convertView.findViewById(R.id.tags);
        ImageView image = convertView.findViewById(R.id.image);
        name.setText(card_item.getName());
        tags.setText(card_item.getTags());
        switch (card_item.getProfileImageUrl()) {
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.ic_profile_hq).into(image);
                break;
            default:
                Glide.with(image).clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }
        return convertView;
    }
}

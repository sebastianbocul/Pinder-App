package com.pinder.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pinder.app.R;
import com.pinder.app.models.Card;

import java.util.List;

public class CardsAdapter extends ArrayAdapter<Card> {
    Context context;

    public CardsAdapter(Context context, int resourceId, List<Card> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Card card_item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        TextView tags = convertView.findViewById(R.id.tags);
        ImageView image = convertView.findViewById(R.id.image);
        ImageView likesMe = convertView.findViewById(R.id.star_icon);
        name.setText(card_item.getName());
        tags.setText(card_item.getTags());
        if (card_item.isLikesMe()) {
            likesMe.setVisibility(View.VISIBLE);
            likesMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (card_item.getGender().equals("Male")) {
                        Toast.makeText(context, "He likes you!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "She likes you!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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

package com.pinder.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.pinder.app.R;

import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private final ArrayList mImages;

    public ImageAdapter(Context context, ArrayList imagesUrls) {
        mContext = context;
        mImages = imagesUrls;
    }

    public ImageAdapter(ArrayList imagesUrls) {
        mImages = imagesUrls;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Integer i = position;
        String imageUrl = mImages.get(i).toString();
        if (mImages.get(i).equals("default")) {
            Glide.with(mContext).load(R.drawable.ic_profile_hq).into(imageView);
        } else {
            Glide.with(mContext).load(imageUrl).into(imageView);
        }
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}

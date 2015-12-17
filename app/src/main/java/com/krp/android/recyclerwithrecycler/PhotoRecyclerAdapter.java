package com.krp.android.recyclerwithrecycler;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by purushottam.kumar on 12/15/2015.
 */
public class PhotoRecyclerAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private List<Photo> photos;

    public PhotoRecyclerAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_images, parent, false);
        return PhotoViewHolder.getInstance(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.setCaption("Caption " + (position + 1));
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }

    public void notifyDataSetChanged(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }
}

class Photo {
    String caption = "Caption";
}

class PhotoViewHolder extends RecyclerView.ViewHolder {

    private View mRootView;
    private TextView tvCaption;

    public PhotoViewHolder(View rootView, TextView captionView) {
        super(rootView);
        mRootView = rootView;
        tvCaption = captionView;
    }

    public static PhotoViewHolder getInstance(View rootView) {
        return new PhotoViewHolder(rootView,
                (TextView) rootView.findViewById(R.id.caption));
    }

    public void setCaption(String caption) {
        tvCaption.setText(caption == null ? "Caption" : caption);
    }
}

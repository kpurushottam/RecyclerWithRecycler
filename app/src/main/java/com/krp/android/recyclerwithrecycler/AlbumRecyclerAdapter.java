package com.krp.android.recyclerwithrecycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krp.android.recyclerwithrecycler.utils.ExpandableGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by purushottam.kumar on 12/15/2015.
 */
public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    public static final int DEFAULT_SPAN_COUNT = 4;
    public static final int DEFAULT_SIZE = 6;

    private int newSize;
    private List<Album> mListAlbums;

    public AlbumRecyclerAdapter() {
        mListAlbums = new ArrayList<>(DEFAULT_SIZE);
        for(int i=0; i<DEFAULT_SIZE; i++) {
            mListAlbums.add(new Album());
        }
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_album, parent, false);
        return AlbumViewHolder.getInstance(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        holder.setHeading("Heading " + (position + 1));
        holder.notifyDataSetChanged(newSize);
    }

    @Override
    public int getItemCount() {
        return mListAlbums == null ? 0 : mListAlbums.size();
    }

    public void notifyDataSetChanged(int newSize) {
        this.newSize = newSize;
        notifyDataSetChanged();
    }
}

class Album {
    String heading = "Heading";
}

class AlbumViewHolder extends RecyclerView.ViewHolder {

    private View mRootView;
    private TextView tvHeading;

    private List<Photo> mListPhotos;
    private RecyclerView mRecyclerView;
    private PhotoRecyclerAdapter mPhotosAdapter;

    public AlbumViewHolder(View rootView, TextView headingView, RecyclerView recyclerView) {
        super(rootView);
        mRootView = rootView;
        tvHeading = headingView;

        mRecyclerView = recyclerView;
        mRecyclerView.setLayoutManager(new ExpandableGridLayoutManager(
                mRootView.getContext(), AlbumRecyclerAdapter.DEFAULT_SPAN_COUNT));
        mPhotosAdapter = new PhotoRecyclerAdapter(getPhotos(AlbumRecyclerAdapter.DEFAULT_SIZE));
        mRecyclerView.setAdapter(mPhotosAdapter);
    }

    public static AlbumViewHolder getInstance(View rootView) {
        return new AlbumViewHolder(rootView,
                (TextView) rootView.findViewById(R.id.heading),
                (RecyclerView) rootView.findViewById(R.id.recycler));
    }

    public void setHeading(String heading) {
        tvHeading.setText(heading == null ? "Heading" : heading);
    }

    public void notifyDataSetChanged(int defaultSize) {
        mPhotosAdapter.notifyDataSetChanged(getPhotos(defaultSize));
    }

    private List<Photo> getPhotos(int size) {
        if(mListPhotos == null) {
            mListPhotos = new ArrayList<>(size);
        }
        mListPhotos.removeAll(mListPhotos);
        for(int i=0; i<size; i++) {
            mListPhotos.add(new Photo());
        }
        return mListPhotos;
    }
}


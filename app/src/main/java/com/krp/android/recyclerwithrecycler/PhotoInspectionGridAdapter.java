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
public class PhotoInspectionGridAdapter extends RecyclerView.Adapter {

    public static final String TAG = PhotoInspectionGridAdapter.class.getSimpleName();
    private Context context;
    private List<Photo> photos;
    private OnPhotoInspectionGridItemClickListener itemClickListener;

    public PhotoInspectionGridAdapter(Context context, List<Photo> photos,
                                      OnPhotoInspectionGridItemClickListener itemClickListener) {
        this.context = context;
        this.photos = photos;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inspection_photo_grid, parent, false);
        return InspectionPhotosGridItemHolder.getInstance(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPhotoInspectionGridItemClick(viewHolder, photos, position);
            }
        });

        Photo photo = photos.get(position);

        InspectionPhotosGridItemHolder holder = (InspectionPhotosGridItemHolder) viewHolder;
        holder.setPhotoImageUri(new File(photo.getURL()).toString());
        holder.setStatusImageResource(getSyncImage(photo));
        holder.setCaption(photo.getCaption());
    }

    @Override
    public int getItemCount() {
        return photos != null ? photos.size() : 0;
    }

    public interface OnPhotoInspectionGridItemClickListener {
        void onPhotoInspectionGridItemClick(RecyclerView.ViewHolder viewHolder, List<Photo> photos, int position);
    }

    private int getSyncImage(Photo photo) {
        if (photo.getState() == Photo.State.UPLOADED.getValue()) {
            return android.R.drawable.ic_menu_call;
        } else if (photo.getState() == Photo.State.UPLOADING.getValue()) {
            return android.R.drawable.ic_input_add;
        }
        return android.R.drawable.ic_menu_search;
    }

    public void updatePhotoList(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }
}

class Photo {

    private String URL = "url";
    private CharSequence caption ="caption";
    private Object state;

    public String getURL() {
        return URL;
    }

    public CharSequence getCaption() {
        return caption;
    }

    public Object getState() {
        return state;
    }

    public class State {
        public class UPLOADED {
            public static Object getValue() {
                return "value";
            }
        }

        public class UPLOADING {
            public static Object getValue() {
                return "value";
            }
        }
    }
}

class InspectionPhotosGridItemHolder extends RecyclerView.ViewHolder {

    private View parent;
    private ImageView ivPhoto;
    private TextView tvCaption;
    private ImageView ivStatus;

    public InspectionPhotosGridItemHolder(View parent, ImageView ivPhoto,
                                          TextView tvCaption, ImageView ivStatus) {
        super(parent);
        this.parent = parent;
        this.ivPhoto = ivPhoto;
        this.tvCaption = tvCaption;
        this.ivStatus = ivStatus;
    }

    public static InspectionPhotosGridItemHolder getInstance(View parent) {
        return new InspectionPhotosGridItemHolder(parent,
                (ImageView) parent.findViewById(R.id.photo),
                (TextView) parent.findViewById(R.id.caption),
                (ImageView) parent.findViewById(R.id.status));
    }

    public View getParent() {
        return parent;
    }

    public void setPhotoImageUri(String imageUri) {
        ivPhoto.setImageURI(Uri.parse(imageUri));
    }

    public void setCaption(CharSequence caption) {
        tvCaption.setText(caption);
    }

    public void setStatusImageResource(int resId) {
        ivStatus.setImageResource(resId);
    }
}

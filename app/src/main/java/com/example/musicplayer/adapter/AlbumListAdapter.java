package com.example.musicplayer.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Album;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder> {

    ArrayList<Album> albumList;
    Context context;

    public void setAlbum(ArrayList<Album> albumList)
    {
        this.albumList = albumList;
    }

    public AlbumListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item,parent,false);
        return new AlbumViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.albumTitle.setText(albumList.get(position).getTitle().trim());
        Bitmap bm = null;
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumList.get(position).getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                bm = context.getContentResolver().loadThumbnail(trackUri, new Size(512, 512), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide.with(context).load(bm).into(holder.imageView);
        }
        else
            Glide.with(context)
                .load(albumList.get(position).getAlbumartPath())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder{

        TextView albumTitle;
        ImageView imageView;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumTitle = itemView.findViewById(R.id.album_name);
            imageView = itemView.findViewById(R.id.album_cover);
        }
    }
}

package com.example.musicplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;

import java.io.IOException;
import java.util.ArrayList;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder>{

    ArrayList<Artist> artistList;
    Context context;

    public void setArtist(ArrayList<Artist> artistList)
    {
        this.artistList = artistList;
    }

    public ArtistListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ArtistListAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item,parent,false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistListAdapter.ArtistViewHolder holder, int position) {
        holder.artistTitle.setText(artistList.get(position).getArtist().trim());
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        TextView artistTitle;
        ImageView imageView;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistTitle = itemView.findViewById(R.id.album_name);
            imageView = itemView.findViewById(R.id.album_cover);
        }
    }
}

package com.example.musicplayer.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.LoadBitmap;
import com.example.musicplayer.utils.LoadPalette;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder>{

    ArrayList<Song> songList;
    Context context;

    public SongListAdapter(Context context) {
        this.context = context;
        songList = new ArrayList<Song>();
    }

    public void setSongList(ArrayList<Song> songList)
    {
        this.songList = songList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item,parent,false);
        return new SongViewHolder(view,context,songList);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);

        holder.albumArt.setImageDrawable (null);
        holder.loadBitmap(song, holder.albumArt);

        String songTitle = (song.getTitle()!=null)?song.getTitle():"NULL";
        holder.title.setText(songTitle);
        holder.artist.setText(song.getArtist());
        holder.duration.setText(song.getDuration());
        //new LoadPalette(holder.title).execute(song.getPath());
    }

    @Override
    public void onViewRecycled(@NonNull SongViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelLoad();
    }

    @Override
    public int getItemCount() {
        if(songList == null) return 0;
        return songList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder{

        TextView title,artist,duration;
        ImageView albumArt;
        Context context;
        ArrayList<Song> songList;
        LoadBitmap asynctask;

        public SongViewHolder(@NonNull View itemView, Context context,ArrayList<Song> songList) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            duration = (TextView) itemView.findViewById(R.id.duration);
            albumArt = (ImageView) itemView.findViewById(R.id.album_art);
            //this.songList = songList;
            this.context = context;
        }

        public void loadBitmap(Song song,ImageView view)
        {
            asynctask = new LoadBitmap(view,song.getId());
            if(asynctask.getStatus() != AsyncTask.Status.RUNNING && asynctask.getStatus() != AsyncTask.Status.FINISHED)
            {
                asynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,song.getPath());
            }
        }

        public void cancelLoad()
        {
            if(asynctask.getStatus() == AsyncTask.Status.RUNNING)
                asynctask.cancel(true);
        }
    }
}

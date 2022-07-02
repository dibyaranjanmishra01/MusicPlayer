package com.example.musicplayer.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.LoadBitmap;

import org.w3c.dom.Text;

public class SongPagerFragment extends Fragment{

    Song song;
    Context context;

    public SongPagerFragment(Song song, Context context) {
        this.song = song;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_item,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView image = view.findViewById(R.id.pager_albumart);
        LoadBitmap asynctask = new LoadBitmap(image,song.getId());
        if(asynctask.getStatus() != AsyncTask.Status.RUNNING && asynctask.getStatus() != AsyncTask.Status.FINISHED)
        {
            asynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,song.getPath());
        }
    }
}

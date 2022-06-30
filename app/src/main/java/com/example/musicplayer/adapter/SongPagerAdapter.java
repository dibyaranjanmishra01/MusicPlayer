package com.example.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.musicplayer.R;
import com.example.musicplayer.fragment.SongPagerFragment;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.LoadBitmap;

import java.util.ArrayList;

public class SongPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    ArrayList<Song> songList;

    public SongPagerAdapter(@NonNull FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public boolean isSongListEqual(ArrayList<Song> list)
    {
        if(songList == null) return  false;
        return list.equals(songList);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new SongPagerFragment(songList.get(position),context);
    }

    @Override
    public int getCount() {
        return songList.size();
    }

//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        return POSITION_NONE;
//    }
}

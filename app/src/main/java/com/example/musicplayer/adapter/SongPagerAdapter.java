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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicplayer.R;
import com.example.musicplayer.fragment.SongPagerFragment;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.LoadBitmap;

import java.util.ArrayList;

public class SongPagerAdapter extends FragmentStateAdapter {

    Context context;
    ArrayList<Song> songList;

    public SongPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context) {
        super(fragmentActivity);
        this.context = context;
        this.songList = new ArrayList<Song>();
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
        notifyDataSetChanged();
    }

    public boolean isSongListEqual(ArrayList<Song> list)
    {
        if(songList == null) return  false;
        return list.equals(songList);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new SongPagerFragment(songList.get(position),context);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        return POSITION_NONE;
//    }
}

package com.example.musicplayer.adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicplayer.fragment.AlbumListFragment;
import com.example.musicplayer.fragment.ArtistListFragment;
import com.example.musicplayer.fragment.SongListFragment;
import com.example.musicplayer.fragment.SongPagerFragment;
import com.example.musicplayer.model.Song;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class MainPagerAdapter extends FragmentStateAdapter {

    ArrayList<Song> songList;

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        //this.songList = songList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0)
            //return new SongListFragment(songList);
            return new SongListFragment();
        if(position == 1)
            return new AlbumListFragment();
        else
            return new ArtistListFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}

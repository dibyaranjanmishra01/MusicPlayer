package com.example.musicplayer.utils;

import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.model.Song;

import java.util.ArrayList;

public class ViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

    ArrayList<Song> songList;
    public ViewPagerPageChangeListener(ArrayList<Song> songList) {
        this.songList = songList;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

package com.example.musicplayer.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Song;

import java.util.ArrayList;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private SongClickListener songclickListener;
    private AlbumClickListener albumclickListener;
    ArrayList<Song> songList;
    ArrayList<Album> albumList;

    public void setSongList(ArrayList<Song> songList)
    {
        this.songList = songList;
    }

    public void setAlbumList(ArrayList<Album> albumList)
    {
        this.albumList = albumList;
    }

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SongClickListener clickListener) {
        this.songclickListener = clickListener;
        //this.songList = songList;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }
    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AlbumClickListener clickListener) {
        this.albumclickListener = clickListener;
        //this.songList = songList;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && songclickListener != null && gestureDetector.onTouchEvent(e)) {
            songclickListener.onClick(child, rv.getChildAdapterPosition(child),songList.get(rv.getChildAdapterPosition(child)));
        }
        if (child != null && albumclickListener != null && gestureDetector.onTouchEvent(e)) {
            albumclickListener.onClick(child, rv.getChildAdapterPosition(child),albumList.get(rv.getChildAdapterPosition(child)));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface SongClickListener {
        void onClick(View view, int position,Song song);

        void onLongClick(View view, int position);
    }

    public interface AlbumClickListener {
        void onClick(View view, int position, Album album);

        void onLongClick(View view, int position);
    }
}

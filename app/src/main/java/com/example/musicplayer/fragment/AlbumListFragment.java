package com.example.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.AlbumListAdapter;
import com.example.musicplayer.adapter.SongPagerAdapter;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.RecyclerTouchListener;
import com.example.musicplayer.viewModel.SongViewModel;

import java.util.ArrayList;

public class AlbumListFragment extends Fragment implements RecyclerTouchListener.AlbumClickListener {

    SongViewModel songViewModel;
    ViewPager viewPager;

    public AlbumListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.album_list_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songViewModel = new ViewModelProvider(getActivity()).get(SongViewModel.class);
        viewPager = getActivity().findViewById(R.id.song_pager);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_album);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(getContext(),recyclerView,this);
        AlbumListAdapter albumListAdapter = new AlbumListAdapter(getContext());
        songViewModel.getAlbumList().observe(getActivity(), new Observer<ArrayList<Album>>() {
            @Override
            public void onChanged(ArrayList<Album> albumList) {
                albumListAdapter.setAlbum(albumList);
                recyclerView.setAdapter(albumListAdapter);
                recyclerTouchListener.setAlbumList(albumList);
                recyclerView.addOnItemTouchListener(recyclerTouchListener);
            }
        });
    }

    @Override
    public void onClick(View view, int position, Album album) {
//        SongPagerAdapter pagerAdapter = new SongPagerAdapter(getParentFragmentManager(),getContext());
//        pagerAdapter.setSongList(songViewModel.getAlbumSong(album.getTitle()));
//        viewPager.setAdapter(pagerAdapter);
        AlbumSongListFragment fragment = new AlbumSongListFragment(album.getTitle());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view,fragment)
                .setReorderingAllowed(true)
                .addToBackStack("name") // name can be null
                .commit();
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}

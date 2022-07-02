package com.example.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.RecyclerTouchListener;
import com.example.musicplayer.viewModel.SongViewModel;

import java.util.ArrayList;

public class ArtistSongListFragment extends Fragment implements RecyclerTouchListener.SongClickListener{
    SongViewModel songViewModel;
    String val;
    RecyclerView recyclerView;
    SongListAdapter adapter;
    ViewPager2 viewPager;
    ArrayList<Song> artistSongList;

    public ArtistSongListFragment(String v) {
        this.val = v;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.song_list_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        artistSongList = songViewModel.getArtistSong(val);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new SongListAdapter(getContext());
        adapter.setSongList(artistSongList);
        recyclerView.setAdapter(adapter);
        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(getContext(),recyclerView,this);
        recyclerTouchListener.setSongList(artistSongList);
        recyclerView.addOnItemTouchListener(recyclerTouchListener);
        viewPager = getActivity().findViewById(R.id.song_pager);

    }

    @Override
    public void onClick(View view, int position, Song song) {
        songViewModel.setCurrentSongList(artistSongList);
        songViewModel.setCurrentSong(song);
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}

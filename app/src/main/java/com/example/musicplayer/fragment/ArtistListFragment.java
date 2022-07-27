package com.example.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.AlbumListAdapter;
import com.example.musicplayer.adapter.ArtistListAdapter;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.utils.RecyclerTouchListener;
import com.example.musicplayer.viewModel.SongViewModel;

import java.util.ArrayList;

public class ArtistListFragment extends Fragment implements RecyclerTouchListener.ArtistClickListener {

    SongViewModel songViewModel;

    public ArtistListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.album_list_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_album);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(getContext(),recyclerView,this);
        ArtistListAdapter artistListAdapter = new ArtistListAdapter(getContext());
        songViewModel.getArtistList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Artist>>() {
            @Override
            public void onChanged(ArrayList<Artist> artistList) {
                if(artistList!=null) {
                    artistListAdapter.setArtist(artistList);
                    recyclerView.setAdapter(artistListAdapter);
                    recyclerTouchListener.setArtistList(artistList);
                    recyclerView.addOnItemTouchListener(recyclerTouchListener);
                }
            }
        });
    }

    @Override
    public void onClick(View view, int position, Artist artist) {
        ArtistSongListFragment fragment = new ArtistSongListFragment(artist.getArtist());
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

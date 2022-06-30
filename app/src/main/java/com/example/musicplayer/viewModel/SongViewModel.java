package com.example.musicplayer.viewModel;

import android.Manifest;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;

import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;

public class SongViewModel extends AndroidViewModel {

    private SongRepository repository;
    private boolean isBottomSheetCollapsed;

    public SongViewModel(@NonNull Application application) {
        super(application);
        repository = new SongRepository(application);
        repository.refreshSongData();
        repository.refreshAlbumData();
        repository.refreshArtistData();
    }

    public boolean isBottomSheetCollapsed() {
        return isBottomSheetCollapsed;
    }

    public void setBottomSheetCollapsed(boolean bottomSheetCollapsed) {
        isBottomSheetCollapsed = bottomSheetCollapsed;
    }

    public ArrayList<Song> getAlbumSong(String v){
        return repository.getAlbumSong(v);
    }

    public MutableLiveData<ArrayList<Song>> getSongList()
    {
        return repository.getSongList();
    }

    public MutableLiveData<ArrayList<Album>> getAlbumList()
    {
        return repository.getAlbumList();
    }

    public MutableLiveData<ArrayList<Artist>> getArtistList()
    {
        return repository.getArtistList();
    }

}

package com.example.musicplayer.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.adapter.SongPagerAdapter;
import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;

import java.util.ArrayList;

public class SongViewModel extends AndroidViewModel {

    SongRepository repository;
    private boolean isBottomSheetCollapsed;
    MutableLiveData<SongPagerAdapter> songPagerAdapter;
    MutableLiveData<Song> currentSong;
    MutableLiveData<ArrayList<Song>> currentSongList;

    public SongViewModel(@NonNull Application application) {
        super(application);
        repository = new SongRepository(application);
        repository.refreshSongData();
        repository.refreshAlbumData();
        repository.refreshArtistData();
        songPagerAdapter = new MutableLiveData<>();
        currentSong = new MutableLiveData<>();
        currentSongList = new MutableLiveData<>();
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
    public ArrayList<Song> getArtistSong(String v){
        return repository.getArtistSong(v);
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

    public MutableLiveData<SongPagerAdapter> getPagerAdapter(){
        return songPagerAdapter;
    }

    public MutableLiveData<ArrayList<Song>> getCurrentSongList() {
        return currentSongList;
    }

    public void setCurrentSongList(ArrayList<Song> currentSongList) {
        this.currentSongList.setValue(currentSongList);
    }

    public MutableLiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song song) {
        currentSong.setValue(song);
    }
}

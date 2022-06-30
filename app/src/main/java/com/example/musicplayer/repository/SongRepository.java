package com.example.musicplayer.repository;

import android.Manifest;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.musicplayer.model.Album;
import com.example.musicplayer.model.Artist;
import com.example.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import permissions.dispatcher.NeedsPermission;

public class SongRepository {

    private MutableLiveData<ArrayList<Song>> songList;
    private MutableLiveData<ArrayList<Album>> albumList;
    private MutableLiveData<ArrayList<Artist>> artistList;
    private Context context;

    public SongRepository(Application application) {
        this.songList  = new MutableLiveData<>();
        this.context = application;
        this.albumList = new MutableLiveData<>();
        this.artistList = new MutableLiveData<>();
    }

    public void refreshSongData(){
        GetSongs getSongs = new GetSongs(context);
        if(getSongs.getStatus() != AsyncTask.Status.RUNNING) getSongs.execute();
        else if(getSongs.getStatus() == AsyncTask.Status.FINISHED)
        {
            Toast.makeText(context, "music loaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshAlbumData(){
        albumList.setValue(getAlbums(context));
    }

    public void refreshArtistData(){
        artistList.setValue(getArtist(context));
    }

    public MutableLiveData<ArrayList<Song>> getSongList()
    {
        if(songList==null) refreshSongData();
        return songList;
    }

    public MutableLiveData<ArrayList<Album>> getAlbumList()
    {
        if(albumList==null) refreshAlbumData();
        return albumList;
    }

    public MutableLiveData<ArrayList<Artist>> getArtistList()
    {
        if(artistList==null) refreshArtistData();
        return artistList;
    }

    public ArrayList<Song> getAlbumSong(String v)
    {

        String where = android.provider.MediaStore.Audio.Media.ALBUM + "=?";
        String[] whereVal = {v};

        String orderBy = android.provider.MediaStore.Audio.Media.TITLE;

        Cursor musicCursor =context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,where,whereVal, orderBy);

        ArrayList<Song> songArrayList = new ArrayList<Song>();
        if (musicCursor != null && musicCursor.moveToFirst() ) {
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumIDColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                Song song = new Song();
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String duration = musicCursor.getString(durationColumn);
                String albumID = musicCursor.getString(albumIDColumn);
                String path = musicCursor.getString(pathColumn);
                song.setTitle(title);
                song.setArtist(artist);
                song.setDuration(formateMilliSeccond(duration));
                song.setAlbum_id(albumID);
                song.setPath(path);
                // Add the info to our array.
                songArrayList.add(song);
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
        return songArrayList;
    }

    public ArrayList<Song> getSongList(Context context)
    {
        /*
        returns the list of songs available on user device
         */
        ContentResolver contentResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = contentResolver.query(musicUri, null, null, null, null, null);
        ArrayList<Song> songArrayList = new ArrayList<Song>();
        // If cursor is not null
        if(musicCursor != null && musicCursor.moveToFirst())
        {
            //get Columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumIDColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            // Store the title, id and artist name in Song Array list.
            do
            {
                Song song = new Song();
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String duration = musicCursor.getString(durationColumn);
                String albumID = musicCursor.getString(albumIDColumn);
                String path = musicCursor.getString(pathColumn);
                song.setTitle(title);
                song.setArtist(artist);
                song.setDuration(formateMilliSeccond(duration));
                song.setAlbum_id(albumID);
                song.setPath(path);
                // Add the info to our array.
                songArrayList.add(song);
            }
            while (musicCursor.moveToNext());

            // For best practices, close the cursor after use.
            musicCursor.close();
        }
        Collections.sort(songArrayList, new Comparator<Song>() {
            @Override
            public int compare(Song song1, Song song2) {
                return song1.getTitle().compareTo(song2.getTitle());
            }
        });
        return songArrayList;
    }

    public ArrayList<Album> getAlbums(Context context) {

        String where = null;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String _id;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            _id = MediaStore.Audio.Albums.ALBUM_ID;
        else _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        final String[] columns = { _id, album_name, artist, albumart, tracks };
        Cursor cursor = context.getContentResolver().query(uri, columns, where,
                null, null);

        ArrayList<Album> albumList = new ArrayList<Album>();

        // add playlsit to list

        if (cursor.moveToFirst()) {

            do {

                Album album = new Album();
                album.setId(cursor.getLong(cursor.getColumnIndexOrThrow(_id)));
                album.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(album_name)));
                album.setAlbumArtist(cursor.getString(cursor.getColumnIndexOrThrow(artist)));
                album.setAlbumartPath(cursor.getString(cursor.getColumnIndexOrThrow(albumart)));
                album.setTracks(cursor.getString(cursor.getColumnIndexOrThrow(tracks)));
                albumList.add(album);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return albumList;
    }

    public ArrayList<Artist> getArtist(Context context) {

        String where = null;

        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Artists._ID;
        final String artist_name = MediaStore.Audio.Artists.ARTIST;
        final String[] columns = { _id, artist_name};
        Cursor cursor = context.getContentResolver().query(uri, columns, where,
                null, null);

        ArrayList<Artist> artistList = new ArrayList<Artist>();

        // add playlsit to list

        if (cursor.moveToFirst()) {
            do {
                Artist artist = new Artist();
                artist.setId(cursor.getLong(cursor.getColumnIndexOrThrow(_id)));
                artist.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(artist_name)));
                artistList.add(artist);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return artistList;
    }

    public static String formateMilliSeccond(String ms) {
        String finalTimerString = "";
        String secondsString = "";
        long milliseconds = 0;
        if(ms!=null)
            milliseconds = Long.parseLong(ms);
        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString;
    }

    class GetSongs extends AsyncTask<Void,Void, ArrayList<Song>>
    {
        Context context;
        public GetSongs(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Song> doInBackground(Void... voids) {
            return getSongList(context);
        }

        @Override
        protected void onPostExecute(ArrayList<Song> strings) {
            super.onPostExecute(strings);
            songList.setValue(strings);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}

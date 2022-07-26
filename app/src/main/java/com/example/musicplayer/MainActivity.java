package com.example.musicplayer;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.musicplayer.Services.MediaPlayerService;
import com.example.musicplayer.adapter.SongPagerAdapter;
import com.example.musicplayer.fragment.MainFragment;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.repository.SongRepository;
import com.example.musicplayer.utils.LoadBitmap;
import com.example.musicplayer.utils.LoadPalette;
import com.example.musicplayer.viewModel.SongViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener, EasyPermissions.PermissionCallbacks {


    private static final int READ_STORAGE_PERMISSION_REQUEST = 123;
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST = 321;
    String permissionStorage = Manifest.permission.READ_EXTERNAL_STORAGE;
    String permissionPhoneState = Manifest.permission.READ_PHONE_STATE;
    SongViewModel songViewModel;
    ViewPager2 viewPager;
    FragmentActivity fragmentActivity;
    Animation fadeIn;
    TextView duration,pagerArtist,artist,pagerTitle,title,currPos,maxPos;
    View v;
    ImageView image;
    ConstraintLayout bottomSheetLayout;
    BottomSheetBehavior bottomSheetBehavior;
    MediaPlayerService mediaPlayerService;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.musicplayer.playNewAudio";
    public static final String ACTION_PLAY = "com.example.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.musicplayer.ACTION_PAUSE";
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    SeekBar seekBar;
    LottieAnimationView playPauseButton;
// Change to your package name

    private Handler seekbarUpdateHandler;
    private Runnable mUpdateSeekbar;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        callStateListener();

        seekbarUpdateHandler = new Handler();
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        v = findViewById(R.id.view);
        playPauseButton = findViewById(R.id.animatedButton);
        viewPager = findViewById(R.id.song_pager);
        viewPager.setOffscreenPageLimit(3);
        seekBar = findViewById(R.id.seekbar);
        currPos = findViewById(R.id.curr_position);
        maxPos = findViewById(R.id.max_position);

        //Button button = findViewById(R.id.play_pause);
        title = findViewById(R.id.sheet_title);
        pagerTitle = findViewById(R.id.pager_title);
        image = findViewById(R.id.sheet_album_art);
        artist = findViewById(R.id.sheet_artist);
        pagerArtist = findViewById(R.id.pager_artist);
        duration = findViewById(R.id.sheet_duration);
        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fragmentActivity = this;
        beginProcess();


    }

    @AfterPermissionGranted(123)
    private void beginProcess() {

        if(EasyPermissions.hasPermissions(this, permissionStorage)) {
            songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
            songViewModel.setBottomSheetCollapsed(true);
            songViewModel.setIsPlaying(false);
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_view, mainFragment)
                    .setReorderingAllowed(true)
                    .commit();

            mUpdateSeekbar = new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        currPos.setText(SongRepository.formateMilliSeccond(Integer.toString(mediaPlayer.getCurrentPosition())));
                        seekbarUpdateHandler.postDelayed(this, 50);
                    }
                }
            };

            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
            CardView card = findViewById(R.id.sheet_album_art_card);
            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    switch(newState)
                    {
                        case STATE_EXPANDED:card.setAlpha(0f);
                            viewPager.setAlpha(1f);
                            songViewModel.setBottomSheetCollapsed(false);
                            break;
                        default:card.setAlpha(1f);
                            v.setVisibility(View.INVISIBLE);
                            songViewModel.setBottomSheetCollapsed(true);
                            viewPager.setAlpha(0f);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    title.setAlpha((1-slideOffset)*(1-slideOffset));
                    artist.setAlpha((1-slideOffset)*(1-slideOffset));
                    duration.setAlpha((1-slideOffset)*(1-slideOffset));
                    int image_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics());
                    float screen_width = Resources.getSystem().getDisplayMetrics().widthPixels-image_width;
                    float screen_height = Resources.getSystem().getDisplayMetrics().heightPixels-5*image_width+16;
                    card.setTranslationX(screen_width*0.5f*(slideOffset)*(slideOffset));
                    card.setTranslationY(screen_height*0.525f*slideOffset);
                    card.setScaleX(3.1f*(slideOffset)*(slideOffset)+1);
                    card.setScaleY(3.1f*(slideOffset)*(slideOffset)+1);
                    int initial_radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics());
                    int final_radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                    card.setRadius(initial_radius-(initial_radius-10)*slideOffset);
                }
            });
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    if(!songViewModel.isBottomSheetCollapsed())
                    {
                        v.setVisibility(View.VISIBLE);
                        v.setScaleX(20*positionOffset*positionOffset);
                        v.setScaleY(20*positionOffset*positionOffset);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    ArrayList<Song> songs = songViewModel.getCurrentSongList().getValue();//current songList loaded to ViewPager
                    if(songs!=null)
                    {
                        songViewModel.setCurrentSong(songs.get(position));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                }
            });
            SongPagerAdapter adapter = new SongPagerAdapter(this,this);
            songViewModel.getCurrentSongList().observe(this, new Observer<ArrayList<Song>>() {
                @Override
                public void onChanged(ArrayList<Song> songs) {
                    adapter.setSongList(songs);
                    viewPager.setAdapter(adapter);
                }
            });
            songViewModel.getCurrentSong().observe(this, new Observer<Song>() {
                @Override
                public void onChanged(Song song) {
                    title.setText(song.getTitle());
                    title.startAnimation(fadeIn);
                    artist.setText(song.getArtist());
                    artist.startAnimation(fadeIn);
                    pagerTitle.setText(song.getTitle());
                    pagerArtist.setText(song.getArtist());
                    duration.setText(song.getDuration());
                    duration.startAnimation(fadeIn);
                    playAudio(song);
                    LoadBitmap asynctask = new LoadBitmap(image,song.getId());
                    if(asynctask.getStatus() == AsyncTask.Status.RUNNING) {
                        Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
                        asynctask.cancel(true);
                    }
                    if(asynctask.getStatus() != AsyncTask.Status.RUNNING && asynctask.getStatus() != AsyncTask.Status.FINISHED)
                    {
                        asynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,song.getPath());
                    }
                    LoadPalette async = new LoadPalette(bottomSheetLayout,song);
                    if(async.getStatus() == AsyncTask.Status.RUNNING) {
                        async.cancel(true);
                    }
                    if(async.getStatus() != AsyncTask.Status.RUNNING && asynctask.getStatus() != AsyncTask.Status.FINISHED)
                    {
                        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,song.getPath());
                    }
//                new LoadBitmap(image,getApplicationContext()).execute(song.getPath());
//                new LoadPalette(bottomSheetLayout,song).execute(song.getPath());
                }
            });

            songViewModel.getIsPlaying().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean playing) {
                    //String text = (playing)?"PAUSE":"PLAY";
                    //button.setText(text);
                    if(playing){
                        playPauseButton.setProgress(0);
                        playPauseButton.pauseAnimation();
                        playPauseButton.setSpeed(1f);
                        playPauseButton.playAnimation();


                    }
                    else{
                        playPauseButton.setProgress(1f);
                        playPauseButton.pauseAnimation();
                        playPauseButton.setSpeed(-1f);
                        playPauseButton.playAnimation();
                    }
                }
            });

            playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(songViewModel.getIsPlaying().getValue()!=null)
                    {
                        boolean isPlaying = songViewModel.getIsPlaying().getValue();
                        //songViewModel.setIsPlaying(!playState);
                        if(isPlaying) pauseMedia();
                        else resumeMedia();
                    }
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        songViewModel.setResumePosition(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    seekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(mediaPlayer!=null) {
                        seekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
                        mediaPlayer.seekTo(songViewModel.getResumePosition());
                    }
                }
            });

        }
        else EasyPermissions.requestPermissions(this, "Our App Requires a permission to access your storage", READ_STORAGE_PERMISSION_REQUEST
                , permissionStorage);

    }

    public void setFullscreen()
    {
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onBackPressed() {

        if(EasyPermissions.hasPermissions(this, permissionStorage) && !songViewModel.isBottomSheetCollapsed())
            bottomSheetBehavior.setState(STATE_COLLAPSED);
        else
        super.onBackPressed();
    }

    private void playAudio(Song media) {
        //Check is service is active
        if (requestAudioFocus()) {
            if(mediaPlayer!=null && mediaPlayer.isPlaying()) stopMedia();
            initMediaPlayer(media);
        }
        songViewModel.setIsPlaying(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    @Override
    protected void onDestroy() {
        if(audioManager!=null)
            removeAudioFocus();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    private void initMediaPlayer(Song audio)
    {
        //Toast.makeText(this, "MediaPlayer", Toast.LENGTH_SHORT).show();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(audio.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    private void playMedia() {
        Log.d("MUSICPLAYER","onPlay()");
        if (!mediaPlayer.isPlaying()) {
            songViewModel.setIsPlaying(true);
            seekBar.setMax(mediaPlayer.getDuration());
            maxPos.setText(SongRepository.formateMilliSeccond(Integer.toString(mediaPlayer.getDuration())));
            seekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        Log.d("MUSICPLAYER","onStop()");
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            songViewModel.setIsPlaying(false);
            seekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            mediaPlayer.stop();
        }
    }



    private void pauseMedia() {
        Log.d("MUSICPLAYER","onPause()");
        if (mediaPlayer.isPlaying()) {
            seekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            songViewModel.setIsPlaying(false);
            mediaPlayer.pause();
            songViewModel.setResumePosition(mediaPlayer.getCurrentPosition());
        }
    }

    private void resumeMedia() {
        Log.d("MUSICPLAYER","onResume()");
        if (!mediaPlayer.isPlaying()) {
            songViewModel.setIsPlaying(true);
            seekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
            mediaPlayer.seekTo(songViewModel.getResumePosition());
            mediaPlayer.start();
        }
    }

    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d("MUSICPLAYER","onAUDIOFOUCUS_GAIN");
                //if (mediaPlayer == null) initMediaPlayer();
                if (mediaPlayer!=null && !mediaPlayer.isPlaying()) mediaPlayer.start();
                if (mediaPlayer != null)
                    mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                Log.d("MUSICPLAYER","onAUDIOFOUCUS_LOSS");
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopMedia();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        switch (i) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + i1);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + i1);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + i1);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    private void callStateListener() {

            // Get the telephony manager
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //Starting listening for PhoneState changes
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                        //if at least one call exists or the phone is ringing
                        //pause the MediaPlayer
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        case TelephonyManager.CALL_STATE_RINGING:
                            if (mediaPlayer != null) {
                                pauseMedia();
                                songViewModel.setOnGoingCall(true);
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            // Phone idle. Start playing.
                            if (mediaPlayer != null) {
                                if (songViewModel.isOnGoingCall()) {
                                    songViewModel.setOnGoingCall(false);
                                    resumeMedia();
                                }
                            }
                            break;
                    }
                }
            };
            // Register the listener with the telephony manager
            // Listen for changes to the device call state.
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){

            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
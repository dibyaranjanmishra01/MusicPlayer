package com.example.musicplayer;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.adapter.MainPagerAdapter;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.adapter.SongPagerAdapter;
import com.example.musicplayer.fragment.MainFragment;
import com.example.musicplayer.fragment.SongPagerFragment;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.DepthPageTransformer;
import com.example.musicplayer.utils.LoadBitmap;
import com.example.musicplayer.utils.LoadPalette;
import com.example.musicplayer.utils.RecyclerTouchListener;
import com.example.musicplayer.utils.ZoomOutSlideTransformer;
import com.example.musicplayer.viewModel.SongViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{



    SongViewModel songViewModel;
    ViewPager2 viewPager;
    FragmentActivity fragmentActivity;
    Animation fadeIn;
    TextView test,duration,pagerArtist,artist,pagerTitle,title;
    View v;
    ImageView image;
    ConstraintLayout bottomSheetLayout;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.setBottomSheetCollapsed(true);
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        v = findViewById(R.id.view);
        viewPager = findViewById(R.id.song_pager);
        viewPager.setOffscreenPageLimit(3);

        title = findViewById(R.id.sheet_title);
        pagerTitle = findViewById(R.id.pager_title);
        image = findViewById(R.id.sheet_album_art);
        artist = findViewById(R.id.sheet_artist);
        pagerArtist = findViewById(R.id.pager_artist);
        duration = findViewById(R.id.sheet_duration);
        test = findViewById(R.id.textView);
        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fragmentActivity = this;

        MainFragment mainFragment = new MainFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_view, mainFragment)
                        .setReorderingAllowed(true)
                        .commit();

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
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
                card.setTranslationY(screen_height*0.5f*slideOffset);
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
                test.setText(" "+positionOffset);
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
//

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

}
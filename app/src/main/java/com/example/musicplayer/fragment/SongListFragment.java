package com.example.musicplayer.fragment;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.adapter.SongPagerAdapter;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.LoadBitmap;
import com.example.musicplayer.utils.LoadPalette;
import com.example.musicplayer.utils.RecyclerTouchListener;
import com.example.musicplayer.utils.ZoomOutSlideTransformer;
import com.example.musicplayer.viewModel.SongViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class SongListFragment extends Fragment implements RecyclerTouchListener.SongClickListener{

    SongViewModel songViewModel;
    TextView title,pagerTitle,artist,pagerArtist,duration,test;
    ImageView image;
    Animation fadeIn;
    View v;
    ArrayList<Song> songList;
    ConstraintLayout bottomSheetLayout;
    ViewPager viewPager;
    SongListAdapter adapter;
    SongPagerAdapter pagerAdapter;
    RecyclerView recyclerView;
    RecyclerTouchListener recyclerTouchListener;

    public SongListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.song_list_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //isBottomSheetCollapsed = true;
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.setBottomSheetCollapsed(true);
        viewPager = getActivity().findViewById(R.id.song_pager);
        title = getActivity().findViewById(R.id.sheet_title);
        pagerTitle = getActivity().findViewById(R.id.pager_title);
        image = getActivity().findViewById(R.id.sheet_album_art);
        artist = getActivity().findViewById(R.id.sheet_artist);
        pagerArtist = getActivity().findViewById(R.id.pager_artist);
        duration = getActivity().findViewById(R.id.sheet_duration);
        test = getActivity().findViewById(R.id.textView);
        fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
        v = getActivity().findViewById(R.id.view);
        bottomSheetLayout = getActivity().findViewById(R.id.bottom_sheet);


        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SongListAdapter(getContext());

        recyclerTouchListener = new RecyclerTouchListener(getContext(),recyclerView,this);
        populateSong();

    }

    public void populateSong() {
        pagerAdapter = new SongPagerAdapter(getParentFragmentManager(),getContext());
        songViewModel.getSongList().observe(getActivity(), new Observer<ArrayList<Song>>() {
            @Override
            public void onChanged(ArrayList<Song> songs) {
                songList =songs;
                adapter.setSongList(songs);
                recyclerView.setAdapter(adapter);
                pagerAdapter.setSongList(songs);
                viewPager.setAdapter(pagerAdapter);
                recyclerTouchListener.setSongList(songs);
                recyclerView.addOnItemTouchListener(recyclerTouchListener);
//                MainFragment mainFragment = new MainFragment(songs);
//                getParentFragmentManager().beginTransaction()
//                        .add(R.id.fragment_container_view, mainFragment)
//                        .setReorderingAllowed(true)
//                        .commit();
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
                        title.setText(songs.get(position).getTitle());
                        artist.setText(songs.get(position).getArtist());
                        pagerTitle.setText(songs.get(position).getTitle());
                        pagerArtist.setText(songs.get(position).getArtist());
                        duration.setText(songs.get(position).getDuration());
                        new LoadBitmap(image,getContext()).execute(songs.get(position).getPath());
                        new LoadPalette(bottomSheetLayout,songs.get(position)).execute(songs.get(position).getPath());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });

    }

    @Override
    public void onClick(View view, int position, Song song) {
           // Toast.makeText(getContext(), "YES", Toast.LENGTH_SHORT).show();
            pagerAdapter = new SongPagerAdapter(getParentFragmentManager(),getContext());
            pagerAdapter.setSongList(songList);
            viewPager.setAdapter(pagerAdapter);
       // Toast.makeText(getContext(), "YES"+viewPager.getAdapter().getCount(), Toast.LENGTH_SHORT).show();
        title.setText(song.getTitle());
        title.startAnimation(fadeIn);
        artist.setText(song.getArtist());
        artist.startAnimation(fadeIn);
        pagerTitle.setText(song.getTitle());
        pagerArtist.setText(song.getArtist());
        duration.setText(song.getDuration());
        duration.startAnimation(fadeIn);
        new LoadBitmap(image,getContext()).execute(song.getPath());
        new LoadPalette(bottomSheetLayout,song).execute(song.getPath());
        viewPager.setCurrentItem(position);
        //Toast.makeText(getContext(), "SongListFrag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}

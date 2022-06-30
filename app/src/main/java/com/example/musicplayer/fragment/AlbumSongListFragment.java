package com.example.musicplayer.fragment;

import android.os.Bundle;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.SongListAdapter;
import com.example.musicplayer.adapter.SongPagerAdapter;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.LoadBitmap;
import com.example.musicplayer.utils.LoadPalette;
import com.example.musicplayer.utils.RecyclerTouchListener;
import com.example.musicplayer.viewModel.SongViewModel;

import java.util.ArrayList;

public class AlbumSongListFragment extends Fragment implements RecyclerTouchListener.SongClickListener{

    SongViewModel songViewModel;
    String val;
    RecyclerView recyclerView;
    SongListAdapter adapter;
    ViewPager viewPager;
    ArrayList<Song> albumSongList;
    TextView title,pagerTitle,artist,pagerArtist,duration,test;
    ImageView image;
    Animation fadeIn;
    View v;
    ConstraintLayout bottomSheetLayout;

    public AlbumSongListFragment(String v) {
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
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        albumSongList = songViewModel.getAlbumSong(val);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        title = getActivity().findViewById(R.id.sheet_title);
        pagerTitle = getActivity().findViewById(R.id.pager_title);
        artist = getActivity().findViewById(R.id.sheet_artist);
        pagerArtist = getActivity().findViewById(R.id.pager_artist);
        duration = getActivity().findViewById(R.id.sheet_duration);
        image = getActivity().findViewById(R.id.sheet_album_art);
        test = getActivity().findViewById(R.id.textView);
        fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
        v = getActivity().findViewById(R.id.view);
        bottomSheetLayout = getActivity().findViewById(R.id.bottom_sheet);


        adapter = new SongListAdapter(getContext());
        adapter.setSongList(albumSongList);
        recyclerView.setAdapter(adapter);
        RecyclerTouchListener recyclerTouchListener = new RecyclerTouchListener(getContext(),recyclerView,this);
        recyclerTouchListener.setSongList(albumSongList);
        recyclerView.addOnItemTouchListener(recyclerTouchListener);
        viewPager = getActivity().findViewById(R.id.song_pager);
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
                title.setText(albumSongList.get(position).getTitle());
                artist.setText(albumSongList.get(position).getArtist());
                pagerTitle.setText(albumSongList.get(position).getTitle());
                pagerArtist.setText(albumSongList.get(position).getArtist());
                duration.setText(albumSongList.get(position).getDuration());
                new LoadBitmap(image,getContext()).execute(albumSongList.get(position).getPath());
                new LoadPalette(bottomSheetLayout,albumSongList.get(position)).execute(albumSongList.get(position).getPath());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view, int position, Song song) {
        SongPagerAdapter pagerAdapter = new SongPagerAdapter(getParentFragmentManager(),getContext());
        pagerAdapter.setSongList(albumSongList);
        viewPager.setAdapter(pagerAdapter);
        //pagerAdapter.notifyDataSetChanged();
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
        Toast.makeText(getContext(), "AlbumSongListFrag", Toast.LENGTH_SHORT).show();
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
                title.setText(albumSongList.get(position).getTitle());
                artist.setText(albumSongList.get(position).getArtist());
                pagerTitle.setText(albumSongList.get(position).getTitle());
                pagerArtist.setText(albumSongList.get(position).getArtist());
                duration.setText(albumSongList.get(position).getDuration());
                new LoadBitmap(image,getContext()).execute(albumSongList.get(position).getPath());
                new LoadPalette(bottomSheetLayout,albumSongList.get(position)).execute(albumSongList.get(position).getPath());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}

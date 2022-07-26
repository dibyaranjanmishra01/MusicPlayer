package com.example.musicplayer.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MainPagerAdapter;
import com.example.musicplayer.adapter.SongPagerAdapter;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.LoadBitmap;
import com.example.musicplayer.utils.LoadPalette;
import com.example.musicplayer.viewModel.SongViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    SongViewModel songViewModel;
    ViewPager2 mainViewPager;
    TabLayout tabLayout;
    Animation fadeIn;
    TextView test,duration,pagerArtist,artist,pagerTitle,title;
    View v;
    ImageView image;
    ConstraintLayout bottomSheetLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        mainViewPager = view.findViewById(R.id.main_pager);
        mainViewPager.setOffscreenPageLimit(3);
        tabLayout = view.findViewById(R.id.tabs);
        fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
        v = getActivity().findViewById(R.id.view);
        image = getActivity().findViewById(R.id.sheet_album_art);
        bottomSheetLayout = getActivity().findViewById(R.id.bottom_sheet);
        title = getActivity().findViewById(R.id.sheet_title);
        pagerTitle = getActivity().findViewById(R.id.pager_title);
        artist = getActivity().findViewById(R.id.sheet_artist);
        pagerArtist = getActivity().findViewById(R.id.pager_artist);
        duration = getActivity().findViewById(R.id.sheet_duration);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                ObjectAnimator animator = ObjectAnimator.ofFloat(tab.view,"translationY",-10f);
                animator.setDuration(200);
                animator.setInterpolator(new OvershootInterpolator());
                animator.start();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(tab.view,"translationY",+10f);
                animator.setDuration(200);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mainViewPager.setAdapter(new MainPagerAdapter(getActivity()));
        new TabLayoutMediator(tabLayout, mainViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch(position)
                {
                    case 0:tab.setText("Songs");
                        break;
                    case 1:tab.setText("Albums");
                        break;
                    case 2:tab.setText("Artists");
                        break;
                }
            }
        }
        ).attach();
//        ObjectAnimator animator = ObjectAnimator.ofFloat(tabLayout.getTabAt(0).view,"translationY",-10f);
//        animator.setDuration(500);
//        animator.setInterpolator(new OvershootInterpolator());
//        animator.start();
    }

    @Override
    public void onResume() {
        super.onResume();



    }
}

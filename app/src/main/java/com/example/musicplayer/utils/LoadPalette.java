package com.example.musicplayer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

public class LoadPalette extends AsyncTask<String,Void, Palette> {

    View view,circle;
    TextView title,artist,duration,pagerTitle,pagerArtist;
    Song song;

    public LoadPalette(View v,Song song) {
        view = v;
        title = view.findViewById(R.id.sheet_title);
        artist = view.findViewById(R.id.sheet_artist);
        duration = view.findViewById(R.id.sheet_duration);
        circle = view.findViewById(R.id.view);
        pagerTitle = view.findViewById(R.id.pager_title);
        pagerArtist = view.findViewById(R.id.pager_artist);
        this.song = song;
    }

    @Override
    protected Palette doInBackground(String... strings) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] result = null;
        Bitmap bitmap = null;
        Palette p = null;
        try{
            mmr.setDataSource(strings[0]);
            result = mmr.getEmbeddedPicture();
            mmr.release();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = 1;
            options.outHeight = 1;
            bitmap = BitmapFactory.decodeByteArray(result, 0, result.length,options);
            if(bitmap!=null)
                p = Palette.generate(bitmap);
        }catch(Exception ignored){}
        return p;
    }

    @Override
    protected void onPostExecute(Palette palette) {
        super.onPostExecute(palette);
        int dominantColor = Color.rgb(236,239,239);
        if(palette!=null && palette.getDominantSwatch()!=null)
        {
            dominantColor = palette.getDominantSwatch().getRgb();

        }
        song.setDominantColor(dominantColor);
        view.setBackgroundColor(dominantColor);
        title.setTextColor(getContrastColor(dominantColor));
        artist.setTextColor(getContrastColor(dominantColor));
        pagerTitle.setTextColor(getContrastColor(dominantColor));
        pagerArtist.setTextColor(getContrastColor(dominantColor));
        duration.setTextColor(getContrastColor(dominantColor));
        GradientDrawable bgShape = (GradientDrawable)circle.getBackground();
        bgShape.mutate();
        bgShape.setColor(getExplosionColor(dominantColor));
    }

    int getContrastColor(int color){
        double whiteContrast = ColorUtils.calculateContrast(Color.WHITE, color);
        double blackContrast = (int) ColorUtils.calculateContrast(Color.BLACK, color);

        return (whiteContrast > blackContrast)?Color.rgb(250,250,250):Color.rgb(40,40,40);
    }
    int getExplosionColor(int color)
    {
        double whiteContrast = ColorUtils.calculateContrast(Color.WHITE, color);
        double blackContrast = (int) ColorUtils.calculateContrast(Color.BLACK, color);

        return (whiteContrast > blackContrast)?
                ColorUtils.blendARGB(color,Color.GRAY,0.5f):ColorUtils.blendARGB(color,Color.WHITE,0.5f);

    }
}

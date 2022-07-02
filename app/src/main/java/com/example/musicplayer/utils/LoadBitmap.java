package com.example.musicplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicplayer.R;

import java.lang.ref.WeakReference;

public class LoadBitmap extends AsyncTask<String,Void,Bitmap>
{

    WeakReference<ImageView> albumart;
    long id;
    BitmapFactory.Options options;

    public LoadBitmap(ImageView albumArt,long id) {
        this.albumart = new WeakReference<ImageView>(albumArt);
        options = new BitmapFactory.Options();
        this.id = id;
        options.inSampleSize = 8;

    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] result;
        Bitmap bitmap = null;
        try{
            mmr.setDataSource(strings[0]);
            result = mmr.getEmbeddedPicture();
            mmr.release();
            bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
        }catch(Exception ignored){}
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (isCancelled()) {
            bitmap = null;
        }

        if (albumart != null ) {
            final ImageView imageView = albumart.get();
            if (imageView != null && bitmap!= null) {
                imageView.setImageBitmap(bitmap);
            }
            else if(imageView != null)
            {
                imageView.setImageDrawable(null);
                imageView.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
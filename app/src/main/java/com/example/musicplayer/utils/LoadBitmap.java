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

public class LoadBitmap extends AsyncTask<String,Void,Bitmap>
{

    private final ImageView albumart;
    private final Context context;
    BitmapFactory.Options options;

    public LoadBitmap(ImageView albumArt,Context context) {
        this.albumart = albumArt;
        this.context = context;
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;

    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] result = null;
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
        if(bitmap!=null)
        {
            //Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);
            albumart.setImageBitmap(bitmap);
            //Glide.with(context).load(bitmap).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.purple_200).into(albumart);
        }
        else {
            albumart.setImageDrawable(null);
            albumart.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }
}
package com.example.musicplayer.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<LoadBitmap> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap,
                         LoadBitmap bitmapWorkerTask) {
        super(res, bitmap);
        bitmapWorkerTaskReference =
                new WeakReference<LoadBitmap>(bitmapWorkerTask);
    }

    public LoadBitmap getLoadBitmapTask() {
        return bitmapWorkerTaskReference.get();
    }

    public boolean cancelPotentialWork(long data, ImageView imageView) {
        final LoadBitmap task = getBitmapWorkerTask(imageView);

        if (task != null) {
            final long bitmapData = task.id;
            if (bitmapData != data) {
                // Cancel previous task
                task.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    LoadBitmap getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getLoadBitmapTask();
            }
        }
        return null;
    }


}

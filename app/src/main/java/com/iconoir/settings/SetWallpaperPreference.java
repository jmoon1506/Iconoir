package com.iconoir.settings;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class SetWallpaperPreference extends DialogPreference {

    public SetWallpaperPreference(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(getContext());
            try {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
                Bitmap oldImg = drawableToBitmap(wallpaperManager.getDrawable());
                String destFolder = getContext().getCacheDir().getAbsolutePath();
                FileOutputStream out = new FileOutputStream(destFolder + "/wallpaper.png");
                oldImg.compress(Bitmap.CompressFormat.PNG, 100, out);


                Bitmap newImg = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wallpaper);
                wallpaperManager.setBitmap(newImg);
            } catch (IOException e) {
                Toast.makeText(getContext(),
                        "Could not set background.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
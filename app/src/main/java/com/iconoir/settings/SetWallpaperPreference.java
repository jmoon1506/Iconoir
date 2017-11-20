package com.iconoir.settings;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SetWallpaperPreference extends DialogPreference {
    WallpaperManager wallpaperManager;
    Boolean restore = false;

    public SetWallpaperPreference(Context context, AttributeSet attrs){
        super(context, attrs);
        wallpaperManager = WallpaperManager.getInstance(getContext());
        if (isWallpaperSet()) {
            String destFolder = getContext().getCacheDir().getAbsolutePath();
            File oldWallpaper = new File(destFolder + "/old_wallpaper.png");
            if (oldWallpaper.exists()) {
                enableRestore(true, false);
            } else {
                enableRestore(false, false);
            }
        } else {
            enableRestore(false, true);
        }
    }

    private void enableRestore(Boolean enableRestore, Boolean enableSet) {
        if (enableRestore) {
            setEnabled(true);
            setTitle(R.string.prefTitleRestoreBackground);
            setSummary(R.string.prefDescRestoreBackground);
            setDialogMessage(R.string.prefTitleRestoreBackground);
            restore = true;
        } else {
            setEnabled(enableSet);
            setTitle(R.string.prefTitleSetBackground);
            setSummary(R.string.prefDescSetBackground);
            setDialogMessage(R.string.prefDescSetBackground);
            restore = false;
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            try {
                Bitmap newImg;
                if (restore) {
                    String destFolder = getContext().getCacheDir().getAbsolutePath();
                    newImg = BitmapFactory.decodeFile(destFolder + "/old_wallpaper.png");
                    enableRestore(false, true);
                } else {
                    Bitmap oldImg = drawableToBitmap(wallpaperManager.getDrawable());
                    String destFolder = getContext().getCacheDir().getAbsolutePath();
                    FileOutputStream out = new FileOutputStream(destFolder + "/old_wallpaper.png");
                    oldImg.compress(Bitmap.CompressFormat.PNG, 100, out);

                    newImg = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wallpaper);
                    enableRestore(true, false);
                }
                wallpaperManager.setBitmap(newImg);
            } catch (IOException e) {
                Toast.makeText(getContext(),getContext().getString(R.string.errorSetWallpaper),
                        Toast.LENGTH_SHORT).show();
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

    public boolean isWallpaperSet() {
        Bitmap currentImg = drawableToBitmap(wallpaperManager.getDrawable());
        Bitmap iconoirImg = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wallpaper);
        return imagesAreEqual(currentImg, iconoirImg);
    }

    public static boolean imagesAreEqual(Bitmap wallpaper, Bitmap black) {
//        if (img1.getHeight() != img2.getHeight()) return false;
//        if (img1.getWidth() != img2.getWidth()) return false;
        for (int y = 0; y < black.getHeight(); ++y)
            for (int x = 0; x < black.getWidth(); ++x)
                if (black.getPixel(x, y) != wallpaper.getPixel(x, y)) return false;
        return true;
    }
}
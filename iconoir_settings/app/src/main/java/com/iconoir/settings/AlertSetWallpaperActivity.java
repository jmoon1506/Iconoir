package com.iconoir.settings;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

import static com.iconoir.settings.SetWallpaperPreference.drawableToBitmap;

public class AlertSetWallpaperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_set_wallpaper);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setWallpaper()) {
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    onBackPressed();
                }
            }
        });
    }

    private boolean setWallpaper() {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            Bitmap oldImg = drawableToBitmap(wallpaperManager.getDrawable());
                String destFolder = getCacheDir().getAbsolutePath();
                FileOutputStream out = new FileOutputStream(destFolder + "/old_wallpaper.png");
                oldImg.compress(Bitmap.CompressFormat.PNG, 100, out);

            Bitmap newImg = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
            wallpaperManager.setBitmap(newImg);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.errorSetWallpaper),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

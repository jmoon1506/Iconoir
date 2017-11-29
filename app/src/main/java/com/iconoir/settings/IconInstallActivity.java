package com.iconoir.settings;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import static com.iconoir.settings.IconListAdapter.getDrawableId;

public class IconInstallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_install);
        addGooglePlayListener();
        setupActionBar();
        String iconPkg = getIntent().getStringExtra("iconPkg");
        ImageView icon = (ImageView) findViewById(R.id.icon);
        int drawableId = getDrawableId(iconPkg);
        icon.setImageDrawable(getResources().getDrawable(drawableId));
    }

    public void addGooglePlayListener() {
        ImageButton btnGooglePlay = (ImageButton) findViewById(R.id.btnGooglePlay);
        btnGooglePlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(IconInstallActivity.this,
                            "Could not open Google Play", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}

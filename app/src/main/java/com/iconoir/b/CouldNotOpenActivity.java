package com.iconoir.b;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CouldNotOpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_could_not_open);
        String targetPkg = getIntent().getExtras().getString("targetPkg", "");
        TextView msg = (TextView) findViewById(R.id.couldNotOpenMsg);
        msg.setText(getString(R.string.couldNotOpenMessage) + targetPkg);
    }

    public void addSettingsListener() {
        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(
                            getString(R.string.iconoirSettingsPackage));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {

                    Toast.makeText(CouldNotOpenActivity.this,
                            "Could not open Iconoir Settings", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}

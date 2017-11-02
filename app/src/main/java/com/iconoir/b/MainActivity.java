package com.iconoir.b;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.pm.PackageManager;

import java.util.List;

public class MainActivity extends Activity {
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pm = getPackageManager();
        if (!tryLaunchTarget()) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            addGooglePlayListener();
        }
    }

    protected void onResume() {
        if (!tryLaunchTarget()) {
            super.onResume();
        }
    }

    private boolean tryLaunchTarget() {
        if (settingsExists()) {
            try {
                Intent intent = pm.getLaunchIntentForPackage("com.google.android.youtube");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                return true;
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this,
                        "Could not open YouTube", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean settingsExists(){
        try {
            PackageInfo info=pm.getPackageInfo("com.iconoir.settings",
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
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
                    Toast.makeText(MainActivity.this,
                            "Could not open Google Play", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}

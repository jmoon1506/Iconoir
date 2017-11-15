package com.iconoir.b;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.pm.PackageManager;

import java.util.List;

public class MainActivity extends Activity {
    String packageName;
    String targetPkg;
    PackageManager pm;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        overridePendingTransition(0, 0);
        packageName = getApplicationContext().getPackageName();
        pm = getPackageManager();
        readSharedPreferences();
        if (!tryLaunchTarget()) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            addGooglePlayListener();
        }
    }

    protected void onResume() {
        readSharedPreferences();
        if (!tryLaunchTarget()) {
            super.onResume();
        }
    }

    private void readSharedPreferences() {
        Context sharedContext = null;
        try {
            sharedContext = this.createPackageContext(
                    getString(R.string.iconoirSettingsPackage), 0);
            if (sharedContext == null) {
                System.out.println("DB error : no shared context");
                return;
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            System.out.println("DB error : " + e.getMessage());
            return;
        }
        pref = sharedContext.getSharedPreferences(getString(R.string.sharedPrefLabel),
                Activity.MODE_PRIVATE);
        targetPkg = pref.getString(packageName, "");
    }

    private boolean tryLaunchTarget() {
        if (settingsExists()) {
            try {
                Intent intent = pm.getLaunchIntentForPackage(targetPkg);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);
                overridePendingTransition(android.R.anim.fade_in, 0);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                return true;
            } catch (Exception e) {
                Intent intent = new Intent( this, CouldNotOpenActivity.class );
                intent.putExtra("targetPkg", targetPkg);
                startActivity(intent);
                Toast.makeText(MainActivity.this,
                        "Could not open " + targetPkg, Toast.LENGTH_SHORT)
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

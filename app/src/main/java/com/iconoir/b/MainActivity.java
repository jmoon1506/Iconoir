package com.iconoir.b;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
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
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    protected void onCreate(Bundle savedInstanceState) {
//        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        packageName = getApplicationContext().getPackageName();
        pm = getPackageManager();
//        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//                targetPkg = pref.getString(packageName, "");
//                Log.d("PKG", "CHANGE");
//                Log.d("PKG", targetPkg);
//            }
//        };

        tryLaunch();
        setContentView(R.layout.activity_main);
        addGooglePlayListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        tryLaunch();
    }

    private void tryLaunch() {
        if (settingsExists()) {
            readSharedPreferences();
            tryLaunchTarget();
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
                Activity.MODE_PRIVATE | MODE_MULTI_PROCESS);
//        pref.registerOnSharedPreferenceChangeListener(listener);
        targetPkg = pref.getString(packageName, "");
//        Log.d("PKG", targetPkg);
    }

    private void tryLaunchTarget() {
        try {
            Intent intent = pm.getLaunchIntentForPackage(targetPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 1);
            finish();
            startActivityForResult(intent, 1);
            overridePendingTransition(android.R.anim.fade_in, 0);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } catch (Exception e) {
            if (targetPkg.equals(getString(R.string.androidPhonePackage))) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivityForResult(intent, 1);
                overridePendingTransition(android.R.anim.fade_in, 0);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            } else {
                Intent intent = new Intent(this, CouldNotOpenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("targetPkg", targetPkg);
                overridePendingTransition(0, 0);
                startActivity(intent);
//            Toast.makeText(MainActivity.this,"Could not open " + targetPkg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean settingsExists(){
        try {
            PackageInfo info=pm.getPackageInfo(getString(R.string.iconoirSettingsPackage),
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
                    intent.setData(Uri.parse(getString(R.string.settingsGooglePlay)));
//                    intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
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

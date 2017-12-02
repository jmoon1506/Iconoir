package com.iconoir.b;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {
    String packageName;
    String targetPkg;
    PackageManager packageManager;
    SharedPreferences pref;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    TextView header;
    TextView message;
    Boolean initialResume = true;

    protected void onCreate(Bundle savedInstanceState) {
//        overridePendingTransition(0, 0);
        packageName = getApplicationContext().getPackageName();
        packageManager = getPackageManager();

        tryLaunch();
        super.onCreate(savedInstanceState);
    }



    @Override
    protected void onResume() {
        if (initialResume) {
            initialResume = false;
        } else {
            tryLaunch();
        }
        super.onResume();
    }

    private void tryLaunch() {
        if (settingsExists()) {
            readSharedPreferences();
            tryLaunchTarget();
        } else {
            setContentView(R.layout.activity_main);
            header = findViewById(R.id.header);
            message = findViewById(R.id.message);
            findViewById(R.id.btnGooglePlay).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSettings).setVisibility(View.GONE);
            findViewById(R.id.btnSettingsLabel).setVisibility(View.GONE);
            header.setText(getString(R.string.settingsNotInstalled));
            message.setText(getString(R.string.settingsNotInstalledMsg));
            addGooglePlayListener();
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
    }

    private void tryLaunchTarget() {
        try {
            Intent intent = packageManager.getLaunchIntentForPackage(targetPkg);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 1);
            finish();
            startActivityForResult(intent, 1);
            overridePendingTransition(android.R.anim.fade_in, 0);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } catch (Exception e1) {
            if (targetPkg.equals(getString(R.string.androidPhonePackage))) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivityForResult(intent, 1);
                    overridePendingTransition(android.R.anim.fade_in, 0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                } catch (Exception e2) {
                    setLayoutCouldNotOpen();
                }
            } else {
                setLayoutCouldNotOpen();
            }
        }
    }

    private void setLayoutCouldNotOpen() {
        setContentView(R.layout.activity_main);
        header = findViewById(R.id.header);
        message = findViewById(R.id.message);
        findViewById(R.id.btnGooglePlay).setVisibility(View.GONE);
        findViewById(R.id.btnSettings).setVisibility(View.VISIBLE);
        findViewById(R.id.btnSettingsLabel).setVisibility(View.VISIBLE);
        if (targetPkg.isEmpty()) {
            header.setText(getString(R.string.noTargetSet));
            message.setText(getString(R.string.noTargetSetMsg));
        } else {
            header.setText(getString(R.string.couldNotOpen, getPackageLabel(targetPkg)));
            message.setText(getString(R.string.couldNotOpenMsg));
        }
        addSettingsListener();
    }

    private String getPackageLabel(String pkgName) {
        try {
            ApplicationInfo app = getPackageManager().getApplicationInfo(pkgName, 0);
            return (String) getPackageManager().getApplicationLabel(app);
        } catch (PackageManager.NameNotFoundException e) {
            return pkgName;
        }
    }

    private boolean settingsExists(){
        try {
            PackageInfo info= packageManager.getPackageInfo(getString(R.string.iconoirSettingsPackage),
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

    public void addSettingsListener() {
        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(
                            getString(R.string.iconoirSettingsPackage));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("callFromIcon", getApplicationContext().getPackageName());
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {

                    Toast.makeText(MainActivity.this,
                            "Could not open Iconoir Settings", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}

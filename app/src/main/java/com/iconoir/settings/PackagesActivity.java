package com.iconoir.settings;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackagesActivity extends AppCompatActivity {
    ListView listView;
    PackageListAdapter listAdapter;
    PackageManager packageManager;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageManager = getPackageManager();
        pref = getApplicationContext().getSharedPreferences("IconoirSettings", MODE_WORLD_READABLE); // 0 - for private mode
        setContentView(R.layout.activity_packages);
        setTitle(R.string.actionBarPackages);
        setupActionBar();
        loadPackageList();
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString("appPkg", "com.android.mail");

        Intent i = new Intent();
        i.putExtras(bundle);
        setResult(RESULT_OK, i);
        super.onBackPressed();
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Remove the left caret
            getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon
        }
    }

    private void loadPackageList() {
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<PackageInfo> visibleList = new ArrayList<PackageInfo>();

        for(PackageInfo pi : packageList) {
            if(!isSystemPackage(pi)) {
                visibleList.add(pi);
            }
        }

        listAdapter = new PackageListAdapter(this, visibleList);
        listAdapter.setShowAll(pref.getBoolean("showSystemPackages", false));
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);
    }

    private boolean isSystemPackage(PackageInfo appInfo) {
        return (appInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}

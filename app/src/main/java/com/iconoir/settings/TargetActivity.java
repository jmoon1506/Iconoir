package com.iconoir.settings;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TargetActivity extends AppCompatActivity {
    ListView listView;
    TargetListAdapter listAdapter;
    PackageManager packageManager;
    SharedPreferences pref;
    List<String> validSystemPkgs;
    List<String> iconoirPkgs;
    String iconoirSettingsPkg;
    boolean showAllSystemPkgs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageManager = getPackageManager();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        pref = getApplicationContext().getSharedPreferences("IconoirSettings", MODE_PRIVATE); // 0 - for private mode
        validSystemPkgs = Arrays.asList(getResources().getStringArray(R.array.validSystemPackages));
        iconoirPkgs = Arrays.asList(getResources().getStringArray(R.array.iconoirPackages));
        iconoirSettingsPkg = getResources().getString(R.string.iconoirSettingsPackage);
        setContentView(R.layout.activity_target);
        setTitle(R.string.actionBarPackages);
        setupActionBar();
    }

    @Override
    protected void onResume() {
        showAllSystemPkgs = pref.getBoolean("showSystemPkgs", false);
        loadPackageList();
        super.onResume();
    }

    public void onBackPressed(String targetPkg) {
        Bundle bundle = new Bundle();
        bundle.putString("targetPkg", targetPkg);

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
        List<String> visibleList = new ArrayList<String>();
        Map<String, PackageInfo> packageMap = new TreeMap<>();

        for(PackageInfo pi : packageList) {
            if(isValidPackage(pi)) {
                final String pkgName = packageManager.getApplicationLabel(pi.applicationInfo).toString();
                packageMap.put(pkgName, pi);
                visibleList.add(pkgName);
            }
        }

        listAdapter = new TargetListAdapter(this, visibleList, packageMap);
        listAdapter.setShowAll(pref.getBoolean("showSystemPkgs", false));
        listView = findViewById(R.id.listView);
        listView.setAdapter(listAdapter);
    }

    private boolean isValidPackage(PackageInfo appInfo) {
        if (isSystemPackage(appInfo)) {
            if (showAllSystemPkgs) {
                return true;
            } else {
                return validSystemPkgs.contains(appInfo.packageName);
            }
        } else {
            return !(iconoirPkgs.contains(appInfo.packageName) || iconoirSettingsPkg.equals(appInfo.packageName));
        }
    }

    private boolean isSystemPackage(PackageInfo appInfo) {
        return (appInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}

package com.iconoir.settings;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class TargetActivity extends AppCompatActivity {
    RecyclerView targetListView;
    TargetListAdapter targetListAdapter;
    PackageManager packageManager;
    SharedPreferences pref;
    Locale locale;
    Configuration config;
    DisplayMetrics displayMetrics;

    PkgChangeReceiver broadcastReceiver;
    Integer pkgChangeSequence = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageManager = getPackageManager();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        locale = getResources().getConfiguration().locale;
        config = new Configuration();
        config.locale = locale;
        displayMetrics = getResources().getDisplayMetrics();
        setContentView(R.layout.activity_target);
        setTitle(R.string.actionBarPackages);

        targetListView = findViewById(R.id.recyclerView);
        targetListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setItemPrefetchEnabled(true);
        targetListView.setLayoutManager(llm);

        if (Build.VERSION.SDK_INT < 26) {
            broadcastReceiver = new PkgChangeReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme("package");
            registerReceiver(broadcastReceiver, intentFilter);
        }



        loadPackageList();
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT < 26) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public class PkgChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadPackageList();
        }
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= 26) {
            ChangedPackages changes = packageManager.getChangedPackages(pkgChangeSequence);
            if (changes != null) {
                pkgChangeSequence = changes.getSequenceNumber();
                loadPackageList();
            }
        }
        targetListAdapter.setShowAll(pref.getBoolean("showSystemPkgs", false));
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

    private void loadPackageList() {
        Bundle bundle = getIntent().getExtras();
        List<PackageInfo> pkgInfos = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        Map<String, PackageInfo> labelInfoMap = new HashMap<>();
        List<String> validSystemPkgs = Arrays.asList(getResources().getStringArray(R.array.validSystemPackages));
        List<String> iconoirPkgs = Arrays.asList(getResources().getStringArray(R.array.iconoirPackages));
        String iconoirSettingsPkg = getResources().getString(R.string.iconoirSettingsPackage);
        Boolean showSystemPkgs = pref.getBoolean("showSystemPkgs", false);
        Boolean labelsLoaded = bundle.getBoolean("labelsLoaded", false);
        for (PackageInfo pkgInfo : pkgInfos) {
            if ((isSystemPackage(pkgInfo) && (showSystemPkgs || validSystemPkgs.contains(pkgInfo.packageName)))
                    || (!isSystemPackage(pkgInfo)
                    && !(iconoirPkgs.contains(pkgInfo.packageName) || iconoirSettingsPkg.equals(pkgInfo.packageName)))) {
                if (labelsLoaded) {
                    labelInfoMap.put(bundle.getString(pkgInfo.packageName), pkgInfo);
                } else {
                    labelInfoMap.put(pkgInfo.applicationInfo.loadLabel(packageManager).toString(), pkgInfo);
                }
            }
        }
        targetListAdapter = new TargetListAdapter(this, labelInfoMap);
        targetListAdapter.setHasStableIds(true);
        targetListView.setAdapter(targetListAdapter);
    }

    public boolean isSystemPackage(PackageInfo appInfo) {
        return (appInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}

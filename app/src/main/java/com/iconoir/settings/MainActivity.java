package com.iconoir.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    LauncherListAdapter listAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.actionBarTitle);
        packageManager = getPackageManager();
        readSharedPreferences();
        loadLauncherList();
        addShowAllListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.advanced:
                startActivity(new Intent(this, AdvancedActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readSharedPreferences() {
        pref = getApplicationContext().getSharedPreferences("IconoirSettings", MODE_WORLD_READABLE); // 0 - for private mode
        editor = pref.edit();
        editor.putString("com.iconoir.B", "com.google.android.youtube");
        editor.putBoolean("showAllEnabled", false);
        editor.commit();
    }

    private void loadLauncherList() {
        Map<String, String> launcherMap = new HashMap<String, String>();

        List<ApplicationInfo> appsList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        String[] iconPkgList = getResources().getStringArray(R.array.iconPackages);
        for (String iconPkg : iconPkgList) {
            String launchPkg = pref.getString(iconPkg, "");
            launcherMap.put(iconPkg, launchPkg);
        }

        listAdapter = new LauncherListAdapter(this, launcherMap);
        listAdapter.setShowAll(pref.getBoolean("showAllEnabled", false));
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);
    }

    private void addShowAllListener() {
        Switch showAll = (Switch) findViewById(R.id.switchShowAll);
        showAll.setChecked(pref.getBoolean("showAllEnabled", false));
        showAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listAdapter.setShowAll(isChecked);
//                listAdapter.notifyDataSetChanged();
                editor.putBoolean("showAllEnabled", isChecked);
                editor.commit();
            }
        });
    }
}

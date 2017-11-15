package com.iconoir.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    IconListAdapter listAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    PackageManager packageManager;
    Map<String, String> iconTargetMap;

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
    protected void onResume() {
        listAdapter.updateHiddenPositions();
        super.onResume();
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
                Intent intent = new Intent( this, AdvancedActivity.class );
                intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                        "com.iconoir.settings.AdvancedActivity$InterfacePreferenceFragment" );
                intent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
                startActivity(intent);
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readSharedPreferences() {
        pref = getSharedPreferences("IconoirSettings", MODE_PRIVATE); // 0 - for private mode
        editor = pref.edit();
        editor.putBoolean("showAllEnabled", false);
        editor.commit();
    }

    private void loadLauncherList() {
        iconTargetMap = new HashMap<String, String>();

        String[] iconPkgList = getResources().getStringArray(R.array.iconPackages);
        for (String iconPkg : iconPkgList) {
            String packagePkg = pref.getString(iconPkg, "");
            iconTargetMap.put(iconPkg, packagePkg);
        }

        listAdapter = new IconListAdapter(this, iconTargetMap);
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
                editor.putBoolean("showAllEnabled", isChecked);
                editor.commit();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String targetPkg = data.getStringExtra("targetPkg");
                listAdapter.updateIconPackageMap(targetPkg);
            }
        }
    }
}

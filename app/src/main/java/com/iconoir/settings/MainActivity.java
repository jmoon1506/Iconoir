package com.iconoir.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RecyclerView iconListView;
    IconListAdapter iconListAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    PackageManager packageManager;
    Map<String, String> iconTargetMap;

    private static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";
    private static final String SHARED_FOLDER = "shared";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.actionBarTitle);
        packageManager = getPackageManager();
        readSharedPreferences();

        LoadIconList();
//        new LoadIconTask().execute();
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
        pref = getApplicationContext().getSharedPreferences(
                getString(R.string.sharedPrefLabel), MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean("showAllEnabled", false);
        editor.commit();
    }

    @NonNull
    private File createFile() throws IOException {

        final File sharedFolder = new File(getFilesDir(), SHARED_FOLDER);
        sharedFolder.mkdirs();

        final File sharedFile = File.createTempFile("picture", ".png", sharedFolder);
        sharedFile.createNewFile();

        return sharedFile;
    }

    public void LoadIconList() {
        iconListView = (RecyclerView) findViewById(R.id.recyclerView);
        iconListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setItemPrefetchEnabled(false);
        iconListView.setLayoutManager(llm);
        iconTargetMap = new HashMap<String, String>();
        String[] iconPkgList = getResources().getStringArray(R.array.iconoirPackages);
        for (String iconPkg : iconPkgList) {
            String packagePkg = pref.getString(iconPkg, "");
            iconTargetMap.put(iconPkg, packagePkg);
        }
        iconListAdapter = new IconListAdapter(MainActivity.this, iconTargetMap);
        iconListAdapter.setHasStableIds(true);
        iconListView.setAdapter(iconListAdapter);

    }

//    public class LoadIconTask extends AsyncTask<String, Void, Integer> {
//        @Override
//        protected Integer doInBackground(String... params) {
//            Integer result = 0;
//
//            iconTargetMap = new HashMap<String, String>();
//            String[] iconPkgList = getResources().getStringArray(R.array.iconoirPackages);
//            for (String iconPkg : iconPkgList) {
//                String packagePkg = pref.getString(iconPkg, "");
//                iconTargetMap.put(iconPkg, packagePkg);
//            }
//            iconListAdapter = new IconListAdapter(MainActivity.this, iconTargetMap);
//            iconListView.setAdapter(iconListAdapter);
//            addShowAllListener();
//
//            return result; //"Failed to set adapters!";
//        }
//    }

    private void addShowAllListener() {
        Switch showAll = (Switch) findViewById(R.id.switchShowAll);
        showAll.setChecked(pref.getBoolean("showAllEnabled", false));
        showAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iconListAdapter.setShowAll(isChecked);
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
//                editor.putString("showAllEnabled", false);
                String iconPkg = iconListAdapter.updateIconPackageMap(targetPkg);
                editor.putString(iconPkg, targetPkg);
                editor.commit();
            }
        }
    }
}

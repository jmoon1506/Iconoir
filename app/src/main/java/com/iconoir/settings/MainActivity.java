package com.iconoir.settings;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iconoir.settings.SetWallpaperPreference.drawableToBitmap;
import static com.iconoir.settings.SetWallpaperPreference.imagesAreEqual;

public class MainActivity extends AppCompatActivity {
    RecyclerView iconListView;
    IconListAdapter iconListAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    PackageManager packageManager;
    Menu optionMenu;
    Map<String, String> labelMap = null;
    PkgChangeReceiver broadcastReceiver;
    Integer pkgChangeSequence = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.actionBarTitle);
        packageManager = getPackageManager();
        readSharedPreferences();
        loadIconList();
        addShowAllListener();

        LoadTargetLabels loadTargetLabels = new LoadTargetLabels(this);
        List<PackageInfo> list = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        PackageInfo[] packageInfos = list.toArray(new PackageInfo[list.size()]);
        loadTargetLabels.execute(packageInfos);

        if (Build.VERSION.SDK_INT < 26) {
            broadcastReceiver = new PkgChangeReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme("package");
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }



    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= 26) {
            ChangedPackages changes = packageManager.getChangedPackages(pkgChangeSequence);
            if (changes != null) {
                pkgChangeSequence = changes.getSequenceNumber();
                iconListAdapter.updateIconList();
//                iconListAdapter.checkRelease();
            }
        }

        Boolean hideOtherIcons = pref.getBoolean("showOnlyIconoir", false);
        if (hideOtherIcons) {
            ComponentName componentName = new ComponentName(this, "com.google.android.youtube");
            packageManager.setComponentEnabledSetting(componentName,packageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    packageManager.DONT_KILL_APP);
        }
        iconListAdapter.setShowAll(pref.getBoolean("showAllEnabled", false));
        super.onResume();
    }



    @Override
    public void onBackPressed()
    {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String callFromIcon = getIntent().getExtras().getString("callFromIcon", "");
            if (!callFromIcon.equals("")) {
                try {
                    Intent intent = packageManager.getLaunchIntentForPackage(
                            callFromIcon);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        }
        super.onBackPressed();
    }

    private void readSharedPreferences() {
        pref = getApplicationContext().getSharedPreferences(
                getString(R.string.sharedPrefLabel), MODE_PRIVATE | MODE_MULTI_PROCESS);
        editor = pref.edit();
        editor.apply();
    }

    private void addShowAllListener() {
        Switch showAll = (Switch) findViewById(R.id.switchShowAll);
        showAll.setChecked(pref.getBoolean("showAllEnabled", false));
        showAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iconListAdapter.setShowAll(isChecked);
                editor.putBoolean("showAllEnabled", isChecked);
                editor.apply();
            }
        });
    }

    //================================================================================
    // Icons and Targets
    //================================================================================

    public void loadIconList() {
        iconListView = (RecyclerView) findViewById(R.id.recyclerView);
        iconListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setItemPrefetchEnabled(false);
        iconListView.setLayoutManager(llm);

        iconListAdapter = new IconListAdapter(MainActivity.this);
        iconListAdapter.setHasStableIds(true);
        iconListView.setAdapter(iconListAdapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String targetPkg = data.getStringExtra("targetPkg");
                String iconPkg = iconListAdapter.updateTarget(targetPkg);
                editor.putString(iconPkg, targetPkg);
                editor.apply();
            }
        } else if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                optionMenu.findItem(R.id.alertWallpaper).setVisible(false);
            }
        }
    }

    private static class LoadTargetLabels extends AsyncTask<PackageInfo, Void, Map<String, String>> {
        private WeakReference<MainActivity> activityRef;

        public LoadTargetLabels(MainActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        protected Map<String, String> doInBackground(PackageInfo... pkgInfos) {
            Map<String, String> labelMap = new HashMap<>();
            if (activityRef != null) {
                for (PackageInfo pkgInfo : pkgInfos) {
                    String label = pkgInfo.applicationInfo.loadLabel(activityRef.get().packageManager).toString();
                    labelMap.put(pkgInfo.packageName, label);
                }
            }
            return labelMap;
        }

        @Override
        protected  void onPostExecute(Map<String, String> labelMap) {
            if (activityRef != null) {
                activityRef.get().labelMap = labelMap;
            }
        }
    }

    //================================================================================
    // Package Updates
    //================================================================================

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT < 26) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    public class PkgChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            iconListAdapter.updateIconList();
//            iconListAdapter.checkRelease();
        }
    }

    //================================================================================
    // Menu and Wallpaper
    //================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= 26) {
            menu.findItem(R.id.alertWallpaper).setVisible(!isWallpaperSet());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean isWallpaperSet() {
        Bitmap currentImg = drawableToBitmap(WallpaperManager.getInstance(this).getDrawable());
        Bitmap iconoirImg = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
        return imagesAreEqual(currentImg, iconoirImg);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alertWallpaper:
                startActivityForResult(new Intent( this, AlertSetWallpaperActivity.class ),
                        2);
                break;
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
}

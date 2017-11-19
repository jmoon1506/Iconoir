package com.iconoir.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RecyclerView iconListView;
    IconListAdapter iconListAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    PackageManager packageManager;
    Map<String, String> iconTargetMap;
    PkgChangeReceiver broadcastReceiver;
    Integer pkgChangeSequence = 0;

    private static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";
    private static final String SHARED_FOLDER = "shared";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.actionBarTitle);
        packageManager = getPackageManager();
        readSharedPreferences();

        loadIconList();
//        new LoadIconTask().execute();
        addShowAllListener();
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
                iconListAdapter.updateHiddenPositions();
            }
        }
        super.onResume();
    }

    public class PkgChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            iconListAdapter.updateHiddenPositions();
        }
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
        editor.putBoolean("showAllEnabled", false);
        editor.apply();
    }

    public void loadIconList() {
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
        iconListAdapter.updateHiddenPositions();

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String targetPkg = data.getStringExtra("targetPkg");
                String iconPkg = iconListAdapter.updateIconPackageMap(targetPkg);
                editor.putString(iconPkg, targetPkg);
                editor.apply();
            }
        }
    }


}

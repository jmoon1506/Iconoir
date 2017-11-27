package com.iconoir.settings;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ChangedPackages;
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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.actionBarTitle);
        packageManager = getPackageManager();
        readSharedPreferences();
        loadIconList();
        addShowAllListener();
    }

    @Override
    protected void onResume() {
        Boolean hideOtherIcons = pref.getBoolean("showOnlyIconoir", false);
        if (hideOtherIcons) {
            ComponentName componentName = new ComponentName(this, "com.google.android.youtube");
            packageManager.setComponentEnabledSetting(componentName,packageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    packageManager.DONT_KILL_APP);
        }
        iconListAdapter.setShowAll(pref.getBoolean("showAllEnabled", false));
        iconListAdapter.updateHiddenPositions();
        super.onResume();
    }

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

    public void loadIconList() {
        iconListView = (RecyclerView) findViewById(R.id.recyclerView);
        iconListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setItemPrefetchEnabled(false);
        iconListView.setLayoutManager(llm);

        iconListAdapter = new IconListAdapter(MainActivity.this);
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

    public boolean isWallpaperSet() {
        Bitmap currentImg = drawableToBitmap(WallpaperManager.getInstance(this).getDrawable());
        Bitmap iconoirImg = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper);
        return imagesAreEqual(currentImg, iconoirImg);
    }

    String convertStreamToString(java.io.InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

    private boolean isPackageReleased(String iconPkg) {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://www.android.com/");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Log.e("REQUEST", ">>>>>PRINTING<<<<<");
            Log.e("REQUEST", in.toString());
            Log.e("REQUEST", convertStreamToString(in));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
//        URL url;
//        HttpURLConnection urlConnection = null;
//        try {
//            url = new URL("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            Log.d("RESPONSE", Integer.toString(urlConnection.getResponseCode()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            urlConnection.disconnect();
//        }
        return false;
    }

    private boolean availableOnGooglePlay(final String packageName)
    {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("https://play.google.com/store/apps/details?id=" + packageName);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            final int responseCode = urlConnection.getResponseCode();
            Log.d("GOOGLEPLAY", "responseCode for " + packageName + ": " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) // code 200
            {
                return true;
            } else // this will be HttpURLConnection.HTTP_NOT_FOUND or code 404 if the package is not found
            {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return false;
    }

    class checkGooglePlay extends AsyncTask<String, Void, Boolean> {

        private Exception exception;

        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

            } catch (Exception e) {
                this.exception = e;
            }
            return false;
        }
    }
}

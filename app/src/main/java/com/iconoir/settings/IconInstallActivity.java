package com.iconoir.settings;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import static com.iconoir.settings.IconListAdapter.getDrawableId;

public class IconInstallActivity extends AppCompatActivity {
    PackageManager packageManager;
    String iconPkg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageManager = getPackageManager();
        setContentView(R.layout.activity_icon_install);
        addGooglePlayListener();
        setTitle("   ");
        setupActionBar();
        iconPkg = getIntent().getStringExtra("iconPkg");
        ImageView icon = (ImageView) findViewById(R.id.icon);
        int drawableId = getDrawableId(iconPkg);
        icon.setImageDrawable(getResources().getDrawable(drawableId));
        setSuggestedShortcuts(iconPkg);
    }

    public void addGooglePlayListener() {
        ImageButton btnGooglePlay = (ImageButton) findViewById(R.id.btnGooglePlay);
        btnGooglePlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.googlePlayPrefix) + iconPkg));
//                    intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(IconInstallActivity.this,
                            "Could not open Google Play", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setSuggestedShortcuts(String iconPkg) {

        Bundle bundle = getIntent().getExtras();
        List<String> validSystemPkgs = Arrays.asList(getResources().getStringArray(R.array.validSystemPackages));
        List<String> iconoirPkgs = Arrays.asList(getResources().getStringArray(R.array.iconoirPackages));
        String iconoirSettingsPkg = getResources().getString(R.string.iconoirSettingsPackage);
        Boolean labelsLoaded = bundle.getBoolean("labelsLoaded", false);
        findViewById(R.id.suggestions).setVisibility(View.VISIBLE);

        try {
            String iconLetter = iconPkg.substring(iconPkg.length() - 1).toLowerCase();
            if (!Character.isLetter(iconLetter.charAt(0))) {
                throw new Exception("iconPkg does not end in a letter");
            }
            int currentSuggestion = 1;
            List<PackageInfo> pkgInfos = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
            for (PackageInfo pkgInfo : pkgInfos) {
                String label;
                if (labelsLoaded) {
                    label = bundle.getString(pkgInfo.packageName);
                } else {
                    label = pkgInfo.applicationInfo.loadLabel(packageManager).toString();
                }
                if (label.length() < 1) continue;
                String pkgLetter = label.substring(0,1).toLowerCase();
                if (iconLetter.equals(pkgLetter)) {
                    if ((isSystemPackage(pkgInfo) && validSystemPkgs.contains(pkgInfo.packageName)) ||
                            (!isSystemPackage(pkgInfo) &&
                            !(iconoirPkgs.contains(pkgInfo.packageName) || iconoirSettingsPkg.equals(pkgInfo.packageName)))) {
                        setSuggestion(currentSuggestion, pkgInfo, label);
                        enableSuggestion(currentSuggestion, true);
                        currentSuggestion++;
                        if (currentSuggestion > 3) return;
                    }
                }
            }
            if (currentSuggestion == 1) {
                findViewById(R.id.suggestions).setVisibility(View.GONE);
            } else {
                while (currentSuggestion <= 3) {
                    enableSuggestion(currentSuggestion, false);
                    currentSuggestion++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSuggestion(Integer index, PackageInfo pkgInfo, String label) {
        ImageView iconView;
        TextView labelView;
        switch (index) {
            case 1: iconView = findViewById(R.id.suggestIcon1);
                    labelView = findViewById(R.id.suggestLabel1); break;
            case 2: iconView = findViewById(R.id.suggestIcon2);
                    labelView = findViewById(R.id.suggestLabel2); break;
            case 3: iconView = findViewById(R.id.suggestIcon3);
                    labelView = findViewById(R.id.suggestLabel3); break;
            default: return;
        }
        iconView.setImageDrawable(packageManager.getApplicationIcon(pkgInfo.applicationInfo));
        labelView.setText(label);
    }

    private void enableSuggestion(Integer index, Boolean enable) {
        LinearLayout suggestion;
        switch (index) {
            case 1: suggestion = findViewById(R.id.suggestion1); break;
            case 2: suggestion = findViewById(R.id.suggestion2); break;
            case 3: suggestion = findViewById(R.id.suggestion3); break;
            default: return;
        }
        if (enable) {
            suggestion.setVisibility(View.VISIBLE);
        } else {
            suggestion.setVisibility(View.GONE);
        }
    }

    public boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}

package com.iconoir.b;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CouldNotOpenActivity extends AppCompatActivity {
    private boolean hasRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_could_not_open);
        String targetPkg = getIntent().getExtras().getString("targetPkg", "");
        TextView header = (TextView) findViewById(R.id.couldNotOpen);
        TextView message = (TextView) findViewById(R.id.couldNotOpenMsg);
        String headerText, messageText;
        if (targetPkg.isEmpty()) {
            headerText = getString(R.string.noTargetSet);
            messageText = getString(R.string.noTargetSetMsg);
        } else {
            headerText = getString(R.string.couldNotOpen) + getPackageLabel(targetPkg);
            messageText = getString(R.string.couldNotOpenMsg);
        }
        header.setText(headerText);
        message.setText(messageText);
        addSettingsListener();
        hasRun = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (hasRun) {
            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overridePendingTransition(0, 0);
            startActivity(intent);

        } else {
            hasRun = true;
        }
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    public void addSettingsListener() {
        ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(
                            getString(R.string.iconoirSettingsPackage));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("callFromIcon", getApplicationContext().getPackageName());
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {

                    Toast.makeText(CouldNotOpenActivity.this,
                            "Could not open Iconoir Settings", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private String getPackageLabel(String pkgName) {
        try {
            ApplicationInfo app = getPackageManager().getApplicationInfo(pkgName, 0);
            return (String) getPackageManager().getApplicationLabel(app);
        } catch (PackageManager.NameNotFoundException e) {
            return pkgName;
        }
    }
}

package com.iconoir.b;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 1);
//            overridePendingTransition(0, 0);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } catch (ActivityNotFoundException e) {
            // Show a toast message on successful change
            Toast.makeText(MainActivity.this,
                    "Could not open YouTube", Toast.LENGTH_SHORT)
                    .show();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

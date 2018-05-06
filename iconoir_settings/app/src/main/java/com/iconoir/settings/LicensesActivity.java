package com.iconoir.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.aboutLicenses);
        setContentView(R.layout.activity_licenses);
    }
}

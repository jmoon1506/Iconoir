package com.iconoir.settings;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_about);
        setTitle(R.string.menuAbout);
        setupActionBar();
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);

        View aboutPage = new AboutPage(this)
                .setImage(R.drawable.ic_launcher_foreground)
                .setDescription(getString(R.string.aboutText))
                .addWebsite(getString(R.string.aboutWebsiteValue), getString(R.string.aboutWebsite))
                .addEmail(getString(R.string.aboutEmailValue), getString(R.string.aboutEmailValue))
                .addTwitter(getString(R.string.aboutTwitterValue), getString(R.string.aboutTwitter))
                .addInstagram(getString(R.string.aboutInstagramValue), getString(R.string.aboutInstagram))
                .addFacebook(getString(R.string.aboutFacebookValue), getString(R.string.aboutFacebook))
                .addPlayStore(getString(R.string.aboutPlayStoreValue), getString(R.string.aboutPlayStore))
                .addItem(getLicenses())
                .create();

        setContentView(aboutPage);
    }

    Element getLicenses() {
        Element licenses = new Element();
        licenses.setTitle(getString(R.string.aboutLicenses));
        licenses.setIconDrawable(android.R.drawable.ic_dialog_info);
        licenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LicensesActivity.class);
                startActivity(intent);
            }
        });
        return licenses;
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}

package com.iconoir.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class AdvancedActivity extends AppCompatPreferenceActivity {
    SharedPreferences pref;
    SharedPreferences.OnSharedPreferenceChangeListener prefListener;

//    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object value) {
////            String stringValue = value.toString();
//            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
//            if (value instanceof String) {
//                pref.edit().putString(preference.getKey(), value.toString()).apply();
//            } else if (value instanceof Boolean) {
//                pref.edit().putBoolean(preference.getKey(), (Boolean) value).apply();
//            }
////            putString("otherKey","value").commit();
////            if (preference instanceof ListPreference) {
////                // For list preferences, look up the correct display value in
////                // the preference's 'entries' list.
////                ListPreference listPreference = (ListPreference) preference;
////                int index = listPreference.findIndexOfValue(stringValue);
////
////                // Set the summary to reflect the new value.
////                preference.setSummary(
////                        index >= 0
////                                ? listPreference.getEntries()[index]
////                                : null);
////
////            } else {
////                // For all other preferences, set the summary to the value's
////                // simple string representation.
////                preference.setSummary(stringValue);
////            }
//            return true;
//        }
//    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     */

//    private static void bindPreferenceSummaryToValue(Preference preference) {
//        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//        // Trigger the listener immediately with the preference's
//        // current value.
//        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
//                PreferenceManager
//                        .getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), ""));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("setBackgroundColor")) {
                    pref.edit().putString("backgroundColor",
                            sharedPreferences.getString(key, "")).apply();
                } else if (key.equals("showOnlyIconoir")) {
                    pref.edit().putBoolean("showOnlyIconoir",
                            sharedPreferences.getBoolean(key, false)).apply();
                } else if (key.equals("showSystemPkgs")) {
                    pref.edit().putBoolean("showSystemPkgs",
                            sharedPreferences.getBoolean(key, false)).apply();
                }
            }
        };
        setupActionBar();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pref.registerOnSharedPreferenceChangeListener(prefListener);
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pref.unregisterOnSharedPreferenceChangeListener(prefListener);
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//                                          String key) {
//        if (key.equals("setBackgroundColor")) {
//            pref.edit().putString("backgroundColor", sharedPreferences.getString(key, "")).apply();
//        } else if (key.equals("showOnlyIconoir")) {
//            pref.edit().putBoolean("showOnlyIconoir", sharedPreferences.getBoolean(key, false)).apply();
//        } else if (key.equals("showSystemPkgs")) {
//            pref.edit().putBoolean("showSystemPkgs", sharedPreferences.getBoolean(key, false)).apply();
//        }
//        return;
//    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Remove the left caret
            getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || InterfacePreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows interface preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class InterfacePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_interface);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("setBackgroundColor"));
//            bindPreferenceSummaryToValue(findPreference("showOnlyIconoir"));
//            bindPreferenceSummaryToValue(findPreference("showSystemPkgs"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AdvancedActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}

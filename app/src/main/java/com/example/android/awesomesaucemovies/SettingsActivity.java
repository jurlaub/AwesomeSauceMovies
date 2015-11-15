package com.example.android.awesomesaucemovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by dev on 7/27/15.
 * from udacity -Android Development Sunshine app
 *
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //add 'general' preferences
        addPreferencesFromResource(R.xml.pref_general);

        // add preferences
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_order_key)));


    }


    /*
    Attach listener so that summary is always updated with the preference value

     */
    private void bindPreferenceSummaryToValue(Preference preference){
        // set listener
        preference.setOnPreferenceChangeListener(this);

        //trigger the listener immediately with preferences current value
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        Log.v("SettingActivity", "Preference is " + stringValue);

        if(preference instanceof ListPreference) {

            // for list preferences look up the correct display value
            // in the preference's 'entries' list - since they have separate label/values arrays.
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if(prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        } else {

            // for other preferences set summary to the values simple string representation
            preference.setSummary(stringValue);
        }



        return true;
    }




}

/*
 *  Copyright (C) 2020 Claus Niesen
 *
 *  This file is part of Claus' Morse Trainer.
 *
 *  Claus' Morse Trainer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Claus' Morse Trainer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Claus' Morse Trainer.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.niesens.morsetrainer;

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
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import com.niesens.morsetrainer.seekbarpreference.SeekBarPreference;

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
public class SettingsActivity extends AppPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

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
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || MorsePreferenceFragment.class.getName().equals(fragmentName)
                || AnswerPreferenceFragment.class.getName().equals(fragmentName)
                || UiPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MorsePreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_morse);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);
            switch (key) {
                case "morse_high_wpm":
                    ((SwitchPreference) preference).setChecked(getMorseHighWpmPreference(sharedPreferences));
                    if (getMorseHighWpmPreference(sharedPreferences)) {
                        ((SeekBarPreference) findPreference("morse_wpm")).setMaxValue(100);
                    } else {
                        ((SeekBarPreference) findPreference("morse_wpm")).setMaxValue(50);
                    }
                    break;
                case "morse_wpm" :
                    ((SeekBarPreference) preference).setCurrentValue(getMorseWpmPreference(sharedPreferences));
                    ((SeekBarPreference) findPreference("morse_farnsworth")).setMaxValue(getMorseWpmPreference(sharedPreferences));
                    break;
                case "morse_farnsworth_enabled" :
                    ((SwitchPreference) preference).setChecked(getMorseFarnsworthEnabledPreference(sharedPreferences));
                    break;
                case "morse_farnsworth" :
                    ((SeekBarPreference) preference).setCurrentValue(getMorseFarnsworthPreference(sharedPreferences));
                    break;
                case "morse_pitch" :
                    ((SeekBarPreference) preference).setCurrentValue(getMorsePitchPreference(sharedPreferences));
                    break;
                case "morse_random_pitch" :
                    ((SwitchPreference) preference).setChecked(getMorseRandomPitchPreference(sharedPreferences));
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            if (getMorseHighWpmPreference(getPreferenceScreen().getSharedPreferences())) {
                ((SeekBarPreference) findPreference("morse_wpm")).setMaxValue(100);
            } else {
                ((SeekBarPreference) findPreference("morse_wpm")).setMaxValue(50);
            }
            ((SeekBarPreference) findPreference("morse_farnsworth")).setMaxValue(getMorseWpmPreference(sharedPreferences));
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        private boolean getMorseHighWpmPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getBoolean("morse_high_wpm", getResources().getBoolean(R.bool.default_morse_high_wpm));
        }
        private int getMorseWpmPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getInt("morse_wpm", getResources().getInteger(R.integer.default_morse_wpm));
        }

        private boolean getMorseFarnsworthEnabledPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getBoolean("morse_farnsworth_enabled", getResources().getBoolean(R.bool.default_morse_farnsworth_enabled));
        }

        private int getMorseFarnsworthPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getInt("morse_farnsworth", getResources().getInteger(R.integer.default_morse_farnsworth));
        }

        private int getMorsePitchPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getInt("morse_pitch", getResources().getInteger(R.integer.default_morse_pitch));
        }

        private boolean getMorseRandomPitchPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getBoolean("morse_random_pitch", getResources().getBoolean(R.bool.default_morse_random_pitch));
        }

    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AnswerPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_answer);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);
            switch (key) {
                case "delay_before_answer" :
                    ((SeekBarPreference) preference).setCurrentValue(getDelayBeforeAnswerPreference(sharedPreferences));
                    break;
                case "delay_after_answer" :
                    ((SeekBarPreference) preference).setCurrentValue(getDelayAfterAnswerPreference(sharedPreferences));
                    break;
                case "answer_toast" :
                    ((SwitchPreference) preference).setChecked(getAnswerToastPreference(sharedPreferences));
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        private int getDelayBeforeAnswerPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getInt("delay_before_answer", getResources().getInteger(R.integer.default_delay_before_answer));
        }

        private int getDelayAfterAnswerPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getInt("delay_after_answer", getResources().getInteger(R.integer.default_delay_after_answer));
        }

        private boolean getAnswerToastPreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getBoolean("answer_toast", getResources().getBoolean(R.bool.default_answer_toast));
        }
    }

    /**
     * This fragment shows UI preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UiPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_ui);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);
            if ("ui_night_mode".equals(key)) {
                ((ListPreference) preference).setValue(getUiNightModePreference(sharedPreferences));
                if ("Yes".equals(getUiNightModePreference(sharedPreferences))) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        private String getUiNightModePreference(SharedPreferences sharedPreferences) {
            return sharedPreferences.getString("ui_night_mode", getResources().getString(R.string.default_ui_night_mode));
        }
    }

}

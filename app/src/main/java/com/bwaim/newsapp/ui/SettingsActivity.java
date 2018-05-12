/*
 *    Copyright 2018 Fabien Boismoreau
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.bwaim.newsapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bwaim.newsapp.R;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        private static final String OR_SEPARATOR = ", ";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference limit = findPreference(getString(R.string.settings_limit_key));
            bindPreferenceSummaryToValue(limit);

            Preference section = findPreference(getString(R.string.settings_section_key));
            bindPreferenceSummaryToValue(section);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            if (preference instanceof MultiSelectListPreference) {
                Set<String> sectionsSet = preferences.getStringSet(preference.getKey()
                        , null);
                onPreferenceChange(preference, sectionsSet);
            } else {
                String preferenceString = preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }

        /**
         * Called when a Preference has been changed by the user. This is
         * called before the state of the Preference is about to be updated and
         * before the state is persisted.
         *
         * @param preference The changed Preference.
         * @param newValue   The new value of the Preference.
         * @return True to update the state of the Preference with the new value.
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference instanceof MultiSelectListPreference) {
                @SuppressWarnings("unchecked")
                Set<String> values = (Set<String>) newValue;
                MultiSelectListPreference pref = (MultiSelectListPreference) preference;
                CharSequence[] entries = pref.getEntries();

                StringBuilder stringValue = new StringBuilder();
                boolean isFirst = true;
                for (String section : values) {
                    int index = pref.findIndexOfValue(section);
                    if (index >= 0) {
                        if (!isFirst) {
                            stringValue.append(OR_SEPARATOR);
                        } else {
                            isFirst = false;
                        }
                        stringValue.append(entries[index]);
                    }
                }
                preference.setSummary(stringValue.toString());
            } else {
                String valueString = (String) newValue;
                preference.setSummary(valueString);
            }

            return true;
        }
    }
}

/*
 * Copyright (C) 2016-2020 riceDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rice.settings.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.android.settings.R;

import java.util.List;
import java.util.ArrayList;

@SearchIndexable
public class About extends SettingsPreferenceFragment {

    public static final String TAG = "About";

    private String KEY_RICE_DONATE = "rice_donate";
    private String KEY_RICE_SOURCE = "rice_source";
    private String KEY_RICE_TELEGRAM = "rice_telegram";
    private String KEY_RICE_SHARE = "rice_share";
    private String KEY_RICE_TRANSLATE = "rice_translate";
    private String KEY_RICE_WEBSITE = "rice_website";
    private String KEY_RICE_TELEGRAM_CHANNEL = "rice_telegram_channel";
    private String KEY_RICE_SPONSOR = "rice_sponsor";

    private Preference mDonate;
    private Preference mSourceUrl;
    private Preference mTelegramUrl;
    private Preference mShare;
    private Preference mTranslate;
    private Preference mWebsite;
    private Preference mTelegramChannelUrl;
    private Preference mSponsor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rice_settings_about);

        mDonate = findPreference(KEY_RICE_DONATE);
        mSourceUrl = findPreference(KEY_RICE_SOURCE);
        mTelegramUrl = findPreference(KEY_RICE_TELEGRAM);
        mShare = findPreference(KEY_RICE_SHARE);
        mTranslate = findPreference(KEY_RICE_TRANSLATE);
        mWebsite = findPreference(KEY_RICE_WEBSITE);
        mTelegramChannelUrl = findPreference(KEY_RICE_TELEGRAM_CHANNEL);
        mSponsor = findPreference(KEY_RICE_SPONSOR);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mDonate) {
            launchUrl("https://www.youtube.com/watch?v=Bgqk6t9Be1Q");
        } else if (preference == mSourceUrl) {
            launchUrl("https://github.com/RiceDroid");
        } else if (preference == mTelegramUrl) {
            launchUrl("https://t.me/riceDroidNews");
        } else if (preference == mShare) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, String.format(
                    getActivity().getString(R.string.share_message), Build.MODEL));
            startActivity(Intent.createChooser(intent, getActivity().getString(R.string.share_chooser_title)));
        } else if (preference == mTranslate) {
            launchUrl("https://crdroid.net/translations.php");
        } else if (preference == mWebsite) {
            launchUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        } else if (preference == mTelegramChannelUrl) {
            launchUrl("https://t.me/riceDroidNews");
        } else if (preference == mSponsor) {
            launchUrl("https://www.scopehosts.com");
        }

        return super.onPreferenceTreeClick(preference);
    }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
        getActivity().startActivity(intent);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CRDROID_SETTINGS;
    }

    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.rice_settings_about);
}

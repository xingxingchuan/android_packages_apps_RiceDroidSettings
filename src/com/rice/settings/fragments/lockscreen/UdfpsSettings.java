/*
 * Copyright (C) 2022 riceDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rice.settings.fragments.lockscreen;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.crdroid.Utils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class UdfpsSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String UDFPS_ANIM_PREVIEW = "udfps_recognizing_animation_preview";
    private static final String SCREEN_OFF_UDFPS = "screen_off_udfps";
    private static final String UDFPS_HAPTIC_FEEDBACK = "udfps_haptic_feedback";

    private Preference mUdfpsAnimPreview;
    private Preference mScreenOffUdfps;
    private Preference mUdfpsHapticFeedback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.udfps_settings);

        final PreferenceScreen prefSet = getPreferenceScreen();
        Resources resources = getResources();

        final boolean udfpsResPkgInstalled = Utils.isPackageInstalled(getContext(),
                "com.crdroid.udfps.animations");
        mUdfpsAnimPreview = findPreference(UDFPS_ANIM_PREVIEW);
        mUdfpsHapticFeedback = findPreference(UDFPS_HAPTIC_FEEDBACK);
        if (!udfpsResPkgInstalled) {
            prefSet.removePreference(mUdfpsAnimPreview);
        }

        mScreenOffUdfps = (Preference) prefSet.findPreference(SCREEN_OFF_UDFPS);
        boolean mScreenOffUdfpsAvailable = resources.getBoolean(
                com.android.internal.R.bool.config_supportScreenOffUdfps);
        if (!mScreenOffUdfpsAvailable)
            prefSet.removePreference(mScreenOffUdfps);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
    	final ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mUdfpsHapticFeedback) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.UDFPS_HAPTIC_FEEDBACK, value ? 1 : 0);
            return true;
        }
        return false;
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putIntForUser(resolver,
                Settings.System.UDFPS_ANIM, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.UDFPS_ANIM_STYLE, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.UDFPS_ICON, 0, UserHandle.USER_CURRENT);
        Settings.System.putIntForUser(resolver,
                Settings.System.SCREEN_OFF_UDFPS, 0, UserHandle.USER_CURRENT);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CRDROID_SETTINGS;
    }
}

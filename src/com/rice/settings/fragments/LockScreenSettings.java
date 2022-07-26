/*
 * Copyright (C) 2016-2022 riceDroid Android Project
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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.crdroid.Utils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.rice.settings.fragments.lockscreen.UdfpsSettings;
import com.rice.settings.preferences.SystemSettingSwitchPreference;

import java.util.List;

import lineageos.providers.LineageSettings;

@SearchIndexable
public class LockScreenSettings extends SettingsPreferenceFragment
            implements Preference.OnPreferenceChangeListener  {

    public static final String TAG = "LockScreenSettings";

    private static final String LOCKSCREEN_INTERFACE_CATEGORY = "lockscreen_interface_category";
    private static final String LOCKSCREEN_GESTURES_CATEGORY = "lockscreen_gestures_category";
    private static final String KEY_UDFPS_SETTINGS = "udfps_settings";
    private static final String KEY_FP_SUCCESS_VIBRATE = "fp_success_vibrate";
    private static final String KEY_FP_ERROR_VIBRATE = "fp_error_vibrate";
    private static final String KEY_FP_WAKE_UNLOCK = "fp_wake_unlock";
    private static final String KEY_RIPPLE_EFFECT = "enable_ripple_effect";
    private static final String AOD_SCHEDULE_KEY = "always_on_display_schedule";
    private static final String SECONDARY_COLOR_CLOCK = "use_secondary_color_clock";
    
    static final int MODE_DISABLED = 0;
    static final int MODE_NIGHT = 1;
    static final int MODE_TIME = 2;
    static final int MODE_MIXED_SUNSET = 3;
    static final int MODE_MIXED_SUNRISE = 4;
    
    Preference mAODPref;
    private Preference mUdfpsSettings;
    private Preference mFingerprintVib;
    private Preference mFingerprintVibErr;
    private Preference mRippleEffect;

    private SwitchPreference mFingerprintWakeUnlock;
    private SystemSettingSwitchPreference mSecondaryColorClock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.rice_settings_lockscreen);

        PreferenceCategory interfaceCategory = (PreferenceCategory) findPreference(LOCKSCREEN_INTERFACE_CATEGORY);
        PreferenceCategory gestCategory = (PreferenceCategory) findPreference(LOCKSCREEN_GESTURES_CATEGORY);

        FingerprintManager mFingerprintManager = (FingerprintManager)
                getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mUdfpsSettings = (Preference) findPreference(KEY_UDFPS_SETTINGS);
        mFingerprintVib = (Preference) findPreference(KEY_FP_SUCCESS_VIBRATE);
        mFingerprintVibErr = (Preference) findPreference(KEY_FP_ERROR_VIBRATE);
        mFingerprintWakeUnlock = (SwitchPreference) findPreference(KEY_FP_WAKE_UNLOCK);
        mRippleEffect = (Preference) findPreference(KEY_RIPPLE_EFFECT);

        mAODPref = findPreference(AOD_SCHEDULE_KEY);
        updateAlwaysOnSummary();

        if (mFingerprintManager == null || !mFingerprintManager.isHardwareDetected()) {
            interfaceCategory.removePreference(mUdfpsSettings);
            gestCategory.removePreference(mFingerprintVib);
            gestCategory.removePreference(mFingerprintVibErr);
            gestCategory.removePreference(mFingerprintWakeUnlock);
            gestCategory.removePreference(mRippleEffect);
        } else {
            if (!Utils.isPackageInstalled(getContext(), "com.crdroid.udfps.icons")) {
                interfaceCategory.removePreference(mUdfpsSettings);
            }
            if (!mFingerprintManager.isPowerbuttonFps()) {
                gestCategory.removePreference(mFingerprintWakeUnlock);
            } else {
                boolean fpWakeUnlockEnabledDef = getContext().getResources().getBoolean(
                        com.android.internal.R.bool.config_fingerprintWakeAndUnlock);
                boolean fpWakeUnlockEnabled = Settings.System.getIntForUser(
                        getContext().getContentResolver(), Settings.System.FP_WAKE_UNLOCK,
                        fpWakeUnlockEnabledDef ? 1 : 0, UserHandle.USER_CURRENT) != 0;
                mFingerprintWakeUnlock.setChecked(fpWakeUnlockEnabled);
            }
        }
        mSecondaryColorClock = (SystemSettingSwitchPreference) findPreference(SECONDARY_COLOR_CLOCK);
        mSecondaryColorClock.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAlwaysOnSummary();
    }

    private void updateAlwaysOnSummary() {
        if (mAODPref == null) return;
        int mode = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.DOZE_ALWAYS_ON_AUTO_MODE, 0, UserHandle.USER_CURRENT);
        switch (mode) {
            default:
            case MODE_DISABLED:
                mAODPref.setSummary(R.string.disabled);
                break;
            case MODE_NIGHT:
                mAODPref.setSummary(R.string.night_display_auto_mode_twilight);
                break;
            case 2:
                mAODPref.setSummary(R.string.night_display_auto_mode_custom);
                break;
            case MODE_MIXED_SUNSET:
                mAODPref.setSummary(R.string.always_on_display_schedule_mixed_sunset);
                break;
            case MODE_MIXED_SUNRISE:
                mAODPref.setSummary(R.string.always_on_display_schedule_mixed_sunrise);
                break;
        }
   }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
    	if (preference == mSecondaryColorClock) {
            Utils.showSysUIRestartDialog(getContext());
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CRDROID_SETTINGS;
    }

    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.rice_settings_lockscreen) {

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);

                    FingerprintManager mFingerprintManager = (FingerprintManager)
                            context.getSystemService(Context.FINGERPRINT_SERVICE);
                    if (mFingerprintManager == null || !mFingerprintManager.isHardwareDetected()) {
                        keys.add(KEY_UDFPS_SETTINGS);
                        keys.add(KEY_FP_SUCCESS_VIBRATE);
                        keys.add(KEY_FP_ERROR_VIBRATE);
                        keys.add(KEY_FP_WAKE_UNLOCK);
                        keys.add(KEY_RIPPLE_EFFECT);
                    } else {
                        if (!Utils.isPackageInstalled(context, "com.crdroid.udfps.icons")) {
                            keys.add(KEY_UDFPS_SETTINGS);
                        } else {
                            keys.add(KEY_FP_SUCCESS_VIBRATE);
                            keys.add(KEY_FP_ERROR_VIBRATE);
                        }
                        if (!mFingerprintManager.isPowerbuttonFps()) {
                            keys.add(KEY_FP_WAKE_UNLOCK);
                        }
                    }

                    return keys;
                }
            };
}

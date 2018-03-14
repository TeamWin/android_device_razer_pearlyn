/*
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

package com.cyanogenmod.cmactions;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.os.SystemProperties;
import android.util.Log;
import java.io.*;
import java.util.prefs.*;

public class PreferencesFragment extends PreferenceFragment {

    private static final String DEVICE_MODE_KEY = "device_mode";
    private static final String DEVICE_MODE_PROPERTY = "persist.sys.rzr.device_mode";
    private static final String OVERSCAN_KEY = "overscan";

    private SwitchPreference mDeviceModePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        // Device Mode PReference
        boolean DeviceModeEnabled = isDeviceModeEnabled();
        mDeviceModePreference =
            (SwitchPreference) findPreference(DEVICE_MODE_KEY);
        mDeviceModePreference.setChecked(DeviceModeEnabled);
        mDeviceModePreference.setOnPreferenceChangeListener(mDeviceModePrefListener);
        
        // Overscan Preference
        Preference mOverscanPreference = findPreference(OVERSCAN_KEY);
        mOverscanPreference.setOnPreferenceClickListener(mOverscanPrefListener);
        
	}
    
        // Get the system property
    public static Boolean isDeviceModeEnabled() {
    InputStreamReader in = null;
    BufferedReader reader = null;
    try {
        Process proc = Runtime.getRuntime().exec(new String[]{"/system/bin/getprop", "persist.sys.rzr.device_mode"});
        in = new InputStreamReader(proc.getInputStream());
        reader = new BufferedReader(in);
        Boolean b = Boolean.valueOf(reader.readLine());
        return b;
    } catch (IOException e) {
        return null;
    } finally {
        closeQuietly(in);
        closeQuietly(reader);
    }
	}

	public static void closeQuietly(Closeable closeable) {
    if (closeable == null) return;
    try {
        closeable.close();
    } catch (IOException ignored) {
    }
	}
	
	// Set the system property
	private void setsysprop(String value) {
	try {
	String[] cmd = { "/system/bin/sh", "-c", "setprop persist.sys.rzr.device_mode " + value};	
    Runtime.getRuntime().exec(cmd);
	} catch (java.io.IOException e) {
	}
	}
  
    
    private void handleDeviceMode(String value) {
    try {
			String f = "/proc/usb_device";
			FileWriter w = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(w);
			PrintWriter wr = new PrintWriter(bw);
			wr.write(value); 
			wr.close();
			bw.close();
            } catch(IOException e) {
            e.printStackTrace();
			}
    }


    private Preference.OnPreferenceChangeListener mDeviceModePrefListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
             if (newValue.toString().equals("true")) {
				handleDeviceMode("0");
				setsysprop("true");
				Log.i("Device Mode","On");
			} else {
			handleDeviceMode("1");
			setsysprop("false");
			Log.i("Device Mode","Off");
			}
			return true;
        }
    };
    
    
    private Preference.OnPreferenceClickListener mOverscanPrefListener = new Preference.OnPreferenceClickListener() {
    @Override
    public boolean onPreferenceClick(Preference preference) {
       launchoverscan();
       return true;
    }
	};

    private void launchoverscan() {
		try {
		String[] cmd = { "/system/bin/sh", "-c", "am start -n com.google.android.tungsten.overscan/.CalibratorActivity" };	
		Runtime.getRuntime().exec(cmd);
		} catch (java.io.IOException e) {}
		}
}

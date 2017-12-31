package pt.isel.pdm.li51n.g4.tmdbisel.presentation.settings;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.content.LocalBroadcastManager;

import pt.isel.pdm.li51n.g4.tmdbisel.R;
import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.presentation.base.LoggingPreferenceFragment;
import pt.isel.pdm.li51n.g4.tmdbisel.services.AppSettingsChangedReceiver;

public class SettingsFragment extends LoggingPreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private static final String IGNORED_STRING = "OUCH!";
    private static final int IGNORED_INT = 9999;

    private static final int TWO_TENTH = 1024;

    private static final int MINUTES_IN_HOURS = 60;
    private static final int MINUTES_TO_MILLIS = 60*1000;

    private String syncFreq_key;
    private String imgCache_key;
    private String dataCache_key;

    private IntentFilter intentFilter;
    private AppSettingsChangedReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from the correspondent XML resource file
        addPreferencesFromResource(R.xml.preferences);

        syncFreq_key = getString(R.string.pref_syncFreq_key);
        imgCache_key = getString(R.string.pref_imgMem_key);
        dataCache_key = getString(R.string.pref_dataMem_key);

        SharedPreferences sharedPrefs = getPreferenceScreen().getSharedPreferences();
        //Convert minutes to hours before update it
        String valueStr = sharedPrefs.getString(syncFreq_key, IGNORED_STRING);
        valueStr = Integer.toString(Integer.parseInt(valueStr) / MINUTES_IN_HOURS);
        updateSummary(syncFreq_key, valueStr);
        updateSummary(imgCache_key, String.valueOf(sharedPrefs.getInt(imgCache_key, IGNORED_INT)));
        updateSummary(dataCache_key, String.valueOf(sharedPrefs.getInt(dataCache_key, IGNORED_INT)));
    }

    @Override
    public void onResume() {
        super.onResume();

        // Define intent filter and broadcast receiver for settings change and register both on local broadcast manager
        intentFilter = new IntentFilter(AppSettingsChangedReceiver.INTENT_FILTER_ID);
        receiver = new AppSettingsChangedReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, intentFilter);

        // Register listener for changed shared preferences
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister listener for change shared preferences
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        // Unregister broadcast receiver for settings change
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Intent intent;
        // Get preference key string ID
        int keyStrId = getResources().getIdentifier(key, "string", getActivity().getPackageName());
        switch (keyStrId) {
            case R.string.pref_notify_key:
                Logger.d(TAG, "DEBUG: Notifications status changed!");

                /* NOT USED
                intent = new Intent(AppSettingsChangedReceiver.INTENT_FILTER_ID);
                intent.putExtra(AppSettingsChangedReceiver.NOTIFY_SETTING, sharedPreferences.getBoolean(key, false));
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                */

                break;
            case R.string.pref_syncFreq_key:
                Logger.d(TAG, "DEBUG: Synchronization frequency changed!");

                //Convert minutes to hours before update it
                String valueStr = sharedPreferences.getString(key, IGNORED_STRING);
                valueStr = Integer.toString(Integer.parseInt(valueStr) / MINUTES_IN_HOURS);
                updateSummary(key, valueStr);

                int freqInMillis = Integer.parseInt(valueStr) * MINUTES_TO_MILLIS; //convert to milliseconds
                intent = new Intent(AppSettingsChangedReceiver.INTENT_FILTER_ID);
                intent.putExtra(AppSettingsChangedReceiver.SYNC_FREQ_SETTING, freqInMillis);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                Logger.d(TAG, "DEBUG: New synchronization frequency (in minutes) = " + valueStr);
                break;
            case R.string.pref_wifiSync_key:
                Logger.d(TAG, "DEBUG: Synchronization mode changed!");

                intent = new Intent(".sharedPrefs.Wifi");
                this.getActivity().sendBroadcast(intent);

                boolean wifiSyncStatus =  sharedPreferences.getBoolean(key, true);
                Logger.d(TAG, "DEBUG: WiFi sync only status: " + wifiSyncStatus);
                break;
            case R.string.pref_imgMem_key:
                Logger.d(TAG, "DEBUG: Image cache size changing!");

                int newImgCacheSize = sharedPreferences.getInt(key, IGNORED_INT); // In KiB
                //Update value only it new size can be set
                if (TMDbISELApplication.sImgCache.setSize(newImgCacheSize * TWO_TENTH) != newImgCacheSize || newImgCacheSize == 0)
                    updateSummary(key, String.valueOf(newImgCacheSize));
                else
                    Logger.d(TAG, "DEBUG: Could not set new cache size!");

                Logger.d(TAG, "DEBUG: Current cache size = " + TMDbISELApplication.sImgCache.currentSize());
                Logger.d(TAG, "DEBUG: Is cache open? " + TMDbISELApplication.sImgCache.containsKey("DUMMY"));
                break;
            case R.string.pref_dataMem_key:
                Logger.d(TAG, "DEBUG: Synchronization data cache changed!");

                int newDataCacheSize = sharedPreferences.getInt(key, IGNORED_INT);
                updateSummary(key, String.valueOf(newDataCacheSize));

                intent = new Intent(AppSettingsChangedReceiver.INTENT_FILTER_ID);
                intent.putExtra(AppSettingsChangedReceiver.CACHE_MEM_SETTING, newDataCacheSize);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                break;
            default:
                Logger.d(TAG, "DEBUG: Preference Settings change not catched!");
                break;
        }
    }

    private void updateSummary(String prefKey, String valueStr) {
        Preference pref = findPreference(prefKey);              // get preference
        String newSummary = pref.getSummary().toString();       // get current preference summary
        newSummary = newSummary.replaceFirst("\\d+", valueStr); // update value in summary
        pref.setSummary(newSummary);                            // update preference summary
    }
}

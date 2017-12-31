package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class AppSettingsChangedReceiver extends BroadcastReceiver {

    private static final String TAG = AppSettingsChangedReceiver.class.getSimpleName();

    public static final String INTENT_FILTER_ID = ".appSettingChanged";
    public static final String NOTIFY_SETTING = ".notify";
    public static final String SYNC_FREQ_SETTING = ".syncFreq";
    public static final String CACHE_MEM_SETTING = ".cacheMem";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "==========================================> Preference settings changed");

        int freqInMillis = intent.getIntExtra(SYNC_FREQ_SETTING, 0);

        if (freqInMillis != 0)
            Utils.SetAlarmManagerForSyncService(context, freqInMillis);
    }
}

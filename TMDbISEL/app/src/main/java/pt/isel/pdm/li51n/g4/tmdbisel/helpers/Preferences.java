package pt.isel.pdm.li51n.g4.tmdbisel.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import pt.isel.pdm.li51n.g4.tmdbisel.R;

public class Preferences {

    public static final int MAX_MOVIES = 600;
    private static final String DEFAULT_SYNC_FREQ = "24"; // In hours
    private static final int DEFAULT_IMG_CACHE_SIZE = 1024 * 1024; // 1MiB in bytes
    private static final int MINUTES_TO_MILLIS = 60*1000;

    public static void init(Context ctx) {
        PreferenceManager.setDefaultValues(ctx, R.xml.preferences, false);
    }

    //}
    public static boolean getNotificationActionStatus(Context ctx) {
        String notifyTag = ctx.getString(R.string.pref_notify_key);
        return getDefPrefs(ctx).getBoolean(notifyTag, false);
    }

    public static int getSyncFrequency(Context ctx) { // In hours
        final String freqTag = ctx.getString(R.string.pref_syncFreq_key);
        final String freqStr = getDefPrefs(ctx).getString(freqTag, DEFAULT_SYNC_FREQ);
        return Integer.parseInt(freqStr);
    }
    public static int getSyncFrequencyInMillis(Context ctx) {
        final String freqTag = ctx.getString(R.string.pref_syncFreq_key);
        final String freqStr = getDefPrefs(ctx).getString(freqTag, DEFAULT_SYNC_FREQ);
        return Integer.parseInt(freqStr) * MINUTES_TO_MILLIS;
    }

    public static boolean getOnWifiOnlyStatus(Context ctx) {
        final String wifiTag = ctx.getString(R.string.pref_wifiSync_key);
        return getDefPrefs(ctx).getBoolean(wifiTag, true);
    }

    public static int getImgCacheSize(Context ctx) {
        return getDefPrefs(ctx)
                .getInt(ctx.getString(R.string.pref_imgMem_key), DEFAULT_IMG_CACHE_SIZE);
    }

    public static int getDataCacheSize(Context ctx) {
        return getDefPrefs(ctx)
                .getInt(ctx.getString(R.string.pref_dataMem_key), getDefaultDataCacheSize());
    }

    public static void clearAll(Context ctx) {
        getDefPrefs(ctx).edit().clear().commit();
    }

    private static int getDefaultDataCacheSize() {
        //use 25% of available heap size
        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        // Use 1/8th of the available memory for this memory cache.
        return maxMemory / 8 / 1024;
    }

    private static SharedPreferences getDefPrefs(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
}

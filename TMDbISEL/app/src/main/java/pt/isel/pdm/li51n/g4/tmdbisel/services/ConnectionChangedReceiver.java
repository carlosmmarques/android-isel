package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Preferences;

public class ConnectionChangedReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectionChangedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "================================================> Connectivity Changed");

        Intent service = SyncService.newInstance(context);
        context.startService(service);
    }
}

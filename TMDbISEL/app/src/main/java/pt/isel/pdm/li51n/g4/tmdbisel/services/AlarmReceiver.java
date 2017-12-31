package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "================================================> AlarmReceiver wokeup");

        Intent service = SyncService.newInstance(context);
        service.putExtra(Constants.ALARM_MANAGER, true);
        context.startService(service);
    }
}

package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public abstract class TMDbService extends IntentService{

    /**
     * Creates an IntentService.  Invoked by static Factory Method
     *
     */
    public TMDbService(){
        super("TMDbService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TMDbService(String name) {
        super(name);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    public void onHandleIntent(Intent intent) {
        if(Utils.canSync(intent, getApplicationContext())) {
            doWork(intent);
        }
    }

    protected abstract void doWork(Intent intent);
}

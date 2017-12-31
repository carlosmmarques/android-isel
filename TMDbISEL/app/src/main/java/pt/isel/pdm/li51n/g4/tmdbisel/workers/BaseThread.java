package pt.isel.pdm.li51n.g4.tmdbisel.workers;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class BaseThread extends HandlerThread {

    Handler mWorkerHandler;
    Handler mResponseHandler;
    Map<String, View> mRequestMap = new ConcurrentHashMap<>();

    BaseThread(String name, Handler responseHandler) {
        super(name);
        mResponseHandler = responseHandler;
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                handleMessageInternal(msg);
                return true;
            }
        });
    }


    abstract void handleMessageInternal(Message message);

}

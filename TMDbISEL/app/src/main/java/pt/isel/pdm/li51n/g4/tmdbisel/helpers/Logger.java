package pt.isel.pdm.li51n.g4.tmdbisel.helpers;

import android.content.Intent;
import android.util.Log;

import pt.isel.pdm.li51n.g4.tmdbisel.BuildConfig;

public class Logger {

    public static final String LOG_TAG = "PDM_SE3-";

    public static Intent d(String tag, String message) {
        if(BuildConfig.DEBUG){
            Log.d(LOG_TAG+tag, message);
        }
        return null;
    }

    public static void i(String tag, String message){
        if(BuildConfig.DEBUG){
            Log.i(LOG_TAG + tag, message);
        }
    }

    public static void e(String tag, Exception message){
        if(BuildConfig.DEBUG){
            message.printStackTrace();
        }
        Log.e(LOG_TAG + tag, message.getMessage() == null ? "No details on this Exception, sorry!" : message.getMessage());
    }

    public static void e(Exception message) {
        if (BuildConfig.DEBUG) {
            message.printStackTrace();
        }
        Log.e(LOG_TAG, message.getMessage() == null ? "No details on this Exception, sorry!" : message.getMessage());
    }

    public static void w(String tag, String message){
        Log.w(LOG_TAG + tag, message);
    }

    public static void v(String tag, String message){
        Log.v(LOG_TAG+tag, message);
    }
}

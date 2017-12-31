package pt.isel.pdm.li51n.g4.tmdbisel.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CustomConnectivityManager {

    private static CustomConnectivityManager instance = null;
    private final ConnectivityManager connMngr;

    private CustomConnectivityManager(Context context) {
        connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static CustomConnectivityManager createInstance(Context context) {
        if (instance == null)
            instance = new CustomConnectivityManager(context);
        return instance;
    }

    public static CustomConnectivityManager getInstance(Context context) {
        if (instance == null)
            instance = new CustomConnectivityManager(context);
        return instance;
    }

    public boolean hasAnyActiveConnection() {
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    public boolean hasActiveWifi() {
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();
        return (netInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public boolean hasActiveMobile() {
        NetworkInfo netInfo = connMngr.getActiveNetworkInfo();
        return (netInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

}

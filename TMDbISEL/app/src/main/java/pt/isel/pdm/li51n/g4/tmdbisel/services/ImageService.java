package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbWebApi;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class ImageService extends TMDbService {

    private static final String TAG = ImageService.class.getSimpleName();
    public static final String EXTRA_IMAGE_URL = TAG + ".image_url";
    public static final String EXTRA_RESULT = TAG + ".RESULT";
    public static final String NOTIFICATION = TAG + ".service.receiver";
    private static final String EXTRA_IMAGE_TYPE = TAG + ".image_type";
    private String imageURL;
    private int result = Activity.RESULT_CANCELED;

    /**
     * Creates an IntentService.  Invoked by static Factory Method
     *
     */
    public ImageService() {
        super("ImageService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ImageService(String name) {
        super(name);
    }

    public static Intent newInstance(Context context, String url, int type) {
        return new Intent(context, ImageService.class)
                .putExtra(EXTRA_IMAGE_URL, url)
                .putExtra(EXTRA_IMAGE_TYPE, type);
    }

    @Override
    protected void doWork(Intent intent) {
        Logger.d(TAG, "Main process ID: " + android.os.Process.myPid() + " and thread ID: " + android.os.Process.myTid());

        Utils.assertNotOnUIThread();

        imageURL = intent.getStringExtra(EXTRA_IMAGE_URL);
        int type = intent.getIntExtra(EXTRA_IMAGE_TYPE, 0);
        if (imageURL == null) return;

        String key;
        switch (type) {
            case Constants.BACKDROP_IMAGE:
                key = TMDbWebApi.BACKDROP_BASE_URL + imageURL;
                break;
            case Constants.POSTER_IMAGE:
                key = TMDbWebApi.IMAGE_BASE_URL + imageURL;
                break;
            default:
                key = TMDbWebApi.IMAGE_BASE_URL + imageURL;
                break;
        }

        Logger.d(TAG, key);

        try { // Get image from HTTP source
            HttpURLConnection connection = (HttpURLConnection) new URL(key).openConnection();
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent());
            TMDbISELApplication.sImgCache.addBitmapToCache(imageURL.substring(1), bitmap);                // cache image
        } catch (IOException failedHttpDownload) {
            Logger.d(TAG, "Could not download image by HTTP - " + failedHttpDownload.getMessage());
            return;
        }
        result = Activity.RESULT_OK;
        notifyActivity();
    }

    private void notifyActivity(){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(EXTRA_RESULT, result);
        intent.putExtra(EXTRA_IMAGE_URL, imageURL);
        sendBroadcast(intent);
    }

}

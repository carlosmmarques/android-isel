package pt.isel.pdm.li51n.g4.tmdbisel.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.widget.ImageView;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import pt.isel.pdm.li51n.g4.tmdbisel.BuildConfig;
import pt.isel.pdm.li51n.g4.tmdbisel.R;
import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;
import pt.isel.pdm.li51n.g4.tmdbisel.services.AlarmReceiver;
import pt.isel.pdm.li51n.g4.tmdbisel.services.ImageService;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    private static final int DELAY_IN_MILLIS = 50;

    public static boolean canSync(Intent intent, Context context){
        boolean noConnectivity = intent.getBooleanExtra(
                ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
        );

        if (noConnectivity) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // only when connected or while connecting....
        if(netInfo == null || !netInfo.isConnectedOrConnecting()){
            return false;
        }

        boolean updateOnlyOnWifi = Preferences.getOnWifiOnlyStatus(context);

        return ((netInfo.getType() == ConnectivityManager.TYPE_MOBILE) && !updateOnlyOnWifi)
                || (netInfo.getType() == ConnectivityManager.TYPE_WIFI);

    }

    public static void sendEmailMessage(Context context, String subject, String body, String chooserTitle, String[] to) {
        Intent mailIntent = new Intent();
        mailIntent.setAction(Intent.ACTION_SEND);
        mailIntent.setType("message/rfc822");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        mailIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(mailIntent, chooserTitle));
        Logger.i(TAG, "[Notification] Sending email notification");
    }

    public static void assertNotOnUIThread() {
        if (BuildConfig.DEBUG && isUiThread()) {
            throw new IllegalThreadStateException("This should not be Running on the UI thread!");
        }
    }

    private static boolean isUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static File getCacheDir(Context context) {
        //Find the dir to save cached images
        File res;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            res = new File(android.os.Environment.getExternalStorageDirectory(), context.getPackageName());
        res=context.getCacheDir();
        Logger.i(TAG, "[Disk] Cache folder:" + res.getAbsolutePath());
        return res;

    }

    public synchronized static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public synchronized static boolean isSinglePane(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    public synchronized static String getLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public static String getFileNameFromURL(final String key) {
        String fileName = key;
        if (key.contains(":") || key.contains("\\") || key.contains("/")) {
            fileName = key.replaceAll(".*[:\\/]", "");
        }
        Logger.d(TAG, "Name from URL: " + fileName);
        return fileName;
    }

    public static void setImageFromCacheToImageView(Context context, ImageView view, String filename, int type){

        if (filename != null && filename.contains(".jpg")) {
            view.setTag(filename.substring(1));
            Bitmap bitmap = getImageFromCache(filename.substring(1));
            if(bitmap != null){
                view.setImageBitmap(bitmap);
            } else {
                switch (type){
                    case Constants.POSTER_IMAGE:
                        view.setImageResource(R.drawable.now_playing);
                        break;
                    default:
                        break;
                }
                synchronized (context) {
                    TMDbISELApplication.sImageViewsToUpdate.put(filename.substring(1), view);
                    sendMessageToImageService(context, filename, type);
                }
            }
        } else {
            switch (type) {
                case Constants.BACKDROP_IMAGE:
                    view.setImageResource(R.drawable.default_backdrop_2);
                    break;
                default:
                    view.setImageResource(R.drawable.now_playing);
                    break;
            }
        }
    }

    /**
     * Gets images from cache
     *
     * @param filename Name of the image file on cache
     * @return image
     */
    private static Bitmap getImageFromCache(String filename) {
        return TMDbISELApplication.sImgCache.getBitmapFromCache(filename);
    }

    private static void sendMessageToImageService(Context context, String filename, int type) {
        Intent intent = ImageService.newInstance(context, filename, type);
        context.startService(intent);
    }

    /**
     * Assigns the image to the requested ImageView
     * @param url Image URL
     * @param view ImageView
     * @param bitmap Image
     */
    public static void updateImageView(String url, ImageView view, Bitmap bitmap) {
        if (view == null || bitmap == null)
            return;
        String currentUrl = (String) view.getTag();
        if (!url.equals(currentUrl)) {
            Logger.i(TAG, "View already reused for url: " + url);
        } else {
            view.setImageBitmap(bitmap);
        }
    }

    public static ContentValues getFieldNamesAndValues(final Object valueObj) throws IllegalArgumentException,
            IllegalAccessException {
        Class c1 = valueObj.getClass();

        ContentValues values = new ContentValues();

        Field[] valueObjFields = c1.getDeclaredFields();

        // compare values now
        for (Field valueObjField : valueObjFields) {
            Annotation annotation = valueObjField.getAnnotation(Attributes.class);
            Attributes attr = (Attributes) annotation;

            // hack because of Android's Instant Run functionality:
            if (valueObjField.getName().equals("$change"))
                continue;

            if (attr != null && attr.notMapped()) {
                continue;
            }

            String fieldName = (attr != null && attr.primaryKey() ? "_" : "") + valueObjField.getName();
            if (fieldName.equals("id")) {
                fieldName = "_id";
            }

            valueObjField.setAccessible(true);

            Object newObj = valueObjField.get(valueObj);

            if (newObj == null) {
                newObj = "";
            }
            if (Integer.class.isAssignableFrom(newObj.getClass())) {
                values.put(fieldName, (Integer) newObj);
            } else if (String.class.isAssignableFrom(newObj.getClass())) {
                values.put(fieldName, (String) newObj);
            } else if (Double.class.isAssignableFrom(newObj.getClass())) {
                values.put(fieldName, (Double) newObj);
            }

        }
        return values;
    }

    public static String getListTitle(Context ctx, int listType) {
        switch (listType) {
            case (Constants.MOVIES_MOST_POPULAR):
                return ctx.getResources().getString(R.string.title_most_popular);
            case (Constants.MOVIES_UPCOMING):
                return ctx.getResources().getString(R.string.title_upcoming);
            case (Constants.MOVIES_SEARCH):
                return ctx.getResources().getString(R.string.title_search);
            case (Constants.MOVIES_NOW_PLAYING):
                return ctx.getResources().getString(R.string.title_now_playing);
            default:
                return "";
        }
    }

    public static HashMap<String, Integer> getColumnsMapFromCursor(Cursor cursor) {
        HashMap<String, Integer> columnsMap = new HashMap<>();
        for (String columnName : cursor.getColumnNames())
            columnsMap.put(columnName, cursor.getColumnIndex(columnName));
        return columnsMap;
    }

    static public void SetAlarmManagerForSyncService(Context ctx, int frequency) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent service = new Intent(ctx, AlarmReceiver.class);
        service.setAction("AlarmReceiver");
        PendingIntent pIntent = PendingIntent.getBroadcast(ctx, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + DELAY_IN_MILLIS,
                frequency,
                pIntent);

        Logger.d(TMDbISELApplication.TAG, "Alarm frequency set = " + frequency);
    }
}

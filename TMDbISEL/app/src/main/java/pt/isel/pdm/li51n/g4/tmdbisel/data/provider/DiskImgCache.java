package pt.isel.pdm.li51n.g4.tmdbisel.data.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pt.isel.pdm.li51n.g4.tmdbisel.BuildConfig;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;

public class DiskImgCache {

    private static final String TAG = DiskImgCache.class.getSimpleName();
    private static final String TAG_LOG_ERROR = "ERROR";
    private static final String TAG_LOG_DEBUG = "DEBUG";

    private static final String PRIVATE_DIR_NAME = ".imagesCache"; // Hidden directory
    private static final int CACHE_ENTRY_VALUE_COUNT = 1;
    private static final int CACHE_FIRST_ENTRY_IDX = 0;
    private static final Bitmap.CompressFormat IMAGE_FORMAT = Bitmap.CompressFormat.JPEG;
    private static final int COMPRESSOR_QUALITY = 50;

    private static final int TWO_TENTH = 1024;
    private static final double MIN_USABLE_SPACE_TAX_ALLOWED = 0.05;
    private static final long SAFE_OVERHEAD_BYTE_SPACE = 2 * TWO_TENTH * TWO_TENTH; // 2 MiB
    private static final int IO_BUFFER_SIZE = 128 * TWO_TENTH; // 128KiB

    //private static int byteSize;
    private static File cacheDir;
    private static DiskLruCache diskCache;
    private static boolean starting;
    private static boolean active;

    public DiskImgCache(Context context, int kbSize) {

        if (kbSize == 0)
            return;

        active = true;
        starting = true;
        long byteSize = kbSize * TWO_TENTH;

        // Initialize disk cache
        cacheDir = getDiskCacheDir(context, byteSize);
        initDiskCache(cacheDir, byteSize);
        if (!starting) { // Init successful
            Logger.d(TAG, TAG_LOG_DEBUG + " Cache initiated successfully!");
        }
        else { // Init problem/error
            Logger.d(TAG, TAG_LOG_DEBUG + " Could not initiate cache!");
        }
    }

    private static void initDiskCache(File dir, long byteSize) {
        Logger.d(TAG, TAG_LOG_DEBUG + " Init disk cache. App version " + BuildConfig.VERSION_CODE);
        try {
            diskCache = DiskLruCache.open(dir, BuildConfig.VERSION_CODE, CACHE_ENTRY_VALUE_COUNT, byteSize);
        } catch (IOException failedDiskLruCache) {
            Logger.e(TAG, failedDiskLruCache);
            return;
        }
        starting = false;
    }

    // Creates a unique subdirectory of the designated app; cache directory tries to use external
    // storage but if not available, falls back on internal storage
    @Nullable
    private static File getDiskCacheDir(Context context, long byteSize) {
        // Check for an already present cache, first on internal and second on external storage
        // Use the first cache found present
        File file = context.getFilesDir();
        File usedCache = alreadyUsedCache(file);
        if (usedCache != null) {
            Logger.d(TAG, TAG_LOG_DEBUG + " Already used cache (internal): " + usedCache.getAbsolutePath());
            return usedCache;
        } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = context.getExternalFilesDir(null);
            usedCache = alreadyUsedCache(file);
            if (usedCache != null) {
                Logger.d(TAG, TAG_LOG_DEBUG + " Already used cache (external): " + usedCache.getAbsolutePath());
                return usedCache;
            }
        }

        // If there's no cache already on any storage, check space availability to the required size
        // First on external second on internal storage
        // Make directory for new cache on the first with enough usable space
        // Case there's no storage with enough usable space create cache nonetheless (on external storage)
        if (hasEnoughFreeSpace(file, byteSize)) {
            file = new File(file, PRIVATE_DIR_NAME);
            Logger.d(TAG, TAG_LOG_DEBUG + " External directory will be created: " + file.getAbsolutePath());
            if (!file.mkdir()) {
                Log.e(TAG + " " + TAG_LOG_ERROR, "Cache external directory not created!");
                return null;
            }
        } else {
            file = context.getFilesDir();
            if (hasEnoughFreeSpace(file, byteSize)) {
                file = new File(file, PRIVATE_DIR_NAME);
                Logger.d(TAG, TAG_LOG_DEBUG + " Internal directory will be created: " + file.getAbsolutePath());
                if (!file.mkdir()) {
                    Log.e(TAG + " " + TAG_LOG_ERROR, "Cache internal directory not created!");
                    return null;
                }
            } else {
                Log.e(TAG + " " + TAG_LOG_ERROR, "Not enough free space on any storage!");
                Logger.d(TAG, TAG_LOG_DEBUG + " Creating cache (on external storage) without requested size!");
                file = context.getExternalFilesDir(null);
                file = new File(file, PRIVATE_DIR_NAME);
                Logger.d(TAG, TAG_LOG_DEBUG + " Directory name: " + file.getAbsolutePath());
                if (!file.mkdir()) {
                    Log.e(TAG + " " + TAG_LOG_ERROR, "Fallback cache external directory not created!");
                    return null;
                }
            }
        }

        return file;
    }

    @Nullable
    private static File alreadyUsedCache(File file) {
        File[] fileList = file.listFiles();

        for (File f : fileList) {
            if (f.getName().equals(PRIVATE_DIR_NAME))
                return f;
        }

        return null;
    }

    private static boolean hasEnoughFreeSpace(File file, long requiredSize) {
        long usableSpace = file.getUsableSpace();
        long totalSpace = file.getTotalSpace();
        long usableSpaceTax = usableSpace / totalSpace;
        Logger.d(TAG, TAG_LOG_DEBUG + " Usable space (%): " + usableSpaceTax);

        return usableSpaceTax > MIN_USABLE_SPACE_TAX_ALLOWED &&
                usableSpace > requiredSize + SAFE_OVERHEAD_BYTE_SPACE;
    }

    private static void deleteCacheDir(File cacheDir) {
        Logger.d(TAG, TAG_LOG_DEBUG + " Trying to delete file: " + cacheDir.getAbsolutePath());
        if (!cacheDir.delete())
            Logger.d(TAG, TAG_LOG_DEBUG + " Could not delete file: " + cacheDir.getAbsolutePath());
    }

    public void open() {
        if (active && diskCache.isClosed())
            initDiskCache(cacheDir, diskCache.size());
    }

    public boolean containsKey(String key) {
        Logger.d(TAG, TAG_LOG_DEBUG + " Contains key: searching for key " + key);

        if (!active)
            return false;

        boolean keyFound = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = diskCache.get(key);
            keyFound = snapshot != null;
        } catch (IOException failedCacheGetOperation) {
            Logger.e(TAG, failedCacheGetOperation);
        } finally {
            if (snapshot != null)
                snapshot.close();
        }
        return keyFound;
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (active && diskCache != null && !starting) {
            DiskLruCache.Editor editor = null;
            try {
                editor = diskCache.edit(key); // Get cache editor for key
                if (editor == null) {         // If editor not available
                    Logger.d(TAG, TAG_LOG_DEBUG + " Add bitmap: could not get editor for key " + key);
                    return;                   // Do nothing
                }                             // Got a valid editor
                if (writeBitmap(bitmap, editor)) {
                    editor.commit();
                    Logger.d(TAG, TAG_LOG_DEBUG + " Add bitmap: written to key " + key);
                }
                else {
                    editor.abort();
                    Logger.d(TAG, TAG_LOG_DEBUG + " Add bitmap: could not write to key " + key);
                }
            } catch (IOException failedDiskCacheOperation) {
                Logger.e(TAG, failedDiskCacheOperation);
                if (editor != null) {
                    try {
                        editor.abort();
                    } catch (IOException failedEditorAbort) {
                        Logger.e(TAG, failedEditorAbort);
                        // This exception is being ignored
                    }
                }
            }
        }
    }

    @Nullable
    public Bitmap getBitmapFromCache(final String key) {
        if (active && diskCache != null && !starting) {
            DiskLruCache.Snapshot snapshot = null;
            try {
                snapshot = diskCache.get(key);
                if (snapshot == null) {
                    Logger.d(TAG, TAG_LOG_DEBUG + " Get bitmap: could not get key " + key);
                    return null;
                }
                final InputStream inStream = snapshot.getInputStream(CACHE_FIRST_ENTRY_IDX);
                if (inStream != null) {
                    final BufferedInputStream buffInStream = new BufferedInputStream(inStream, IO_BUFFER_SIZE);
                    Logger.d(TAG, TAG_LOG_DEBUG + " Get bitmap: decoding bitmap on key " + key);
                    return BitmapFactory.decodeStream(buffInStream);
                }
            } catch (IOException failedCacheGetOperation) {
                Logger.e(TAG, failedCacheGetOperation);
            } finally {
                if (snapshot != null)
                    snapshot.close();
            }
        }
        return null;
    }

    public long currentSize() {
        if (!active)
            return 0;
        return diskCache.size();
    }

    public void clear() {
        Logger.d(TAG, TAG_LOG_DEBUG + " Clear cache: in progress!");
        if (active) {
            try {
                diskCache.delete();
            } catch (IOException failedCacheClear) {
                Logger.e(TAG, failedCacheClear);
            }
        }
    }

    public void close() {
        Logger.d(TAG, TAG_LOG_DEBUG + " Close cache: in progress!");
        if (active) {
            try {
                diskCache.close();
            } catch (IOException failedCacheClose) {
                Logger.e(TAG, failedCacheClose);
            }
        }
    }

    public long setSize(int newByteSize) {
        if (!active) {
            try {
                diskCache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, CACHE_ENTRY_VALUE_COUNT, newByteSize);
            } catch (IOException failedCacheOpen) {
                Logger.e(TAG, failedCacheOpen);
                Logger.d(TAG, TAG_LOG_DEBUG + " Failed cache open!");
                return 0;
            }
            active = true;
        }
        else {
            if (newByteSize != diskCache.maxSize()) {
                Logger.d(TAG, TAG_LOG_DEBUG + " On cache set size!");
                try {
                    diskCache.close();
                    if (newByteSize == 0) {
                        active = false;
                        return 0;
                    }
                    Logger.d(TAG, TAG_LOG_DEBUG + " Cache closed!");
                    diskCache = DiskLruCache.open(cacheDir, BuildConfig.VERSION_CODE, CACHE_ENTRY_VALUE_COUNT, newByteSize);
                    Logger.d(TAG, TAG_LOG_DEBUG + " Cache open!");
                } catch (IOException failedCacheResize) {
                    Logger.e(TAG, failedCacheResize);
                    Logger.d(TAG, TAG_LOG_DEBUG + " Failed cache resize!");
                    return diskCache.maxSize();
                }
            }
        }
        return newByteSize;
    }

    private boolean writeBitmap(Bitmap bitmap, DiskLruCache.Editor editor) throws IOException {
        IOException e = null;
        boolean success = false;
        OutputStream outStream = null;
        try {
            Logger.d(TAG, TAG_LOG_DEBUG + " Editor Out Stream: " + editor.newOutputStream(CACHE_FIRST_ENTRY_IDX).toString());
            outStream = new BufferedOutputStream(editor.newOutputStream(CACHE_FIRST_ENTRY_IDX), IO_BUFFER_SIZE);
            Logger.d(TAG, TAG_LOG_DEBUG + " Buff Out Stream: " + outStream.toString());
            success = bitmap.compress(IMAGE_FORMAT, COMPRESSOR_QUALITY, outStream);
            Logger.d(TAG, TAG_LOG_DEBUG + " Bitmap compress result: " + success);
        } catch (IOException failedGettingStream) {
            Logger.e(TAG, failedGettingStream);
            e = failedGettingStream;
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException failedOutStreamClose) {
                    Logger.e(TAG, failedOutStreamClose);
                    e = failedOutStreamClose;
                }
            }
            if (e != null) throw e;
        }
        return success;
    }
}

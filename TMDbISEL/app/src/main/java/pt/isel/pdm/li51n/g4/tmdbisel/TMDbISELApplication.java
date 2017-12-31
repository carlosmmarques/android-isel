package pt.isel.pdm.li51n.g4.tmdbisel;

import android.app.Application;
import android.content.Intent;
import android.widget.ImageView;

import java.util.HashMap;

import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.DiskImgCache;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.IRepository;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.MovieFavourites;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.MovieInfoProvider;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbProvider;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbDB.TMDbDBRepo;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbvolatileRepo.TMDbVolatileRepo;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Preferences;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;
import pt.isel.pdm.li51n.g4.tmdbisel.services.SyncService;

public class TMDbISELApplication extends Application {

    public static final String TAG = TMDbISELApplication.class.getSimpleName();

    public static final String MOVIE_LIST_TYPE = "movie_type";
    public static final String MOVIE_QUERY = "movie_query";
    public static final String TAG_MOVIE_LIST_FRAGMENT = "ListFrag";
    public static final String TAG_MOVIE_PAGER_FRAGMENT = "PagerFrag";
    public static final String MOVIE_REPOSITORY_REF = TAG + ".movie.repository.ref";
    public static final IRepository MOVIE_REPOSITORY = TMDbVolatileRepo.getInstance();
    public static final IRepository MOVIE_REPOSITORY_CP = TMDbDBRepo.getInstance();
    public static final HashMap<String, ImageView> sImageViewsToUpdate = new HashMap<>();
    public static final MovieFavourites FAVOURITES = new MovieFavourites();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static boolean mTwoPane;
    public static MovieInfoProvider mProvider;
    public static int mListType;
    public static DiskImgCache sImgCache;

    public static MovieInfoProvider getMovieInfoProvider() {
        return mProvider;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "Starting App...");

        MOVIE_REPOSITORY_CP.setContentResolver(getContentResolver());

        mProvider = new TMDbProvider();
        mListType = Constants.MOVIES_NOW_PLAYING;

        sImgCache = new DiskImgCache(this, Preferences.getImgCacheSize(this));

        FAVOURITES.setContext(this);

        Intent initialSyncService = SyncService.newInstance(this);
        startService(initialSyncService);

        Utils.SetAlarmManagerForSyncService(this, Preferences.getSyncFrequencyInMillis(this));

        Logger.d(TAG, "Started Application, Alarm Manager is Set, All systems Go!");

    }

}

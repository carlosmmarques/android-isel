package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class SyncService extends TMDbService {

    private static final HashMap<String, Integer> SYNCSTATES_MAP = new HashMap<>();
    private static final String TAG = SyncService.class.getSimpleName();
    public static final String SYNCSERVICE_STALE_MOVIELIST = TAG + ".sync.state.movieList";
    public static final String SYNCSERVICE_SYNCSTATE = TAG + ".sync.service.syncstate";
    public static final String SYNCSERVICE_CRITERIA = TAG + ".sync.service.criteria";

    /**
     * Creates an IntentService.  Invoked by static Factory Method
     *
     */
    public SyncService() { super("SyncService"); }

    public static Intent newInstance(Context context) {
        return new Intent(context, SyncService.class);
    }

    @Override
    protected void doWork(Intent intent) {
        Logger.d(TAG, "Starting SyncService.......");
        Utils.assertNotOnUIThread();

        boolean fromAM = intent.getBooleanExtra(Constants.ALARM_MANAGER, false);
        if (fromAM) {
            Logger.d(TAG, "================================================> From AlarmManager");
        }
        if (TMDbISELApplication.MOVIE_REPOSITORY_CP.getCount() == 0) { // Go fetch everything and populate repository
            Logger.d(TAG, "SyncService, starting all services");
            Intent serviceNowPlaying = MovieCollectionRefresherService.NewInstance(this, Constants.MOVIES_NOW_PLAYING);
            startService(serviceNowPlaying);
            Intent serviceUpComing = MovieCollectionRefresherService.NewInstance(this, Constants.MOVIES_UPCOMING);
            startService(serviceUpComing);
            Intent servicePopular = MovieCollectionRefresherService.NewInstance(this, Constants.MOVIES_MOST_POPULAR);
            startService(servicePopular);
            SYNCSTATES_MAP.putAll(TMDbISELApplication.MOVIE_REPOSITORY_CP.getLatestSyncStatus());
            return;
        }
        // ... Or go sync current data
        SYNCSTATES_MAP.putAll(TMDbISELApplication.MOVIE_REPOSITORY_CP.getLatestSyncStatus());
        for (String criteria : SYNCSTATES_MAP.keySet()) {
            // Update latest syncstatus
            for (Movie movie : TMDbISELApplication.MOVIE_REPOSITORY_CP.getMoviesByCriteria(criteria)) {
                Integer movieId = movie.getId();
                int movieSyncStatus = TMDbISELApplication.MOVIE_REPOSITORY_CP.getMovieSyncStateByCriteria(movieId, criteria);
                if (movieSyncStatus >= SYNCSTATES_MAP.get(criteria)) {
                    continue;
                }
                startService(new Intent(
                                this, MovieRefresherService.class)
                                .putExtra(MovieRefresherService.EXTRA_MOVIE, movie)
                                .putExtra(MovieRefresherService.EXTRA_MOVIE_LIST, Integer.parseInt(criteria))
                );
            }
        }
        Logger.d(TAG, "Ending SyncService.......");
    }
}

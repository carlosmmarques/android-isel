package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.app.Activity;
import android.content.Intent;

import java.io.IOException;

import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbAPIConnectionException;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbAPIRateLimitException;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class MovieRefresherService extends TMDbService {

    private static final String TAG = MovieRefresherService.class.getSimpleName();
    public static final String EXTRA_MOVIE_LIST_TYPE = TAG + ".Movie.List.Type";
    public static final String EXTRA_MOVIE = TAG + ".movie";
    public static final String EXTRA_MOVIE_LIST = TAG + ".movieList";
    public static final String EXTRA_RESULT = TAG + ".RESULT";
    public static final String NOTIFICATION = TAG + ".service.receiver";
    private int result = Activity.RESULT_CANCELED;
    private String mListType = "999";
    /**
     * Creates an IntentService.  Invoked by static Factory Method
     */
    public MovieRefresherService() {
        super("MovieRefresherService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MovieRefresherService(String name) {
        super(name);
    }

    @Override
    protected void doWork(Intent intent) {
        Utils.assertNotOnUIThread();
        try {
            mListType = String.valueOf(intent.getIntExtra(EXTRA_MOVIE_LIST, 999));
            Movie movie = TMDbISELApplication.getMovieInfoProvider()
                    .getMovieInfo(
                            String.valueOf(((Movie) intent.getParcelableExtra(EXTRA_MOVIE)).getId()),
                            Utils.getLanguage(getApplicationContext()));
            TMDbISELApplication.MOVIE_REPOSITORY_CP.updateMovieDetails(movie);
            int lastSyncstatus = TMDbISELApplication.MOVIE_REPOSITORY_CP.getMovieSyncStateByCriteria(movie.getId(), mListType);
            TMDbISELApplication.MOVIE_REPOSITORY_CP.updateMovieSyncStatus(movie.getId(), mListType, lastSyncstatus + 1);
            result = Activity.RESULT_OK;
            notifyActivity();
        /**
         * In case we've reached the API limit, we wait and relaunch service
         */
        } catch (TMDbAPIRateLimitException e) {
            try {
                Thread.sleep(10000);
            } catch (Exception e1) {
                Logger.e(TAG, e);
            }
            startService(new Intent(this, MovieRefresherService.class)
                    .putExtra(MovieRefresherService.EXTRA_MOVIE, intent.getParcelableExtra(EXTRA_MOVIE)));
        } catch (IOException | TMDbAPIConnectionException e) {
            Logger.e(TAG, e);
        }
    }

    private void notifyActivity() {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(EXTRA_RESULT, result)
                .putExtra(EXTRA_MOVIE_LIST_TYPE, mListType);
        sendBroadcast(intent);
    }
}
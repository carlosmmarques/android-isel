package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.R;
import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbAPIRateLimitException;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class MovieCollectionRefresherService extends TMDbService {

    private static final String TAG = MovieCollectionRefresherService.class.getSimpleName();
    public static final String EXTRA_MOVIE_LIST_TYPE = TAG + ".Movie.List.Type";
    public static final String RESULT = TAG + ".RESULT";
    public static final String NOTIFICATION = TAG + ".service.receiver";
    private int result = Activity.RESULT_CANCELED;

    /**
     * Creates an IntentService.  Invoked by static Factory Method
     *
     */
    public MovieCollectionRefresherService() { super("MovieCollectionRefresherService"); }

    public static Intent NewInstance(Context ctx, int movieListType) {
        return new Intent(ctx, MovieCollectionRefresherService.class)
                .putExtra(EXTRA_MOVIE_LIST_TYPE, movieListType);
    }

    @Override
    protected void doWork(Intent intent) {
        Utils.assertNotOnUIThread();
        final int movieListType = intent.getIntExtra(EXTRA_MOVIE_LIST_TYPE, Constants.MOVIES_NOW_PLAYING);
        try {
            for (int i = 1; i < Constants.PAGES_TO_DOWNLOAD; ++i) {
                saveAndNotify(
                        TMDbISELApplication.getMovieInfoProvider().getMovieListInfo(Integer.toString(movieListType),
                                Utils.getLanguage(getApplicationContext()), null, i),
                        movieListType
                );
            }
        } catch (TMDbAPIRateLimitException e) {
            Logger.e(TAG, e);
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e1) {
                Logger.e(TAG, e1);
            }
        } catch (IOException e) {
            Logger.e(TAG, e);
        }
    }

    /**
     *
     * @param movies
     * @param movieListType
     */
    private void saveAndNotify(List<Movie> movies, final int movieListType) {
        for (Movie m : movies) {
            if (movieListType == Constants.MOVIES_NOW_PLAYING && TMDbISELApplication.FAVOURITES.contains(m.getId())) {
                notifyUserMovieNowPlaying(m);
                TMDbISELApplication.FAVOURITES.remove(m.getId());
            }
            if (!m.hasDetails()) {
                startService(new Intent(
                                this, MovieRefresherService.class)
                                .putExtra(MovieRefresherService.EXTRA_MOVIE, m)
                                .putExtra(MovieRefresherService.EXTRA_MOVIE_LIST, movieListType)
                );
            }

        }
        result = Activity.RESULT_OK;
        notifyActivity(movieListType);
        result = Activity.RESULT_CANCELED;
    }

    private void notifyActivity(int listType) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result)
                .putExtra(EXTRA_MOVIE_LIST_TYPE, listType);
        sendBroadcast(intent);
    }

    public void notifyUserMovieNowPlaying(Movie movie) {
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            final Notification notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("The current Movie is now playing!")
                    .setContentText(movie.getTitle())
                    .build();
            notificationManager.notify(movie.getId(), notification);
        }
    }

}

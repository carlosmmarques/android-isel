package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.content.Intent;

import java.io.IOException;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Review;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbAPIConnectionException;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbAPIRateLimitException;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class MovieReviewsRefresherService extends TMDbService {

    private static final String TAG = MovieReviewsRefresherService.class.getSimpleName();
    public static final String EXTRA_MOVIE = TAG + ".movie";

    /**
     * Creates an IntentService.  Invoked by static Factory Method
     *
     */
    public MovieReviewsRefresherService() { super("MovieReviewsRefresherService"); }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MovieReviewsRefresherService(String name) {
        super(name);
    }

    @Override
    protected void doWork(Intent intent) {
        Utils.assertNotOnUIThread();

        try {
            String movieId = String.valueOf(((Movie) intent.getParcelableExtra(EXTRA_MOVIE)).getId());
            List<Review> movieReviews = TMDbISELApplication.getMovieInfoProvider()
                    .getReviewsByMovieId(movieId, Utils.getLanguage(getApplicationContext()));

            if (movieReviews.size() > 0) {
                Logger.d(TAG, "we have reviews!!!");
                TMDbISELApplication.MOVIE_REPOSITORY_CP.updateMovieReviews(
                        movieId,
                        movieReviews);
            }
        } catch (TMDbAPIRateLimitException e) {
            try {
                Thread.sleep(10000);
            } catch (Exception e1) {
                Logger.e(TAG, e);
            }
            startService(new Intent(this, MovieReviewsRefresherService.class)
                    .putExtra(MovieReviewsRefresherService.EXTRA_MOVIE, intent.getParcelableExtra(EXTRA_MOVIE)));
        } catch (IOException | TMDbAPIConnectionException e) {
            Logger.e(TAG, e);
        }
    }
}

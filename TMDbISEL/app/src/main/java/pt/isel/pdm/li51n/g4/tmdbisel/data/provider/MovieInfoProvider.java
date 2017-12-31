package pt.isel.pdm.li51n.g4.tmdbisel.data.provider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Review;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbAPIConnectionException;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbAPIRateLimitException;
import pt.isel.pdm.li51n.g4.tmdbisel.workers.DownloadThread;

public interface MovieInfoProvider {

    /**
     * Asynchonous method to get the movie list from the selected type.
     *
     * @param type Movie List Type
     * @param language Movie List language
     * @param query    Text to search
     * @param completionCallback The callback to be executed once the operation is completed, either successfully or in failure.
     */
    void getMovieListInfoAsync(@NonNull String type, @NonNull String language, @Nullable String query, @NonNull DownloadThread.Callback completionCallback);

    /**
     * Method to get the movie list from the selected type.
     *
     * @param type Movie List Type
     * @param language Movie List language
     * @param query    Text to search
     * @param pageNumber
     * @return Ther resulting Movie List
     * @throws TMDbAPIRateLimitException, IOException If an error occurred while fetching the Movie List information.
     */
    List<Movie> getMovieListInfo(@NonNull String type, @NonNull String language, @Nullable String query, int pageNumber) throws TMDbAPIRateLimitException, IOException;

    /**
     * Asynchonous method to get the movie info from the given id.
     *
     * @param id The movie id
     * @param language Movie language
     * @param completionCallback The callback to be executed once the operation is completed, either successfully or in failure.
     */
    void getMovieInfoAsync(@NonNull String id, @NonNull String language, @NonNull DownloadThread.Callback completionCallback);

    /**
     * Method to get the movie info from the given id.
     *
     * @param id The movie id.
     * @param language Movie language
     * @return The resulting Movie information
     * @throws TMDbAPIRateLimitException If API Limit was reached
     * @throws IOException If an error occurred while fetching the Movie List information.
     */
    Movie getMovieInfo(@NonNull String id, @NonNull String language) throws TMDbAPIRateLimitException, IOException, TMDbAPIConnectionException;


    /**
     * Method to get the Movie Reviews
     *
     * @param id       The movie id.
     * @param language Movie language
     * @return The Movie Reviews
     */
    List<Review> getReviewsByMovieId(@NonNull String id, @NonNull String language) throws IOException, TMDbAPIRateLimitException, TMDbAPIConnectionException;

}

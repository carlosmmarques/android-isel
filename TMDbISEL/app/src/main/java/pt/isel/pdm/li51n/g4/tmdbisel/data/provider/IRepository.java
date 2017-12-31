package pt.isel.pdm.li51n.g4.tmdbisel.data.provider;

import android.content.ContentResolver;

import java.util.HashMap;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Review;

public interface IRepository {

    /**
     * Set the content resolver to this repository
     *
     * @param contentResolver
     */
     void setContentResolver(ContentResolver contentResolver);
    /**
     * Add a List of Movies to the repository with specified sync criteria. Will update movie details and sync status if instance is present.
     * Ex: addAndUpdateAllWithCriteria(Movies, "NOW_PLAYING") - will add and update the syncstatus on this movie for this criteria;
     * @param movies
     */
    void addAndUpdateAllWithCriteria(List<Movie> movies, String criteria);

    /**
     * Add Movie and Update detais and sync status if existant
     * @param movie
     */
    void updateMovieDetails(Movie movie);

    /**
     * Retrieve a Movie Instance from the repository
     * @param movieId
     * @return Movie instance if found, null otherwise
     */
    Movie getMovie(int movieId);

    /**
     * Get a List of Movies by list Criteria
     * @param criteria
     * @return List of Movies.
     */
    List<Movie> getMoviesByCriteria(String criteria);

    /**
     * Get syncState from the repository
     * @param movieId
     * @param criteria
     * @return int count of syncstate
     */
    int getMovieSyncStateByCriteria(int movieId, String criteria);

    /**
     * Get Count of all movies in the Repository
     * @return
     */
    int getCount();

    /**
     * Get Count of all movies int the Repository by criteria
     * @param criteria ListType
     * @return int count by criteria
     */
    int getCountByCriteria(String criteria);

    /**
     * Update Movie Sync Status for a specific Criteria
     * @param id
     * @param stringExtra
     * @param syncStatus
     */
    void updateMovieSyncStatus(Integer id, String stringExtra, int syncStatus);

    /**
     * Fetch a complete hashmap<String criteria, Integer SyncStatus> with the highest syncStatus By Criteria
     * @return
     */
    HashMap<String, Integer> getLatestSyncStatus();

    /**
     * Updates Movie Reviews
     *
     * @param movieId movie ID
     * @param reviews Reviews
     */
    void updateMovieReviews(String movieId, List<Review> reviews);
}

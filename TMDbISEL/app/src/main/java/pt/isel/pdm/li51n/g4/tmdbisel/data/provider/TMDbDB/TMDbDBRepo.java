package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbDB;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.ProductionCompany;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Review;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.SyncState;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.schema.TMDbSchema;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.IRepository;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class TMDbDBRepo implements IRepository {


    private static final String TAG = TMDbDBRepo.class.getSimpleName();


    private static TMDbDBRepo repo = new TMDbDBRepo();
    private ContentResolver mContentResolver;

    /**
     * Private constructor - this constructor is not to be redefined
     */
    private TMDbDBRepo() {
    }

    /**
     * Static factory method
     *
     * @return instance
     */
    public static TMDbDBRepo getInstance() {
        return repo;
    }

    /**
     * Set the content resolver to this repository
     *
     * @param contentResolver
     */
    public void setContentResolver(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    /**
     * Add a List of Movies to the repository with specified sync criteria. Will update movie details and sync status if instance is present.
     * Ex: addAndUpdateAllWithCriteria(Movies, "NOW_PLAYING") - will add and update the syncstatus on this movie for this criteria;
     *
     * @param movies
     * @param criteria
     */
    @Override
    public void addAndUpdateAllWithCriteria(List<Movie> movies, String criteria) {
        for (Movie movie : movies) {
            if (getMovie(movie.getId()) == null) {
                updateMovieDetails(movie);
            }
            int lastSyncstatus = getMovieSyncStateByCriteria(movie.getId(), criteria);
            updateMovieSyncStatus(movie.getId(), criteria, lastSyncstatus + 1);
        }
        // status cleanup (If a Movie in the current List is no longer associated with such criteria,
        // that means we need to reflect this dissociation on the repository):
        for (Movie movieInUpdate : movies) {
            Boolean found = false;
            for (Movie movieInRepo : getMoviesByCriteria(criteria)) {
                if (movieInRepo.getId().equals(movieInUpdate.getId())) found = true;
            }
            if (!found) {
                updateMovieSyncStatus(movieInUpdate.getId(), criteria, -1);
            }
        }

    }

    /**
     * Update movie details
     *
     * @param movie
     */
    @Override
    public void updateMovieDetails(Movie movie) {
        try {
            /**
             * Update Movie
             * Obs: Always calling Contentresolver.insert because of db call to insertWithOnConflict
             */
            ContentValues movieFieldsAndValues = Utils.getFieldNamesAndValues(movie);
            // always insert because of db call to insertWithOnConflict
            Uri movieResult = mContentResolver.insert(TMDbDBContract.MovieContract.CONTENT_URI, movieFieldsAndValues);
            if (movieResult == null) {
                return;
            }
            long itemId = ContentUris.parseId(movieResult);
            Logger.d(TAG, "Movie updated with id: " + itemId);

            /**
             * Update Production Companies for this Movie
             */
            ContentValues productionCompanyValues;
            for (ProductionCompany pc : movie.getProductionCompanies()) {
                pc.movieId = String.valueOf(movie.getId());
                productionCompanyValues = Utils.getFieldNamesAndValues(pc);
                Uri pcResult = mContentResolver.insert(TMDbDBContract.ProductionCompanyContract.CONTENT_URI, productionCompanyValues);
                if (pcResult == null) {
                    return;
                }
                itemId = ContentUris.parseId(pcResult);
                Logger.d(TAG, "Production Companies inserted with id: " + itemId);
            }

            /**
             * Update Reviews for this Movie
             */
            List<Review> reviews = movie.getReviews();
            ContentValues reviewValues;
            for (Review r : reviews) {
                r.setMovieId(String.valueOf(movie.getId()));
                reviewValues = Utils.getFieldNamesAndValues(r);
                Uri reviewResult = mContentResolver.insert(TMDbDBContract.ReviewContract.CONTENT_URI, reviewValues);
                if (reviewResult == null) {
                    return;
                }
                long ReviewItemId = ContentUris.parseId(reviewResult);
                Logger.d(TAG, "Review inserted with id: " + ReviewItemId);
            }
        } catch (IllegalAccessException e) {
            Logger.e(TAG, e);
        }
    }

    /**
     * Retrieve a Movie Instance from the repository
     *
     * @param movieId
     * @return Movie instance if found, null otherwise
     */
    @Override
    public Movie getMovie(int movieId) {
        String[] projection = TMDbSchema.Movies.getColumns();
        Cursor cur = mContentResolver.query(
                ContentUris.withAppendedId(TMDbDBContract.MovieContract.CONTENT_URI, (long) movieId),
                projection,
                null,
                null,
                null);
        Movie movie = null;
        if (cur != null && cur.moveToNext()) {
            movie = new Movie(cur);
            cur.close();
            movie.setProductionCompanies(getProductionCompanies(movie.getId()));
            movie.setReviews(getReviews(movie.getId()));
        }
        return movie;
    }

    /**
     * Get a List of Movies by list Criteria
     *
     * @param criteria
     * @return List of Movies.
     */
    @Override
    public List<Movie> getMoviesByCriteria(String criteria) {
        List<Movie> result = new ArrayList<>();
        Cursor cursor = mContentResolver.query(
                TMDbDBContract.MovieContract.CONTENT_URI_BY_CRITERIA,
                null,
                null,
                new String[]{String.valueOf(criteria)},
                TMDbDBContract.MovieContract.SORT_ORDER_DEFAULT
        );

        if (cursor != null) {
            Logger.d(TAG, "cursor isn't null, lets get " + cursor.getCount() + " Movies ");
            while (cursor.moveToNext()) {
                Movie movie = new Movie(cursor);
                result.add(movie);
            }
            cursor.close();
        }
        Logger.d(TAG, "Já está!!!!!");
        return result;
    }

    /**
     * Get syncState for a specific movie from the repository.
     *
     * @param movieId
     * @param criteria
     * @return int count of syncstate. -1 if movie is not existent.
     */
    @Override
    public int getMovieSyncStateByCriteria(int movieId, String criteria) {
        Cursor cursor = mContentResolver.query(
                TMDbDBContract.SyncStateContract.CONTENT_URI
                        .buildUpon()
                        .appendQueryParameter("movieId", Integer.toString(movieId) + " AND ")
                        .appendQueryParameter("criteria", criteria)
                        .build(),
                null,
                null,
                null,
                TMDbDBContract.SyncStateContract.SORT_ORDER_DEFAULT
        );
        if (cursor != null && cursor.moveToNext()) {
            Logger.d(TAG, "cursor isn't null, lets get the SyncState.");
            int version = cursor.getColumnIndex("version");
            cursor.close();
            return version;
        }
        Logger.d(TAG, "No syncstate was found for this criteria.");
        return -1;
    }

    /**
     * Get number of movies in the Repository
     *
     * @return number of movies in the Repository
     */
    @Override
    public int getCount() {
        int res = 0;
        Cursor cursor = mContentResolver.query(
                TMDbDBContract.MovieContract.CONTENT_URI,
                null,
                null,
                null,
                TMDbDBContract.MovieContract.SORT_ORDER_DEFAULT
        );
        if (cursor != null) {
            res = cursor.getCount();
            cursor.close();
        }

        return res;
    }

    /**
     * Get Count of all movies int the Repository by criteria
     *
     * @param criteria ListType
     * @return int count by criteria
     */
    @Override
    public int getCountByCriteria(String criteria) {
        int res = 0;
        Cursor cursor = mContentResolver.query(
                TMDbDBContract.MovieContract.CONTENT_URI,
                null,
                null,
                new String[]{String.valueOf(criteria)},
                TMDbDBContract.MovieContract.SORT_ORDER_DEFAULT
        );
        if (cursor != null) {
            res = getCount();
            cursor.close();
        }

        return res;
    }

    /**
     * Update Movie Sync Status for a specific Criteria
     *
     * @param movieId
     * @param criteria   - Criteria
     * @param syncStatus - SyncStatus
     */
    @Override
    public void updateMovieSyncStatus(Integer movieId, String criteria, int syncStatus) {
        try {
            /**
             * actualiação de Syncstate - Só faz sentido em termos de actualização de colecção (Actualiza os mais recentes)
             */
            if (getMovie(movieId) == null) return;
            SyncState syncstate = new SyncState(movieId, Integer.parseInt(criteria), Integer.toString(syncStatus));
            ContentValues syncstateValues = Utils.getFieldNamesAndValues(syncstate);
            Uri syncStateResult = mContentResolver.insert(TMDbDBContract.SyncStateContract.CONTENT_URI, syncstateValues);
            if (syncStateResult == null) {
                Logger.d(TAG, "Something wron happened while trying to update syncstatus for movie " + movieId + " to version: " + syncStatus);
                return;
            }
            Logger.d(TAG, "SyncState for movie (" + movieId + ") updated with criteria (" + criteria + ") to version: " + syncStatus);
        } catch (IllegalAccessException e) {
            Logger.e(TAG, e);
        }
    }

    /**
     * Fetch a complete hashmap<String criteria, Integer SyncStatus> with the highest syncStatus By Criteria
     *
     * @return
     */
    @Override
    public HashMap<String, Integer> getLatestSyncStatus() {
        HashMap<String, Integer> latestSyncStatus = new HashMap<>();
        Cursor ssCur = mContentResolver.query(
                TMDbDBContract.SyncStateContract.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (ssCur != null) {
            while (ssCur.moveToNext()) {
                SyncState syncState = new SyncState(ssCur);
                int version = syncState.version.equals("") ? 0 : Integer.parseInt(syncState.version);
                String criteria = Integer.toString(syncState.criteria);
                if (latestSyncStatus.containsKey(criteria) && version < latestSyncStatus.get(criteria))
                    continue;
                latestSyncStatus.put(criteria, version);
            }
            ssCur.close();
        }
        return latestSyncStatus;
    }

    /**
     * Updates Movie Reviews
     *
     * @param movieId movie ID
     * @param reviews Reviews
     */
    @Override
    public void updateMovieReviews(String movieId, List<Review> reviews) {
        try {
            /**
             * actualiação de Reviews
             */
            if (getMovie(Integer.parseInt(movieId)) == null) return;
            for (Review review : reviews) {
                ContentValues reviewValues = Utils.getFieldNamesAndValues(review);
                Uri reviewResult = mContentResolver.insert(TMDbDBContract.ReviewContract.CONTENT_URI, reviewValues);
                if (reviewResult == null) return;
                long reviewStateId = ContentUris.parseId(reviewResult);
                Logger.d(TAG, "Review for movie (" + movieId + ") updated with id: " + reviewStateId);
            }
        } catch (IllegalAccessException e) {
            Logger.e(TAG, e);
        }
    }

    private List<ProductionCompany> getProductionCompanies(int movieId) {
        Cursor pc = mContentResolver.query(
                TMDbDBContract.ProductionCompanyContract.CONTENT_URI,
                null,
                null,
                new String[]{String.valueOf(movieId)},
                null);
        List<ProductionCompany> pcList = new ArrayList<>();
        if (pc != null) {
            while (pc.moveToNext()) {
                pcList.add(new ProductionCompany(pc));
            }
            pc.close();
        }
        return pcList;
    }

    private List<Review> getReviews(int movieId) {
        Cursor r = mContentResolver.query(
                TMDbDBContract.ReviewContract.CONTENT_URI,
                null,
                null,
                new String[]{String.valueOf(movieId)},
                null);
        List<Review> rList = new ArrayList<>();
        if (r != null) {
            while (r.moveToNext()) {
                rList.add(new Review(r));
            }
            r.close();
        }
        return rList;
    }
}

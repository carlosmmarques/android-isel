package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbvolatileRepo;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Review;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.IRepoEntry;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.IRepository;

/**
 * Volatile repository. Stores local versions of the data for offline presentation. Allows for
 * search criteria versioning
 */
public class TMDbVolatileRepo implements IRepository{

    private static final TMDbVolatileRepo repo = new TMDbVolatileRepo();
    private final HashMap<Integer, IRepoEntry> tmdbRepoMap = new HashMap<>();

    private TMDbVolatileRepo(){}

    /**
     * static factory Method
     * @return repository instance
     */
    public static TMDbVolatileRepo getInstance(){
        return repo;
    }

    /**
     * Set the content resolver to this repository
     *
     * @param contentResolver
     */
    @Override
    public void setContentResolver(ContentResolver contentResolver) {
        //do nothing, this repository does not use a Content Provider
    }

    /**
     * Add, and if existent, update search criteria versioning information
     *
     * @param movies
     * @param criteria
     */
    @Override
    public synchronized void addAndUpdateAllWithCriteria(List<Movie> movies, String criteria) {
        for (Movie movie : movies) {
            if (tmdbRepoMap.containsKey(movie.getId())) {
                tmdbRepoMap.get(movie.getId()).getSyncstate().updateSyncStateFor(criteria);
                continue;
            }
            String[] crit = {criteria};
            tmdbRepoMap.put(movie.getId(), TMDbRepoEntry.newInstance(movie, new TMDbRepoEntrySyncState(crit)));
        }

        // status cleanup (If a Movie in the current List is no longer associated with such criteria,
        // that means we need to reflect this dissociation on the repository):
        for (Movie movieInUpdate : movies) {
            Boolean found = false;
            for (Movie movieInRepo : this.getMoviesByCriteria(criteria)) {
                if (movieInRepo.getId().equals(movieInUpdate.getId())) found = true;
            }
            if (!found) { // This movie no longer belongs in this list.
                // Perform cleanup:
                TMDbRepoEntry entry = (TMDbRepoEntry) tmdbRepoMap.get(movieInUpdate.getId());
                if (entry.getSyncstate().hasSyncStateForCriteria(criteria))
                    entry.removeSyncState(criteria);
            }
        }


        for (Integer id : tmdbRepoMap.keySet()) {
            for (Movie m : movies) {

            }
            TMDbRepoEntry entry = (TMDbRepoEntry) tmdbRepoMap.get(id);
        }
    }

    /**
     * Update movie details - Dependant of the full Movie Implementation
     * @param movie
     */
    @Override
    public synchronized void updateMovieDetails(@NonNull Movie movie) {
        IRepoEntry entry = tmdbRepoMap.get(movie.getId());
        if (entry != null) {
            Movie m = entry.getMovie();
            m.setAdult(movie.getAdult());
            m.setBackdropPath(movie.getBackdropPath());
            m.setBelongsToCollection(movie.getBelongsToCollection());
            m.setBudget(movie.getBudget());
            m.setGenres(movie.getGenres());
            m.setHomepage(movie.getHomepage());
            m.setId(movie.getId());
            m.setImdbId(movie.getImdbId());
            m.setOriginalLanguage(movie.getOriginalLanguage());
            m.setOriginalTitle(movie.getOriginalTitle());
            m.setOverview(movie.getOverview());
            m.setPopularity(movie.getPopularity());
            m.setPosterPath(movie.getPosterPath());
            m.setProductionCompanies(movie.getProductionCompanies());
            m.setProductionCountries(movie.getProductionCountries());
            m.setReleaseDate(movie.getReleaseDate());
            m.setRevenue(movie.getRevenue());
            m.setRuntime(movie.getRuntime());
            m.setSpokenLanguages(movie.getSpokenLanguages());
            m.setStatus(movie.getStatus());
            m.setTagline(movie.getTagline());
            m.setTitle(movie.getTitle());
            m.setVideo(movie.getVideo());
            m.setVoteAverage(movie.getVoteAverage());
            m.setVoteCount(movie.getVoteCount());
            m.setHasDetails(true);
        }
    }

    /**
     * get a Movie Instance from the repository
     * @param movieId
     * @return
     */
    @Override
    public Movie getMovie(int movieId) {
        return tmdbRepoMap.get(Integer.valueOf(movieId)).getMovie();
    }

    /**
     * Get a List of Movies by search criteria.
     * @param criteria
     * @return
     */
    @Override
    public synchronized List<Movie> getMoviesByCriteria(String criteria) {
        List<Movie> movies = new LinkedList<>();
        for (Integer id : tmdbRepoMap.keySet()) {
            TMDbRepoEntry entry = (TMDbRepoEntry) tmdbRepoMap.get(id);
            if (entry.getSyncstate().getSyncStateFor(criteria) >= 0)
                movies.add(entry.getMovie());
        }
        return movies;
    }

    /**
     * Get syncState for a specific movie from the repository.
     *
     * @param movieId
     * @param criteria
     * @return int count of syncstate. -1 if movie is not existent.
     */
    @Override
    public synchronized int getMovieSyncStateByCriteria(int movieId, String criteria) {
        return tmdbRepoMap.containsKey(movieId) ?
                tmdbRepoMap.get(movieId).getSyncstate().getSyncStateFor(criteria) :
                -1;
    }

    /**
     * Get Count of all movies in the Repository
     *
     * @return
     */
    @Override
    public int getCount() {
        return tmdbRepoMap.size();
    }

    /**
     * Get Count of all movies int the Repository by criteria
     *
     * @param criteria ListType
     * @return int count by criteria
     */
    @Override
    public int getCountByCriteria(String criteria) {
        return getMoviesByCriteria(criteria).size();
    }

    @Override
    public void updateMovieSyncStatus(Integer id, String criteria, int syncStatus) {
        if (tmdbRepoMap.containsKey(id)){
            tmdbRepoMap.get(id).getSyncstate().updateSyncStateFor(criteria, syncStatus);
        }
    }

    @Override
    public HashMap<String, Integer> getLatestSyncStatus() {
        HashMap<String, Integer> latestSyncStatus = new HashMap<>();
        for (Integer id : tmdbRepoMap.keySet()) {
            TMDbRepoEntry entry = (TMDbRepoEntry) tmdbRepoMap.get(id);
            HashMap<String, Integer> syncState = entry.getSyncstate().getCompleteSyncState();
            for (String crit: syncState.keySet()){
                if (latestSyncStatus.containsKey(crit) && syncState.get(crit) < latestSyncStatus.get(crit))
                    continue;
                latestSyncStatus.put(crit, syncState.get(crit));
            }
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
        if (tmdbRepoMap.containsKey(movieId)) {
        }
    }
}

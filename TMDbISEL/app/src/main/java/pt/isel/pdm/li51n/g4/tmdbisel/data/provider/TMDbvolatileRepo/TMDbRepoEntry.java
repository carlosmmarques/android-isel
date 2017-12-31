package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbvolatileRepo;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.IRepoEntry;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.IRepoEntrySyncState;

/**
 * Movie and sync state wrapper for Repository
 */
public class TMDbRepoEntry implements IRepoEntry {

    private Movie movie;
    private IRepoEntrySyncState syncState;

    private TMDbRepoEntry() {
    }

    private TMDbRepoEntry(Movie movie, IRepoEntrySyncState syncState){
        this.movie = movie;
        this.syncState = syncState;
    }

    /**
     * static factory Method
     *
     * @return repository instance
     */
    public static TMDbRepoEntry newInstance() {
        return new TMDbRepoEntry();
    }

    /**
     * static factory Method
     *
     * @return repository instance
     */
    public static TMDbRepoEntry newInstance(Movie movie, IRepoEntrySyncState syncState) {
        return new TMDbRepoEntry(movie, syncState);
    }

    @Override
    public Movie getMovie() {
        return movie;
    }

    @Override
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public IRepoEntrySyncState getSyncstate() {
        return syncState;
    }

    @Override
    public void setSyncState(IRepoEntrySyncState syncState) {
        this.syncState = syncState;
    }

    /**
     * Remove the syncState for this Criterua
     *
     * @param criteria
     */
    @Override
    public void removeSyncState(String criteria) {
        syncState.removeSyncstateForCriteria(criteria);
    }
}

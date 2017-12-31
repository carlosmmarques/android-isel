package pt.isel.pdm.li51n.g4.tmdbisel.data.provider;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;

/**
 * Wrapper for IMovie / Sync Status
 */
public interface IRepoEntry {

    /**
     * Return the IMovie instance from the Entry
     * @return movie
     */
    Movie getMovie();

    /**
     * Sets the IMovie instance on this Repository Entry
     * @param movie movie
     */
    void setMovie(Movie movie);

    /**
     * Returns the SyncState of this Repository Entry.
     * @return syncstate
     */
    IRepoEntrySyncState getSyncstate();

    /**
     * set the SyncState on this Repository Entry.
     * @param syncState syncstate
     */
    void setSyncState(IRepoEntrySyncState syncState);

    /**
     * Remove the syncState for this Criterua
     *
     * @param criteria criteria
     */
    void removeSyncState(String criteria);
}

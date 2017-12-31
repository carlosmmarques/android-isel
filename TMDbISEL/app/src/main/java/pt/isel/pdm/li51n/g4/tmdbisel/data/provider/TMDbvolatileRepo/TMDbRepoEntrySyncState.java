package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbvolatileRepo;

import android.support.annotation.NonNull;

import java.util.HashMap;

import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.IRepoEntrySyncState;

public class TMDbRepoEntrySyncState implements IRepoEntrySyncState {

    /**
     * String / Integer Map containing the set of criteria and update of an entry.
     */
    private final HashMap<String, Integer> syncState = new HashMap<>();

    /**
     * private parameterless constructor. There's no point in extending this class
     */
    private TMDbRepoEntrySyncState() {
    }

    /**
     * Constructor
     *
     * @param setOfCriteria - Accepts a set of criteria. For a single criteria pass "<CRITERIA>"
     */
    public TMDbRepoEntrySyncState(String[] setOfCriteria) {
        for (String s : setOfCriteria) {
            syncState.put(s, 0);
        }
    }

    /**
     * Get the sync state (int) for the specified Criteria
     *
     * @param criteria
     * @return int >=0 if found, -1 if it doesn't
     */
    @Override
    public int getSyncStateFor(@NonNull String criteria) {
        try {
            return syncState.get(criteria);
        } catch (NullPointerException e) {
            return -1;
        }

    }

    /**
     * Update the sync state for this specific criteria.
     *
     * @param criteria
     */
    @Override
    public synchronized void updateSyncStateFor(String criteria) {
        if (!syncState.containsKey(criteria)) {
            syncState.put(criteria, 0);
            return;
        }
        syncState.put(criteria, syncState.get(criteria) + 1);
    }

    /**
     * Update syncstatus for the specified criteria to the specified syncStatus
     *
     * @param criteria
     * @param syncStatus
     */
    @Override
    public void updateSyncStateFor(String criteria, int syncStatus) {
        if (!syncState.containsKey(criteria)) {
            syncState.put(criteria, 0);
            return;
        }
        syncState.put(criteria, syncStatus);
    }

    /**
     * retrieve all the entries for this sync state map
     *
     * @return
     */
    @Override
    public HashMap<String, Integer> getCompleteSyncState() {
        return syncState;
    }

    /**
     * Checks for existance of syncState for this Criteria:
     *
     * @param criteria
     * @return
     */
    @Override
    public boolean hasSyncStateForCriteria(String criteria) {
        return syncState.containsKey(criteria);
    }

    /**
     * Remove the SyncState fro this Criteria
     *
     * @param criteria
     */
    @Override
    public void removeSyncstateForCriteria(String criteria) {
        syncState.remove(criteria);
    }
}

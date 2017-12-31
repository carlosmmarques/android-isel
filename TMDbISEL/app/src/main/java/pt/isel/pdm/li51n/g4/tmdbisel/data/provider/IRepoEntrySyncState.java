package pt.isel.pdm.li51n.g4.tmdbisel.data.provider;

import java.util.HashMap;

/**
 * Synchronization State representation
 */
public interface IRepoEntrySyncState {

    /**
     * Obtain SyncState for the specified criteria
     * @param criteria
     * @return int - indicator of last update
     */
    int getSyncStateFor(String criteria);

    /**
     * update SyncState for the specified Criteria.
     * @param criteria
     */
    void updateSyncStateFor(String criteria);

    /**
     * Update syncstatus for the specified criteria to the specified syncStatus
     * @param criteria
     * @param syncStatus
     */
    void updateSyncStateFor(String criteria, int syncStatus);

    /**
     * retrieve all the entries for this sync state map
     * @return
     */
    HashMap<String, Integer> getCompleteSyncState();

    /**
     * Checks for existance of syncState for this Criteria:
     *
     * @param criteria
     * @return
     */
    boolean hasSyncStateForCriteria(String criteria);

    /**
     * Remove the SyncState fro this Criteria
     *
     * @param criteria
     */
    void removeSyncstateForCriteria(String criteria);
}

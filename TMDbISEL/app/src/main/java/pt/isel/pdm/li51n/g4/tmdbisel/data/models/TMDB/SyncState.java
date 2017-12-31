package pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB;

import android.database.Cursor;

import java.util.HashMap;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;

public class SyncState {

    public Integer id;

    @Attributes(foreignTable = "Movie")
    public Integer movieId;

    public Integer criteria;

    public String version;

    @Attributes(compositeKey = "movieId, criteria", notMapped = true)
    public int primaryKey;

    public SyncState(Integer movieId, Integer criteria, String version) {
        this.movieId = movieId;
        this.criteria = criteria;
        this.version = version;
    }

    public SyncState(Integer movieId, Integer criteria) {
        this.movieId = movieId;
        this.criteria = criteria;
    }

    public SyncState(Cursor cursor) {
        HashMap<String, Integer> columnsMap = new HashMap<>();
        for(String columnName : cursor.getColumnNames())
            columnsMap.put(columnName, cursor.getColumnIndex(columnName));
        this.id = cursor.getInt(columnsMap.get("_id"));
        this.criteria = cursor.getInt(columnsMap.get("criteria"));
        this.movieId = cursor.getInt(columnsMap.get("movieId"));
        this.version = cursor.getString(columnsMap.get("version"));
    }
}

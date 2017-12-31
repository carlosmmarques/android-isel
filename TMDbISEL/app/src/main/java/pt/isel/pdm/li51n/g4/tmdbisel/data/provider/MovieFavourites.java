package pt.isel.pdm.li51n.g4.tmdbisel.data.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashSet;
import java.util.Set;

import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;

public class MovieFavourites {

    private static final String TAG_LOG_ERROR = "ERROR";
    private static final String TAG_LOG_DEBUG = "DEBUG";
    private static final String TAG_LOG_CLASS = MovieFavourites.class.getName();
    private static final String FAV_FILE_NAME = "moviefavs";
    SharedPreferences sharedPrefs = null;
    private Set<Integer> ids = new HashSet<>();

    public MovieFavourites() {
    }

    // Returns false if ID already present
    public boolean add(final int id) {
        return ids.add(id);
    }

    // Returns false if ID not present
    public boolean remove(final int id) {
        return ids.remove(id);
    }

    public boolean contains(final int id) {
        return ids.contains(id);
    }

    public Set<Integer> getAll() {
        return ids;
    }

    public void commit() {
        if (sharedPrefs != null)
            saveList();
    }

    public void clear() {
        ids.clear();
    }

    public void list() {
        for (Integer i : ids) {
            Log.d("FAVS", "Elem: " + i);
        }
    }

    private void loadList() {
        Gson gson = new Gson();
        String json = sharedPrefs.getString(MovieFavourites.class.getSimpleName(), TAG_LOG_ERROR);
        if (!json.equals(TAG_LOG_ERROR)) {
            ids = gson.fromJson(json, new TypeToken<HashSet<Integer>>() {
            }.getType());
        }
        Log.d(TAG_LOG_DEBUG, TAG_LOG_CLASS + "Obtained list size: " + ids.size());
    }

    private void saveList() {
        Log.d(TAG_LOG_DEBUG, TAG_LOG_CLASS + "Closing!");
        Gson gson = new Gson();
        String json = gson.toJson(ids, new TypeToken<HashSet<Integer>>() {
        }.getType());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(MovieFavourites.class.getSimpleName(), json);
        editor.apply();
    }

    public void setContext(TMDbISELApplication context) {
        Log.d(TAG_LOG_DEBUG, TAG_LOG_CLASS + "SharedPreferences File: " + TAG_LOG_CLASS + FAV_FILE_NAME);
        sharedPrefs = context.getSharedPreferences(TAG_LOG_CLASS + FAV_FILE_NAME, Context.MODE_PRIVATE);
        Log.d(TAG_LOG_DEBUG, TAG_LOG_CLASS + "SharedPreferences: " + sharedPrefs.toString());
        loadList();
    }
}

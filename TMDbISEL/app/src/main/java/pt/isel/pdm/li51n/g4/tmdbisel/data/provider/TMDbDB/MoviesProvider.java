package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbDB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.sql.SQLException;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.schema.TMDbSchema;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;

public class MoviesProvider extends ContentProvider {

    private static final String TAG = MoviesProvider.class.getSimpleName();
    // helper constants for use with the UriMatcher
    private static final int MOVIE_LIST = 1;
    private static final int MOVIE_ID = 2;
    private static final int MOVIE_LIST_BY_CRITERIA = 3;
    private static final int REVIEW_LIST = 10;
    private static final int REVIEW_ID = 11;
    private static final int PRODUCTION_COMPANY_LIST = 20;
    private static final int PRODUCTION_COMPANY_ID = 21;
    private static final int SYNCSTATE = 30;
    private static final int SYNCSTATE_ID = 31;


    private static final int ADD_AND_UPDATE_ALL_WITH_CRITERIA = 100;

    private static final UriMatcher URI_MATCHER;

    //prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "movies", MOVIE_LIST);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "movies/#", MOVIE_ID);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "movieListByCriteria/", MOVIE_LIST_BY_CRITERIA);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "reviews", REVIEW_LIST);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "review/#", REVIEW_ID);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "productionCompanies", PRODUCTION_COMPANY_LIST);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "productionCompanies/#", PRODUCTION_COMPANY_ID);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "syncstate", SYNCSTATE);
        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "syncstate/#", SYNCSTATE_ID);

        URI_MATCHER.addURI(TMDbDBContract.AUTHORITY, "addAndUpdateAllWithCriteria", ADD_AND_UPDATE_ALL_WITH_CRITERIA);
    }

    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<>();
    // database
    private volatile TMDbDatabaseHelper database;

    @Override
    public boolean onCreate() {
        Logger.d(TAG, "Initialize MoviesProvider");
        database = new TMDbDatabaseHelper(getContext());
        Logger.d(TAG, "ENDS Initialize MoviesProvider");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                builder.setTables(TMDbSchema.Movies.getTableName());
                break;
            case MOVIE_LIST_BY_CRITERIA:
                builder.setTables(TMDbDatabaseHelper.GET_MOVIES_BY_CRITERIA_TABLENAME);
                builder.appendWhere("criteria = ?");
                break;
            case MOVIE_ID:
                builder.setTables(TMDbSchema.Movies.getTableName());
                builder.appendWhere("_id = " + uri.getLastPathSegment());
                break;
            case REVIEW_LIST:
                builder.setTables(TMDbSchema.Reviews.getTableName());
                builder.appendWhere("movieId = ?");
                break;

            case PRODUCTION_COMPANY_LIST:
                builder.setTables(TMDbSchema.ProductionCompany.getTableName());
                builder.appendWhere("movieId = ?");
                break;
            case SYNCSTATE:
                builder.setTables(TMDbSchema.SyncState.getTableName());
                for (String paramName : uri.getQueryParameterNames()) {
                    builder.appendWhere(paramName + "=" + uri.getQueryParameter(paramName));
                }
                break;
            default:
                return null;
        }
        cursor = builder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                return TMDbDBContract.MovieContract.CONTENT_TYPE;
            case MOVIE_ID:
                return TMDbDBContract.MovieContract.CONTENT_ITEM_TYPE;
            case REVIEW_LIST:
                return TMDbDBContract.ReviewContract.CONTENT_TYPE;
            case REVIEW_ID:
                return TMDbDBContract.ReviewContract.CONTENT_ITEM_TYPE;
            case SYNCSTATE:
                return TMDbDBContract.SyncStateContract.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != MOVIE_LIST &&
                URI_MATCHER.match(uri) != REVIEW_ID &&
                URI_MATCHER.match(uri) != REVIEW_LIST &&
                URI_MATCHER.match(uri) != PRODUCTION_COMPANY_LIST &&
                URI_MATCHER.match(uri) != SYNCSTATE) {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
        SQLiteDatabase db = database.getWritableDatabase();
        long id = -1;

        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                id = db.insertWithOnConflict(TMDbSchema.Movies.getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;
            case MOVIE_ID:
                id = db.insertWithOnConflict(TMDbSchema.Movies.getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;
            case REVIEW_LIST:
                id = db.insertWithOnConflict(TMDbSchema.Reviews.getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;
            case PRODUCTION_COMPANY_LIST:
                id = db.insertWithOnConflict(TMDbSchema.ProductionCompany.getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;
            case SYNCSTATE:

                try {
                    db.beginTransaction();
                    id = db.insertWithOnConflict(TMDbSchema.SyncState.getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
                    ContentValues val = new ContentValues();
                    val.put("_id", id);
                    String where = "movieId = ? AND criteria = ?";
                    String[] args = {values.getAsString("movieId"), values.getAsString("criteria")};
                    db.update(TMDbSchema.SyncState.getTableName(), val, where, args);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
        }

        Uri res = null;
        try {
            res = getUriForId(id, uri);
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
//        db.close();
        return res;
    }

    private Uri getUriForId(long id, Uri uri) throws SQLException {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                getContext().getContentResolver().notifyChange(itemUri, null);
            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException("Problem while inserting into uri: " + uri);

    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

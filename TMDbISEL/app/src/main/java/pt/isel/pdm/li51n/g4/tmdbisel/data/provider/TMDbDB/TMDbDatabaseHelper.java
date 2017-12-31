package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.schema.TMDbSchema;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;

public class TMDbDatabaseHelper extends SQLiteOpenHelper {

    public static final String GET_MOVIES_BY_CRITERIA_TABLENAME = "MoviesByCriteria";
    public static final String GET_MOVIES_BY_CRITERIA_PROJECTION = TMDbSchema.Movies.getColumnsString();
    public static final String GET_MOVIES_BY_CRITERIA_DROP_VIEW = "DROP VIEW " + GET_MOVIES_BY_CRITERIA_TABLENAME + ";";
    public static final String GET_MOVIES_BY_CRITERIA_CREATE_VIEW = "CREATE VIEW IF NOT EXISTS " + GET_MOVIES_BY_CRITERIA_TABLENAME + " AS " +
            "SELECT * FROM " + TMDbSchema.Movies.getTableName() + " " +
            "INNER JOIN " + TMDbSchema.SyncState.getTableName() +
            " ON " + TMDbSchema.SyncState.getTableName() + ".movieId = " + TMDbSchema.Movies.getTableName() + "._id;";
    private static final String TAG = TMDbDatabaseHelper.class.getSimpleName();


    public TMDbDatabaseHelper(Context context) {
        super(context, TMDbSchema.DB_NAME, null, TMDbSchema.DB_VERSION);
        Logger.d(TAG, "Default Contructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "Begin onCreate");
        createDb(db);
        Logger.d(TAG, "Ends onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteDb(db);
        createDb(db);
    }

    private void deleteDb(SQLiteDatabase db) {

        try {
            db.execSQL(GET_MOVIES_BY_CRITERIA_DROP_VIEW);

            db.execSQL(TMDbSchema.MGA.getDropTableDDL());

            db.execSQL(TMDbSchema.SyncState.getDropTableDDL());

            db.execSQL(TMDbSchema.SpokenLanguage.getDropTableDDL());
            db.execSQL(TMDbSchema.ProductionCountry.getDropTableDDL());
            db.execSQL(TMDbSchema.ProductionCompany.getDropTableDDL());
            db.execSQL(TMDbSchema.BelongsToCollection.getDropTableDDL());

            db.execSQL(TMDbSchema.Genre.getDropTableDDL());
            db.execSQL(TMDbSchema.Reviews.getDropTableDDL());
            db.execSQL(TMDbSchema.Movies.getDropTableDDL());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createDb(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(TMDbSchema.Movies.getCreateTableDDL());
            db.execSQL(TMDbSchema.Reviews.getCreateTableDDL());
            db.execSQL(TMDbSchema.Genre.getCreateTableDDL());

            db.execSQL(TMDbSchema.BelongsToCollection.getCreateTableDDL());
            db.execSQL(TMDbSchema.ProductionCompany.getCreateTableDDL());
            db.execSQL(TMDbSchema.ProductionCountry.getCreateTableDDL());
            db.execSQL(TMDbSchema.SpokenLanguage.getCreateTableDDL());

            db.execSQL(TMDbSchema.SyncState.getCreateTableDDL());

            db.execSQL(TMDbSchema.MGA.getCreateTableDDL());

            db.execSQL(GET_MOVIES_BY_CRITERIA_CREATE_VIEW);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

}

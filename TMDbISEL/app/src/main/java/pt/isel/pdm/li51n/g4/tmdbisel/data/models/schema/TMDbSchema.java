package pt.isel.pdm.li51n.g4.tmdbisel.data.models.schema;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.BelongsToCollection;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Genre;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.MovieGenreAssociation;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.ProductionCompany;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.ProductionCountry;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Review;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.SpokenLanguage;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.SyncState;

/**
 * TMDb Schema - Our Database Schema defenition
 */
public class TMDbSchema {

    /**
     * DB Name
     */
    public static final String DB_NAME = "tmdbisel.sqlite";

    /**
     * DB Version
     */
    public static final int DB_VERSION = 1;

    /**
     * Table Movie Schema
     */
    public static final TableSchema<Movie> Movies = new TableSchema<>(Movie.class);

    /**
     * Table Reviews Schema
     */
    public static final TableSchema<Review> Reviews = new TableSchema<>(Review.class);

    /**
     * Table Genre Schema
     */
    public static final TableSchema<Genre> Genre = new TableSchema<>(Genre.class);

    /**
     * Table BelongsToCollection Schema
     */
    public static final TableSchema<BelongsToCollection> BelongsToCollection = new TableSchema<>(BelongsToCollection.class);

    /**
     * Table ProductionCompany Schema
     */
    public static final TableSchema<ProductionCompany> ProductionCompany = new TableSchema<>(ProductionCompany.class);

    /**
     * Table ProductionCountry Schema
     */
    public static final TableSchema<ProductionCountry> ProductionCountry = new TableSchema<>(ProductionCountry.class);

    /**
     * Table SpokenLanguage Schema
     */
    public static final TableSchema<SpokenLanguage> SpokenLanguage = new TableSchema<>(SpokenLanguage.class);

    /**
     * Table MGA Schema
     */
    public static final TableSchema<MovieGenreAssociation> MGA = new TableSchema<>(MovieGenreAssociation.class);

    /**
     * Table SyncState Schema
     */
    public static final TableSchema<SyncState> SyncState = new TableSchema<>(SyncState.class);
}

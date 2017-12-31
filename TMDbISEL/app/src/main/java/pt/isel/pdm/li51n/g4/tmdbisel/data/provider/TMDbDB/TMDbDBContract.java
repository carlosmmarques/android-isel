package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbDB;

import android.content.ContentResolver;
import android.net.Uri;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.schema.TMDbSchema;

public final class TMDbDBContract {

    /**
     * The authority of the Movies Provider
     */
    public static final String AUTHORITY = "pt.isel.pdm.li51n.g4.tmdbisel.tmdbdb";

    /**
     * The content URI for the top-level Movies Provider authority
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * A selection clause for ID based queries.
     */
    public static final String SELECTION_ID_BASED = "_id = ? ";


    public static final class MovieContract {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TMDbDBContract.CONTENT_URI, "movies");
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI_BY_CRITERIA = Uri.withAppendedPath(TMDbDBContract.CONTENT_URI, "movieListByCriteria");
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.pt.isel.pdm.li51n.g4.tmdbisel.movies";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.pt.isel.pdm.li51n.g4.tmdbisel.movies";

        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = "title" + " ASC";
    }

    public static final class ReviewContract {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TMDbDBContract.CONTENT_URI, "reviews");
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.pt.isel.pdm.li51n.g4.tmdbisel.reviews";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.pt.isel.pdm.li51n.g4.tmdbisel.reviews";

        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = TMDbSchema.Reviews.getSortOrderDefault() + " ASC";
    }

    public static final class ProductionCompanyContract {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TMDbDBContract.CONTENT_URI, "productionCompanies");
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.pt.isel.pdm.li51n.g4.tmdbisel.productionCompanies";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.pt.isel.pdm.li51n.g4.tmdbisel.productionCompanies";

        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = TMDbSchema.ProductionCompany.getSortOrderDefault() + " ASC";
    }

    public static final class SyncStateContract {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(TMDbDBContract.CONTENT_URI, "syncstate");
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.pt.isel.pdm.li51n.g4.tmdbisel.syncstate";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.pt.isel.pdm.li51n.g4.tmdbisel.syncstate";

        /**
         * The default sort order for queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = TMDbSchema.Reviews.getSortOrderDefault() + " ASC";
    }



}

package pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class Movie implements Parcelable {

    @SuppressWarnings("unused")
    @Attributes(notMapped = true)
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Attributes(notMapped = true)
    private Boolean hasDetails = false;

    @Attributes(notMapped = true)
    @SerializedName("adult")
    @Expose
    private Boolean adult;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @Attributes(notMapped = true)
    @SerializedName("belongs_to_collection")
    @Expose
    private BelongsToCollection belongsToCollection;

    @SerializedName("budget")
    @Expose
    private String budget;

    @Attributes(notMapped = true)
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = new ArrayList<>();

    @SerializedName("homepage")
    @Expose
    private String homepage;

    @Attributes(primaryKey = true)
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("imdb_id")
    @Expose
    private String imdbId;

    @SerializedName("original_language")
    @Expose
    private String originalLanguage;

    @SerializedName("original_title")
    @Expose
    private String originalTitle;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("popularity")
    @Expose
    private Double popularity;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @Attributes(notMapped = true)
    @SerializedName("production_companies")
    @Expose
    private List<ProductionCompany> productionCompanies = new ArrayList<>();

    @Attributes(notMapped = true)
    @SerializedName("production_countries")
    @Expose
    private List<ProductionCountry> productionCountries = new ArrayList<>();

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("revenue")
    @Expose
    private Integer revenue;

    @SerializedName("runtime")
    @Expose
    private Integer runtime;

    @Attributes(notMapped = true)
    @SerializedName("spoken_languages")
    @Expose
    private List<SpokenLanguage> spokenLanguages = new ArrayList<>();

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("tagline")
    @Expose
    private String tagline;

    @Attributes(sortOrderDefault = true)
    @SerializedName("title")
    @Expose
    private String title;

    @Attributes(notMapped = true)
    @SerializedName("video")
    @Expose
    private Boolean video;

    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    @Attributes(notMapped = true)
    @SerializedName("reviews")
    @Expose
    private ReviewResults mReviews;


    /**
     * No args constructor for use in serialization
     *
     */
    public Movie() {
    }


    /**
     * Constructor using cursor
     * @param cursor
     */
    public Movie(Cursor cursor){
        HashMap<String, Integer> columnsMap = Utils.getColumnsMapFromCursor(cursor);

        this.backdropPath = cursor.getString(columnsMap.get("backdropPath"));
        this.budget = cursor.getString(columnsMap.get("budget"));
        this.homepage = cursor.getString(columnsMap.get("homepage"));
        this.id = cursor.getInt(columnsMap.get("_id"));
        this.imdbId = cursor.getString(columnsMap.get("imdbId"));
        this.originalLanguage = cursor.getString(columnsMap.get("originalLanguage"));
        this.originalTitle = cursor.getString(columnsMap.get("originalTitle"));
        this.overview = cursor.getString(columnsMap.get("overview"));
        this.popularity = cursor.getDouble(columnsMap.get("popularity"));
        this.posterPath = cursor.getString(columnsMap.get("posterPath"));
        this.releaseDate = cursor.getString(columnsMap.get("releaseDate"));
        this.revenue = cursor.getInt(columnsMap.get("revenue"));
        this.runtime = cursor.getInt(columnsMap.get("runtime"));
        this.status = cursor.getString(columnsMap.get("status"));
        this.tagline = cursor.getString(columnsMap.get("tagline"));
        this.title = cursor.getString(columnsMap.get("title"));
        this.voteAverage = cursor.getDouble(columnsMap.get("voteAverage"));
        this.voteCount = cursor.getInt(columnsMap.get("voteCount"));
    }



    /**
     *
     * @param budget
     * @param genres
     * @param spokenLanguages
     * @param runtime
     * @param backdropPath
     * @param voteCount
     * @param id
     * @param title
     * @param releaseDate
     * @param posterPath
     * @param originalTitle
     * @param voteAverage
     * @param video
     * @param popularity
     * @param revenue
     * @param productionCountries
     * @param status
     * @param originalLanguage
     * @param adult
     * @param imdbId
     * @param homepage
     * @param overview
     * @param belongsToCollection
     * @param productionCompanies
     * @param tagline
     */
    public Movie(Boolean adult, String backdropPath,
                 BelongsToCollection belongsToCollection, String budget,
                 List<Genre> genres, String homepage, Integer id,
                 String imdbId, String originalLanguage, String originalTitle,
                 String overview, Double popularity,
                 String posterPath, List<ProductionCompany> productionCompanies,
                 List<ProductionCountry> productionCountries, String releaseDate,
                 Integer revenue, Integer runtime, List<SpokenLanguage> spokenLanguages,
                 String status, String tagline, String title, Boolean video, Double voteAverage,
                 Integer voteCount, ReviewResults reviews) {
        this.adult = adult;
        this.backdropPath = backdropPath;
        this.belongsToCollection = belongsToCollection;
        this.budget = budget;
        this.genres = genres;
        this.homepage = homepage;
        this.id = id;
        this.imdbId = imdbId;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.productionCompanies = productionCompanies;
        this.productionCountries = productionCountries;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.runtime = runtime;
        this.spokenLanguages = spokenLanguages;
        this.status = status;
        this.tagline = tagline;
        this.title = title;
        this.video = video;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.mReviews = reviews;
    }

    protected Movie(Parcel in) {
        byte hasDetailsVal = in.readByte();
        hasDetails = hasDetailsVal == 0x02 ? null : hasDetailsVal != 0x00;
        byte adultVal = in.readByte();
        adult = adultVal == 0x02 ? null : adultVal != 0x00;
        backdropPath = in.readString();
        belongsToCollection = (BelongsToCollection) in.readValue(BelongsToCollection.class.getClassLoader());
//        budget = in.readByte() == 0x00 ? null : in.readInt();
        budget = in.readString();
        if (in.readByte() == 0x01) {
            genres = new ArrayList<>();
            in.readList(genres, Genre.class.getClassLoader());
        } else {
            genres = null;
        }
        homepage = in.readString();
        id = in.readByte() == 0x00 ? null : in.readInt();
        imdbId = in.readString();
        originalLanguage = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        popularity = in.readByte() == 0x00 ? null : in.readDouble();
        posterPath = in.readString();
        if (in.readByte() == 0x01) {
            productionCompanies = new ArrayList<>();
            in.readList(productionCompanies, ProductionCompany.class.getClassLoader());
        } else {
            productionCompanies = null;
        }
        if (in.readByte() == 0x01) {
            productionCountries = new ArrayList<>();
            in.readList(productionCountries, ProductionCountry.class.getClassLoader());
        } else {
            productionCountries = null;
        }
        releaseDate = in.readString();
        revenue = in.readByte() == 0x00 ? null : in.readInt();
        runtime = in.readByte() == 0x00 ? null : in.readInt();
        if (in.readByte() == 0x01) {
            spokenLanguages = new ArrayList<>();
            in.readList(spokenLanguages, SpokenLanguage.class.getClassLoader());
        } else {
            spokenLanguages = null;
        }
        status = in.readString();
        tagline = in.readString();
        title = in.readString();
        byte videoVal = in.readByte();
        video = videoVal == 0x02 ? null : videoVal != 0x00;
        voteAverage = in.readByte() == 0x00 ? null : in.readDouble();
        voteCount = in.readByte() == 0x00 ? null : in.readInt();
        mReviews = (ReviewResults) in.readValue(ReviewResults.class.getClassLoader());
    }

    /**
     *
     * @return
     *     The adult
     */
    public Boolean getAdult() {
        return adult;
    }

    /**
     *
     * @param adult
     *     The adult
     */
    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    /**
     *
     * @return
     *     The backdropPath
     */
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     *
     * @param backdropPath
     *     The backdrop_path
     */
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /**
     *
     * @return
     *     The belongsToCollection
     */
    public BelongsToCollection getBelongsToCollection() {
        return belongsToCollection;
    }

    /**
     *
     * @param belongsToCollection
     *     The belongs_to_collection
     */
    public void setBelongsToCollection(BelongsToCollection belongsToCollection) {
        this.belongsToCollection = belongsToCollection;
    }

    /**
     *
     * @return
     *     The budget
     */
    public String getBudget() {
        return budget;
    }

    /**
     *
     * @param budget
     *     The budget
     */
    public void setBudget(String budget) {
        this.budget = budget;
    }

    /**
     *
     * @return
     *     The genres
     */
    public List<Genre> getGenres() {
        return genres;
    }

    /**
     *
     * @param genres
     *     The genres
     */
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    /**
     *
     * @return
     *     The homepage
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     *
     * @param homepage
     *     The homepage
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     *
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The imdbId
     */
    public String getImdbId() {
        return imdbId;
    }

    /**
     *
     * @param imdbId
     *     The imdb_id
     */
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    /**
     *
     * @return
     *     The originalLanguage
     */
    public String getOriginalLanguage() {
        return originalLanguage;
    }

    /**
     *
     * @param originalLanguage
     *     The original_language
     */
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    /**
     *
     * @return
     *     The originalTitle
     */
    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     *
     * @param originalTitle
     *     The original_title
     */
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    /**
     *
     * @return
     *     The overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     *
     * @param overview
     *     The overview
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     *
     * @return
     *     The popularity
     */
    public Double getPopularity() {
        return popularity;
    }

    /**
     *
     * @param popularity
     *     The popularity
     */
    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    /**
     *
     * @return
     *     The posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     *
     * @param posterPath
     *     The poster_path
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     *
     * @return
     *     The productionCompanies
     */
    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    /**
     *
     * @param productionCompanies
     *     The production_companies
     */
    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    /**
     *
     * @return
     *     The productionCountries
     */
    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }

    /**
     *
     * @param productionCountries
     *     The production_countries
     */
    public void setProductionCountries(List<ProductionCountry> productionCountries) {
        this.productionCountries = productionCountries;
    }

    /**
     *
     * @return
     *     The releaseDate
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     *
     * @param releaseDate
     *     The release_date
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     *
     * @return
     *     The revenue
     */
    public Integer getRevenue() {
        return revenue;
    }

    /**
     *
     * @param revenue
     *     The revenue
     */
    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    /**
     *
     * @return
     *     The runtime
     */
    public Integer getRuntime() {
        return runtime;
    }

    /**
     *
     * @param runtime
     *     The runtime
     */
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    /**
     *
     * @return
     *     The spokenLanguages
     */
    public List<SpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }

    /**
     *
     * @param spokenLanguages
     *     The spoken_languages
     */
    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    /**
     *
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     *     The tagline
     */
    public String getTagline() {
        return tagline;
    }

    /**
     *
     * @param tagline
     *     The tagline
     */
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    /**
     *
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     *     The video
     */
    public Boolean getVideo() {
        return video;
    }

    /**
     *
     * @param video
     *     The video
     */
    public void setVideo(Boolean video) {
        this.video = video;
    }

    /**
     *
     * @return
     *     The voteAverage
     */
    public Double getVoteAverage() {
        return voteAverage;
    }

    /**
     *
     * @param voteAverage
     *     The vote_average
     */
    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     *
     * @return
     *     The voteCount
     */
    public Integer getVoteCount() {
        return voteCount;
    }

    /**
     * @param voteCount The vote_count
     */
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (hasDetails == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (hasDetails ? 0x01 : 0x00));
        }
        if (adult == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (adult ? 0x01 : 0x00));
        }
        dest.writeString(backdropPath);
        dest.writeValue(belongsToCollection);
//        if (budget == null) {
//            dest.writeByte((byte) (0x00));
//        } else {
//            dest.writeByte((byte) (0x01));
        dest.writeString(budget);
//        }
        if (genres == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(genres);
        }
        dest.writeString(homepage);
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(imdbId);
        dest.writeString(originalLanguage);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        if (popularity == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(popularity);
        }
        dest.writeString(posterPath);
        if (productionCompanies == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(productionCompanies);
        }
        if (productionCountries == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(productionCountries);
        }
        dest.writeString(releaseDate);
        if (revenue == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(revenue);
        }
        if (runtime == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(runtime);
        }
        if (spokenLanguages == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(spokenLanguages);
        }
        dest.writeString(status);
        dest.writeString(tagline);
        dest.writeString(title);
        if (video == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (video ? 0x01 : 0x00));
        }
        if (voteAverage == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(voteAverage);
        }
        if (voteCount == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(voteCount);
        }
        dest.writeValue(mReviews);
    }

    public void setHasDetails(Boolean bool){
        hasDetails = bool;
    }

    public boolean hasDetails() {
        return hasDetails;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                '}';
    }

    public List<Review> getReviews() {
        return mReviews.getResults();
    }

    public void setReviews(List<Review> reviews){
        if(mReviews == null)
            mReviews = new ReviewResults();
        mReviews.setResults(reviews);
    }

    public boolean hasReviews() {
        return getReviews().size() > 0;
    }
}
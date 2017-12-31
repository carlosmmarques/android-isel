package pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;


public class Review implements Parcelable {

    @SuppressWarnings("unused")
    @Attributes(notMapped = true)
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String movieId;

    @Attributes(primaryKey = true, sortOrderDefault = true)
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("url")
    @Expose
    private String url;

    /**
     * No args constructor for use in serialization
     */
    public Review() {
    }

    /**
     * @param content
     * @param id
     * @param author
     * @param url
     */
    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }


    public Review(Parcel in) {
        movieId = in.readString();
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public Review(Cursor cursor) {
        HashMap<String, Integer> columnsMap = Utils.getColumnsMapFromCursor(cursor);
        this.id = cursor.getString(columnsMap.get("_id"));
        this.author = cursor.getString(columnsMap.get("author"));
        this.content = cursor.getString(columnsMap.get("content"));
        this.url = cursor.getString(columnsMap.get("url"));
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    public Review withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * @return The author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author The author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    public Review withAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * @return The content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    public Review withContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public Review withUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }
}
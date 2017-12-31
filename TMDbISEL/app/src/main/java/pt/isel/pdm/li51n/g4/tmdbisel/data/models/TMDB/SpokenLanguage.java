package pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;

public class SpokenLanguage implements Parcelable {

    @SuppressWarnings("unused")
    @Attributes(notMapped = true)
    public static final Parcelable.Creator<SpokenLanguage> CREATOR = new Parcelable.Creator<SpokenLanguage>() {
        @Override
        public SpokenLanguage createFromParcel(Parcel in) {
            return new SpokenLanguage(in);
        }

        @Override
        public SpokenLanguage[] newArray(int size) {
            return new SpokenLanguage[size];
        }
    };
    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;
    @SerializedName("name")
    @Expose
    private String name;
    @Attributes(primaryKey = true, autoIncrement = true)
    private Integer id;

    /**
     * No args constructor for use in serialization
     *
     */
    public SpokenLanguage() {
    }

    /**
     *
     * @param iso6391
     * @param name
     */
    public SpokenLanguage(String iso6391, String name) {
        this.iso6391 = iso6391;
        this.name = name;
    }

    protected SpokenLanguage(Parcel in) {
        iso6391 = in.readString();
        name = in.readString();
    }

    /**
     *
     * @return
     *     The iso6391
     */
    public String getIso6391() {
        return iso6391;
    }

    /**
     *
     * @param iso6391
     *     The iso_639_1
     */
    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iso6391);
        dest.writeString(name);
    }
}
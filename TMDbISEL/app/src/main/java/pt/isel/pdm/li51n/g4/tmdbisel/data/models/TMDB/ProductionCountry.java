package pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;

public class ProductionCountry implements Parcelable {

    @SuppressWarnings("unused")
    @Attributes(notMapped = true)
    public static final Parcelable.Creator<ProductionCountry> CREATOR = new Parcelable.Creator<ProductionCountry>() {
        @Override
        public ProductionCountry createFromParcel(Parcel in) {
            return new ProductionCountry(in);
        }

        @Override
        public ProductionCountry[] newArray(int size) {
            return new ProductionCountry[size];
        }
    };
    @SerializedName("iso_3166_1")
    @Expose
    private String iso31661;
    @SerializedName("name")
    @Expose
    private String name;
    @Attributes(primaryKey = true, autoIncrement = true)
    private Integer id;

    /**
     * No args constructor for use in serialization
     *
     */
    public ProductionCountry() {
    }

    /**
     *
     * @param iso31661
     * @param name
     */
    public ProductionCountry(String iso31661, String name) {
        this.iso31661 = iso31661;
        this.name = name;
    }

    protected ProductionCountry(Parcel in) {
        iso31661 = in.readString();
        name = in.readString();
    }

    /**
     *
     * @return
     *     The iso31661
     */
    public String getIso31661() {
        return iso31661;
    }

    /**
     *
     * @param iso31661
     *     The iso_3166_1
     */
    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
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
        dest.writeString(iso31661);
        dest.writeString(name);
    }
}
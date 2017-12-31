package pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class ProductionCompany implements Parcelable {

    @SuppressWarnings("unused")
    @Attributes(notMapped = true)
    public static final Parcelable.Creator<ProductionCompany> CREATOR = new Parcelable.Creator<ProductionCompany>() {
        @Override
        public ProductionCompany createFromParcel(Parcel in) {
            return new ProductionCompany(in);
        }

        @Override
        public ProductionCompany[] newArray(int size) {
            return new ProductionCompany[size];
        }
    };

    @Attributes(compositeKey = "movieId, name", notMapped = true)
    public int primaryKey;

    public String movieId;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private Integer id;

    /**
     * No args constructor for use in serialization
     *
     */
    public ProductionCompany() {
    }

    public ProductionCompany(Cursor cursor) {
        HashMap<String, Integer> columnsMap = Utils.getColumnsMapFromCursor(cursor);
        this.name = cursor.getString(columnsMap.get("name"));
        this.id = Integer.parseInt(cursor.getString(columnsMap.get("_id")));
    }

    /**
     *
     * @param id
     * @param name
     */
    public ProductionCompany(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    protected ProductionCompany(Parcel in) {
        name = in.readString();
        id = in.readByte() == 0x00 ? null : in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
    }
}
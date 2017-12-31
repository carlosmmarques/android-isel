package pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReviewResults extends BaseResults implements Parcelable {


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ReviewResults> CREATOR = new Parcelable.Creator<ReviewResults>() {
        @Override
        public ReviewResults createFromParcel(Parcel in) {
            return new ReviewResults(in);
        }

        @Override
        public ReviewResults[] newArray(int size) {
            return new ReviewResults[size];
        }
    };
    @SerializedName("results")
    @Expose
    private List<Review> results = new ArrayList<>();

    protected ReviewResults() {

    }

    protected ReviewResults(Parcel in) {
        if (in.readByte() == 0x01) {
            results = new ArrayList<>();
            in.readList(results, Review.class.getClassLoader());
        } else {
            results = null;
        }
    }

    /**
     * @return The results
     */
    public List<Review> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<Review> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (results == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(results);
        }
    }
}
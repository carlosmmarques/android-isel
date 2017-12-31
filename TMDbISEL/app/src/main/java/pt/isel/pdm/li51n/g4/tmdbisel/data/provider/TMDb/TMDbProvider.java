package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Headers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.MovieResults;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Review;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.ReviewResults;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.MovieInfoProvider;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.workers.DownloadThread;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static java.lang.System.currentTimeMillis;

public class TMDbProvider implements MovieInfoProvider{

    private static String rateLimitRemaining = "";
    private static long beginRequestsTime;
    private final String TAG = this.getClass().getSimpleName();
    private final TMDbWebApi serviceAPI;
    private final DataMapper mapper;


    public TMDbProvider(){

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDbWebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        serviceAPI = retrofit.create(TMDbWebApi.class);

        mapper = new DataMapper();

    }


    /**
     * Asynchonous method to get the movie list from the selected type.
     *
     * @param type               Movie List Type
     * @param language           Movie List language
     * @param query              Text to search
     * @param completionCallback The callback to be executed once the operation is completed, either successfully or in failure.
     */
    @Override
    public void getMovieListInfoAsync(@NonNull String type, @NonNull String language, @Nullable String query, @NonNull DownloadThread.Callback completionCallback) {

    }

    /**
     * Method to get the movie list from the selected type.
     *
     * @param type     Movie List Type
     * @param language Movie List language
     * @param query    Text to search
     * @param pageNumber
     * @return The resulting Movie List
     * @throws TMDbAPIRateLimitException, IOException If an error occurred while fetching the Movie List information.
     */
    @Override
    public List<Movie> getMovieListInfo(@NonNull String type, @NonNull String language, @Nullable String query, int pageNumber)
            throws TMDbAPIRateLimitException, IOException {

        Logger.d(TAG, String.format("Getting Movies List of %s, in %s", type, language));
        Call<MovieResults> callSync;
        String page = String.valueOf(pageNumber);
        switch(Integer.parseInt(type)){
            case (Constants.MOVIES_NOW_PLAYING):
                callSync = serviceAPI.getMoviesNowPlaying(TMDbWebApi.APY_KEY, language, page);
                break;
            case (Constants.MOVIES_UPCOMING):
                callSync = serviceAPI.getUpcomingMovies(TMDbWebApi.APY_KEY, language, page);
                break;
            case (Constants.MOVIES_MOST_POPULAR):
                callSync = serviceAPI.getTopRatedMovies(TMDbWebApi.APY_KEY, language, page);
                break;
            case (Constants.MOVIES_SEARCH):
                callSync = serviceAPI.getMovieSearch(TMDbWebApi.APY_KEY, query, language, page);
                break;
            default:
                return new ArrayList<>();
        }

        Response<MovieResults> response = callSync.execute();
        Headers headers = response.headers();
        verifyRateLimit(headers);

        MovieResults movieResults = response.body();

        return movieResults != null ? mapper.convertToList(movieResults) : new ArrayList<Movie>();


    }

    /**
     * Asynchonous method to get the movie info from the given id.
     *
     * @param id                 The movie id
     * @param language           Movie language
     * @param completionCallback The callback to be executed once the operation is completed, either successfully or in failure.
     */
    @Override
    public void getMovieInfoAsync(@NonNull String id, @NonNull String language, @NonNull DownloadThread.Callback completionCallback) {

    }

    /**
     * Method to get the movie info from the given id.
     *
     * @param id       The movie id.
     * @param language Movie language
     * @return The resulting Movie information
     * @throws TMDbAPIRateLimitException, IOException If an error occurred while fetching the Movie List information.
     */
    @Override
    public Movie getMovieInfo(@NonNull String id, @NonNull String language) throws TMDbAPIRateLimitException, IOException, TMDbAPIConnectionException {
        Logger.d(TAG, String.format("Getting Movie with id: %s, in %s", id, language));
        Call<Movie> callSync = serviceAPI.getMovieByIdWithReviews(Integer.parseInt(id), TMDbWebApi.APY_KEY, language, "reviews");
        Response<Movie> response;
        try{
            response = callSync.execute();
        } catch(Exception e){
            Logger.e(TAG, e);
            throw new TMDbAPIConnectionException("Something went wrong executing the callSync.execute() method!");
        }
        Headers headers = response.headers();
        verifyRateLimit(headers);

        return response.body();
    }

    /**
     * Method to get the Movie Reviews
     *
     * @param id       The movie id.
     * @param language Movie language
     * @return The Movie Reviews
     */
    @Override
    public List<Review> getReviewsByMovieId(@NonNull String id, @NonNull String language) throws IOException, TMDbAPIRateLimitException, TMDbAPIConnectionException {
        Logger.d(TAG, String.format("Getting Movie Reviews with id: %s, in %s", id, language));
        Call<ReviewResults> callSync = serviceAPI.getMovieReviewsById(id, TMDbWebApi.APY_KEY, language);
        Response<ReviewResults> response;
        try{
            response = callSync.execute();
        } catch(Exception e){
            Logger.e(TAG, e);
            throw new TMDbAPIConnectionException("Something went wrong executing the callSync.execute() method!");
        }
        Headers headers = response.headers();
        verifyRateLimit(headers);

        return response.body().getResults();
    }

    private void verifyRateLimit(Headers headers) throws TMDbAPIRateLimitException {
        rateLimitRemaining = headers.get("X-RateLimit-Remaining");
        if (rateLimitRemaining == null /*|| rateLimitRemaining.equals("0")*/) {
            throw new TMDbAPIRateLimitException("We ran out of requests from the API while getting movieInfo");
        }
        if (rateLimitRemaining.equals("39")) beginRequestsTime = currentTimeMillis();
        Logger.d(TAG, "number of requests left: " + rateLimitRemaining);
        Logger.d(TAG, "getMovieInfo:rateLimitRemaining = " + rateLimitRemaining);
    }
}

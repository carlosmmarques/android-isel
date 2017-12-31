package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb;


import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.MovieResults;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.ReviewResults;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TMDbWebApi {

    String BASE_URL = "http://api.themoviedb.org/3/";
    String APY_KEY = "b0cacca60a21f3cad6192dc3d942f18a";
    String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w500";
    // Base URL para imagens
    // Exemplo: http://image.tmdb.org/t/p/w500/lIv1QinFqz4dlp5U4lQ6HaiskOZ.jpg
    // See http://docs.themoviedb.apiary.io/#configuration

    /**
     * Gets Reviews by Movie id - Only in English
     * @param id Id of the Movie
     * @param key API_KEY
     * @return List of Movie Reviews
     */
    @GET("movie/{id}/reviews")
    Call<ReviewResults> getMovieReviewsById(@Path("id") String id, @Query("api_key") String key, @Query("language") String lang);

    /**
     * Search for movies by title.
     * Example: http://api.themoviedb.org/3/search/movie?api_key=b0cacca60a21f3cad6192dc3d942f18a&query=%22007%22
     * @return Search result
     */
    @GET("search/movie")
    Call<MovieResults> getMovieSearch(@Query("api_key") String key, @Query("query") String query, @Query("language") String lang, @Query("page") String page);

    /**
     * Get the list of movies playing that have been, or are being released this week. This list refreshes every day.
     * @return List of Playing Movies
     */
    @GET("movie/now_playing")
    Call<MovieResults> getMoviesNowPlaying(@Query("api_key") String key, @Query("language") String lang, @Query("page") String page);

    /**
     * Get the list of upcoming movies by release date. This list refreshes every day.
     * @return Upcoming MovieDTO
     */
    @GET("movie/upcoming")
    Call<MovieResults> getUpcomingMovies(@Query("api_key") String key, @Query("language") String lang, @Query("page") String page);

    /**
     * Get the list of top rated movies. By default, this list will only include movies that have 50 or more votes. This list refreshes every day.
     * @return Top Rated Movie
     */
    @GET("movie/top_rated")
    Call<MovieResults> getTopRatedMovies(@Query("api_key") String key, @Query("language") String lang, @Query("page") String page);

    /**
     * Get the movie information for a specific movie id.
     * @param id Movie id
     * @return Movie Object
     */
    @GET("movie/{id}")
    Call<Movie> getMovieById(@Path("id") Integer id, @Query("api_key") String key, @Query("language") String lang);

    /**
     * Get the movie information with Reviews for a specific movie id.
     *
     * @param id Movie id
     * @return Movie Object
     */
    @GET("movie/{id}")
    Call<Movie> getMovieByIdWithReviews(@Path("id") Integer id, @Query("api_key") String key, @Query("language") String lang, @Query("append_to_response") String appendToResponse);


}

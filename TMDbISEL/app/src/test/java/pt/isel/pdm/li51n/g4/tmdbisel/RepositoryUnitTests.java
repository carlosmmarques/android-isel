package pt.isel.pdm.li51n.g4.tmdbisel;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.MovieResults;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb.TMDbWebApi;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbvolatileRepo.TMDbVolatileRepo;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RepositoryUnitTests extends TestCase {

//    @Test
//    public void shouldCreate_Repository(){
//        IRepository repository = new TMDbVolatileRepo();
//    }

    public TMDbVolatileRepo repository = TMDbVolatileRepo.getInstance();

    public void test1_GetMovieByNameWithDualCriteria() throws IOException {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDbWebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final TMDbWebApi service = retrofit.create(TMDbWebApi.class);

        final Call<MovieResults> callSync = service.getMovieSearch(
                TMDbWebApi.APY_KEY,
                "Star Wars: Episode VI - Return of the Jedi",
                "en", "1");

        List<Movie> moviesFromAPI = callSync.execute().body().getResults();

        repository.addAndUpdateAllWithCriteria(moviesFromAPI, "SOME_CRITERIA");
        repository.addAndUpdateAllWithCriteria(moviesFromAPI, "SOME_OTHER_CRITERIA");

        List<Movie> moviesWithSomeCriteria = repository.getMoviesByCriteria("SOME_CRITERIA");
        List<Movie> moviesWithSomeOtherCriteria = repository.getMoviesByCriteria("SOME_OTHER_CRITERIA");
        Assert.assertTrue(moviesWithSomeCriteria != null && moviesWithSomeOtherCriteria != null);
        Assert.assertTrue(moviesFromAPI.get(0).getId().equals(repository.getMovie(moviesFromAPI.get(0).getId()).getId()));
        Assert.assertTrue(moviesWithSomeCriteria.contains(moviesFromAPI.get(0)));
        Assert.assertTrue(moviesWithSomeOtherCriteria.contains(moviesFromAPI.get(0)));
        Assert.assertTrue(
                moviesFromAPI.get(0)
                        .getTitle()
                        .equalsIgnoreCase("Star Wars: Episode VI - Return of the Jedi"));
    }

    public void test2_GetMovieByNameRefreshUpdateByCriteria() throws IOException {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDbWebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final TMDbWebApi service = retrofit.create(TMDbWebApi.class);

        final Call<MovieResults> callSync = service.getMovieSearch(
                TMDbWebApi.APY_KEY,
                "Star Wars: Episode VI - Return of the Jedi",
                "en", "1");

        List<Movie> moviesFromAPI = callSync.execute().body().getResults();

        repository.addAndUpdateAllWithCriteria(moviesFromAPI, "SOME_CRITERIA");

        List<Movie> moviesWithSomeCriteria = repository.getMoviesByCriteria("SOME_CRITERIA");
        List<Movie> moviesWithSomeOtherCriteria = repository.getMoviesByCriteria("SOME_OTHER_CRITERIA");
        Assert.assertTrue(moviesWithSomeCriteria != null && moviesWithSomeOtherCriteria != null);
        Assert.assertTrue(moviesFromAPI.get(0).getId().equals(repository.getMovie(moviesFromAPI.get(0).getId()).getId()));
        Assert.assertTrue(repository.getMovieSyncStateByCriteria(moviesWithSomeCriteria.get(0).getId(), "SOME_CRITERIA") == 1);
        Assert.assertTrue(repository.getMovieSyncStateByCriteria(moviesWithSomeOtherCriteria.get(0).getId(), "SOME_OTHER_CRITERIA") == 0);
        Assert.assertTrue(
                moviesFromAPI.get(0)
                        .getTitle()
                        .equalsIgnoreCase("Star Wars: Episode VI - Return of the Jedi"));
    }

    public void test3_GetMoviesNowPlaying_ReturnsResultDTO() throws IOException {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDbWebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final TMDbWebApi service = retrofit.create(TMDbWebApi.class);

        final Call<MovieResults> callSync = service.getMoviesNowPlaying(TMDbWebApi.APY_KEY, "en", "1");

        List<Movie> nowPlayingMoviesFromAPI = callSync.execute().body().getResults();

        repository.addAndUpdateAllWithCriteria(nowPlayingMoviesFromAPI, "NOW_PLAYING");

        List<Movie> nowPlayingMoviesFromRepo = repository.getMoviesByCriteria("NOW_PLAYING");

        Assert.assertNotNull(nowPlayingMoviesFromRepo);
        for (Movie m : nowPlayingMoviesFromAPI) {
            Assert.assertTrue(m.getId().equals(repository.getMovie(m.getId()).getId()));
            Assert.assertTrue(nowPlayingMoviesFromRepo.contains(m));
        }
    }

    public void test4_UpcomingMovies() throws IOException {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDbWebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final TMDbWebApi service = retrofit.create(TMDbWebApi.class);

        final Call<MovieResults> callSync = service.getUpcomingMovies(TMDbWebApi.APY_KEY, "en", "1");

        List<Movie> upcomingMoviesFromAPI = callSync.execute().body().getResults();

        repository.addAndUpdateAllWithCriteria(upcomingMoviesFromAPI, "UPCOMING");

        List<Movie> upcomingMoviesFromRepo = repository.getMoviesByCriteria("UPCOMING");

        Assert.assertNotNull(upcomingMoviesFromRepo);
        for (Movie m : upcomingMoviesFromAPI) {
            Assert.assertTrue(m.getId().equals(repository.getMovie(m.getId()).getId()));
            Assert.assertTrue(upcomingMoviesFromRepo.contains(m));
        }
    }

    public void test5_GetPopularMovies_ReturnsResultDTO() throws IOException {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDbWebApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final TMDbWebApi service = retrofit.create(TMDbWebApi.class);

        final Call<MovieResults> callSync = service.getTopRatedMovies(TMDbWebApi.APY_KEY, "en", "1");

        List<Movie> topRatedMoviesFromAPI = callSync.execute().body().getResults();

        repository.addAndUpdateAllWithCriteria(topRatedMoviesFromAPI, "TOP_RATED");

        List<Movie> topRatedMoviesFromRepo = repository.getMoviesByCriteria("TOP_RATED");

        Assert.assertNotNull(topRatedMoviesFromRepo);
        for (Movie m : topRatedMoviesFromAPI) {
            Assert.assertTrue(m.getId().equals(repository.getMovie(m.getId()).getId()));
            Assert.assertTrue(topRatedMoviesFromRepo.contains(m));
        }
    }
//
//    public void testGetMovieById_ReturnsMovie() throws IOException {
//        final Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(TMDbWebApi.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        final TMDbWebApi service = retrofit.create(TMDbWebApi.class);
//
//        final Call<Movie> callAsync = service.getMovieById(
//                1892,
//                TMDbWebApi.APY_KEY,
//                "en");
//
//        final Movie[] movieHolder = new Movie[1];
//
//        callAsync.enqueue(new Callback<Movie>() {
//            @Override
//            public void onResponse(Response<Movie> response, Retrofit retrofit) {
//                movieHolder[0] = response.body();
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.e("SanityTest", "call Failed!", t);
//            }
//        });
//
//        Assert.assertNotNull(movieHolder[0]);
//        Assert.assertEquals(
//                movieHolder[0].getTitle(),
//                "Star Wars: Episode VI - Return of the Jedi");
//    }
}
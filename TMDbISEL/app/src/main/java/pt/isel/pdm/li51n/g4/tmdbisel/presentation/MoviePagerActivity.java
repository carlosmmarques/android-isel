package pt.isel.pdm.li51n.g4.tmdbisel.presentation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.R;
import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.presentation.base.LoggingAppCompatActivity;

public class MoviePagerActivity extends LoggingAppCompatActivity {

    private static List<Movie> mMovies;
    ViewPager mViewPager;
    private  FragmentPagerAdapter mAdapter;
    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getBundleExtra(MovieFragment.EXTRA_BUNDLE);
        movieId = args.getInt(MovieFragment.EXTRA_MOVIE_ID);
        int listType = args.getInt(MovieFragment.EXTRA_MOVIE_LIST, TMDbISELApplication.mListType);

        new MovieListLoader().execute(listType);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public  class MovieListLoader extends AsyncTask<Integer, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Integer... params) {
            return TMDbISELApplication.MOVIE_REPOSITORY_CP.getMoviesByCriteria(String.valueOf(params[0]));
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mMovies = movies;
            FragmentManager fm = getSupportFragmentManager();
            mAdapter = new FragmentPagerAdapter(fm) {
                @Override
                public Fragment getItem(int position) {
                    int movieId = mMovies.get(position).getId();
                    Fragment fragment = MovieFragment.newInstance(String.valueOf(movieId));
                    return fragment;
                }



                @Override
                public int getCount() {
                    return mMovies.size();
                }
            };
            mViewPager.setAdapter(mAdapter);
            for(int i = 0; i < mMovies.size(); i++){
                if(mMovies.get(i).getId().equals(movieId)){
                    mViewPager.setCurrentItem(i);
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}

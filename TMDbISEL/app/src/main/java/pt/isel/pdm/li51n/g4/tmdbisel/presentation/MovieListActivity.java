package pt.isel.pdm.li51n.g4.tmdbisel.presentation;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import pt.isel.pdm.li51n.g4.tmdbisel.R;
import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.CustomConnectivityManager;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Preferences;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Toaster;
import pt.isel.pdm.li51n.g4.tmdbisel.presentation.about.AboutActivity;
import pt.isel.pdm.li51n.g4.tmdbisel.presentation.settings.SettingsActivity;
import pt.isel.pdm.li51n.g4.tmdbisel.services.MovieRefresherService;

public class MovieListActivity extends SingleFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LAST_SELECTED_MOVIE_POSITION = ".lastSelectedMoviePos";

    MovieListFragment mMovieListFragment;
    private Toolbar mToolbar;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(MovieRefresherService.EXTRA_RESULT);
                if (resultCode == RESULT_OK) {
                    Logger.d(TAG, "Broadcast received!!!!!");
                    Logger.d(TAG, "mMovieListFragment is Null? " + (mMovieListFragment == null));
                    if ((mMovieListFragment != null)
                            &&
                            (Integer.parseInt(bundle.getString(MovieRefresherService.EXTRA_MOVIE_LIST_TYPE)) == TMDbISELApplication.mListType)
                            ) {
                        mMovieListFragment.updateUI(String.valueOf(TMDbISELApplication.mListType), mMovieListFragment.mAdapter.getSelectedPos());
                    }
                }
            }
        }
    };

    @Override
    protected Fragment createFragment() {
        mMovieListFragment = new MovieListFragment();
        return mMovieListFragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set preferences defaults if there is no preferences whatsoever (from the correspondent XML resource file)
        Preferences.init(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentManager fm = getSupportFragmentManager();
        if (mMovieListFragment == null) {
            mMovieListFragment = (MovieListFragment) fm.findFragmentById(R.id.fragmentContainer);
        }
        int pos = -1;
        if (savedInstanceState != null)
            pos = savedInstanceState.getInt(LAST_SELECTED_MOVIE_POSITION);

        mMovieListFragment.updateUI(String.valueOf(TMDbISELApplication.mListType), pos);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(LAST_SELECTED_MOVIE_POSITION, mMovieListFragment.mAdapter.getSelectedPos());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(MovieRefresherService.NOTIFICATION));
        Logger.d(TAG, "Broadcast registered!!!");
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBroadcastReceiver);
        Logger.d(TAG, "Broadcast unregistered!!!");
        super.onPause();
    }

    public void onMovieSelected(Movie movie) {
        if (findViewById(R.id.detailFragmentContainer) == null) {
            // Portrait - start an instance of MoviePagerActivity
            Intent i = new Intent(this, MoviePagerActivity.class);
            Bundle args = new Bundle();
            args.putInt(MovieFragment.EXTRA_MOVIE_ID, movie.getId());
            args.putInt(MovieFragment.EXTRA_MOVIE_LIST, TMDbISELApplication.mListType);
            i.putExtra(MovieFragment.EXTRA_BUNDLE, args);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(i);
            }
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            cleanOldDetailFragment(fm, ft);
            Fragment newDetail = MovieFragment.newInstance(String.valueOf(movie.getId()));
            ft.add(R.id.detailFragmentContainer, newDetail, Constants.TAG_MOVIE_DETAIL_FRAGMENT);
            ft.commit();
        }
    }

    private void cleanOldDetailFragment(FragmentManager fm, FragmentTransaction ft) {
        Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
        if (oldDetail != null) {
            ft.remove(oldDetail);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        // http://codetheory.in/adding-search-to-android/
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchItem.collapseActionView();

                Context context = getApplicationContext();
                CustomConnectivityManager cConnMngr = CustomConnectivityManager.getInstance(context);
                if (!cConnMngr.hasAnyActiveConnection())
                    Toaster.makeTextLong(context, getResources().getText(R.string.no_conectivity_wrn).toString());
                else {
                    TMDbISELApplication.mListType = Constants.MOVIES_SEARCH;

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_clear_settings:
                Preferences.clearAll(this);
                return true;
            case R.id.action_search:
                onSearchRequested();
                return true;
            case R.id.action_clear_cache:
                TMDbISELApplication.sImgCache.clear();
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                Logger.d(TAG, "DEBUG: Option item selected not catched!");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Logger.d(TAG, "onNavigationItemSelected = " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.upcoming:
                TMDbISELApplication.mListType = Constants.MOVIES_UPCOMING;
                mToolbar.setTitle(R.string.title_upcoming);
                Toaster.makeTextShort(this, getResources().getText(R.string.title_upcoming).toString());
                break;
            case R.id.most_popular:
                TMDbISELApplication.mListType = Constants.MOVIES_MOST_POPULAR;
                mToolbar.setTitle(R.string.title_most_popular);
                Toaster.makeTextShort(this, getResources().getText(R.string.title_most_popular).toString());
                break;
            default:
                TMDbISELApplication.mListType = Constants.MOVIES_NOW_PLAYING;
                mToolbar.setTitle(R.string.title_now_playing);
                Toaster.makeTextShort(this, getResources().getText(R.string.title_now_playing).toString());
                break;
        }
        showProgressBar();
        mMovieListFragment.updateUI(String.valueOf(TMDbISELApplication.mListType), -1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        View detail;
        if ((detail = findViewById(R.id.detailFragmentContainer)) != null)
            detail.setVisibility(View.GONE);
        return true;
    }

    public void showProgressBar() {
        findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
    }
}

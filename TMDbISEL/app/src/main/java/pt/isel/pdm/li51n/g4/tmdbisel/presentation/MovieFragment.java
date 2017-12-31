package pt.isel.pdm.li51n.g4.tmdbisel.presentation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.isel.pdm.li51n.g4.tmdbisel.R;
import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.ProductionCompany;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Review;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;
import pt.isel.pdm.li51n.g4.tmdbisel.presentation.base.LoggingFragment;
import pt.isel.pdm.li51n.g4.tmdbisel.services.ImageService;

public class MovieFragment extends LoggingFragment {

    public static final String EXTRA_MOVIE_ID = "tmdbisel.MOVIE_ID";
    public static final String EXTRA_MOVIE = "tmdbisel.MOVIE";
    public static final String EXTRA_MOVIE_LIST = "tmdbisel.MOVIE_LIST";
    public static final String EXTRA_BUNDLE = "tmdbisel.EXTRA_BUNDLE";

    volatile Movie mMovie;
    CollapsingToolbarLayout mAppBarLayout;
    FloatingActionButton mFollowButton;
    ImageView mMoviePoster;
    ImageView mBackdropImage;
    TextView mSinopseField;
    CardView mSinopseContainer;
    android.support.v7.widget.CardView mReviewsContainer;
    LinearLayout mReviewsBag;
    TextView mSinopselabel;
    TextView mOriginalLanguageField;
    TextView mReleaseDateField;
    TextView mPopularityField;
    TextView mVoteAverageField;
    TextView mHomepageField;
    LinearLayout mHomepageContainer;
    LinearLayout mProductionCompaniesContainer;
    LinearLayout mProductionCompanies;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(ImageService.EXTRA_RESULT);
                if (resultCode == Activity.RESULT_OK) {
                    Logger.d(TAG, "Broadcast received!!!!!");
                    synchronized (this) {
                        String key = bundle.getString(ImageService.EXTRA_IMAGE_URL).substring(1);
                        ImageView aux = TMDbISELApplication.sImageViewsToUpdate
                                .get(key);
                        if (aux != null) {
                            Utils.updateImageView(key,
                                    aux,
                                    TMDbISELApplication.sImgCache.getBitmapFromCache(key)
                            );
                            TMDbISELApplication.sImageViewsToUpdate.remove(key);
                        }
                    }

                }
            }
        }
    };


    public static MovieFragment newInstance(String movieId) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MOVIE_ID, movieId);

        MovieFragment fragment = new MovieFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateUI() {
        Utils.setImageFromCacheToImageView(getActivity(), mMoviePoster, mMovie.getPosterPath(), Constants.POSTER_IMAGE);

        mOriginalLanguageField.setText(mMovie.getOriginalLanguage());
        mReleaseDateField.setText(mMovie.getReleaseDate());
        mPopularityField.setText(String.valueOf(mMovie.getPopularity()));
        mVoteAverageField.setText(String.valueOf(mMovie.getVoteAverage()));

        if (mMovie.getHomepage().length() > 0) {
            mHomepageField.setText(mMovie.getHomepage());
            mHomepageContainer.setVisibility(View.VISIBLE);
            mHomepageField.setClickable(true);
            mHomepageField.setMovementMethod(LinkMovementMethod.getInstance());

        }
        for (ProductionCompany pc : mMovie.getProductionCompanies()) {
            mProductionCompaniesContainer.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(getContext());
            textView.setText(pc.getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(R.style.Theme_TMDbISELTheme);
            }
            mProductionCompanies.addView(textView);
            mProductionCompaniesContainer.setVisibility(View.VISIBLE);
        }

        if (mAppBarLayout != null) {
            mAppBarLayout.setTitle(mMovie.getTitle());
        }

        if (mMovie.getOverview().length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSinopseField.setTextAppearance(R.style.Theme_TMDbISELTheme);
            }
            mSinopseField.setText(mMovie.getOverview());
            mSinopseContainer.setVisibility(View.VISIBLE);
        }
        if (mMovie.getReviews().size() > 0) {
            for (Review r : mMovie.getReviews()) {
                mReviewsBag.addView(getViewOfReview(r));
            }
            mReviewsContainer.setVisibility(View.VISIBLE);
        }
        loadBackdrop();
    }

    private View getViewOfReview(Review r) {
        CardView view = new CardView(getContext());
        view.setPadding(4, 4, 4, 4);

        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(18, 8, 4, 8);

        LinearLayout AuthorLinearLayout = new LinearLayout(getContext());
        AuthorLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView authorLabel = new TextView(getContext());
        authorLabel.setText(R.string.author_label);
        authorLabel.setTextColor(R.color.info_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            authorLabel.setTextAppearance(android.R.style.TextAppearance);
        }

        TextView author = new TextView(getContext());
        author.setText(r.getAuthor());
        AuthorLinearLayout.addView(authorLabel);
        AuthorLinearLayout.addView(author);

        LinearLayout ContentLinearLayout = new LinearLayout(getContext());
        ContentLinearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView contentLabel = new TextView(getContext());
        contentLabel.setText(R.string.content_label);
        contentLabel.setTextColor(R.color.info_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contentLabel.setTextAppearance(android.R.style.TextAppearance);
        }

        TextView content = new TextView(getContext());
        content.setText(r.getContent());
        content.setPadding(4, 0, 4, 0);
        ContentLinearLayout.addView(contentLabel);
        ContentLinearLayout.addView(content);

        container.addView(AuthorLinearLayout);
        container.addView(ContentLinearLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            authorLabel.setTextAppearance(R.style.Theme_TMDbISELTheme);
            contentLabel.setTextAppearance(R.style.Theme_TMDbISELTheme);
            content.setTextAppearance(R.style.Theme_TMDbISELTheme);
        }

        view.addView(container);
        return view;
    }

    private void loadBackdrop() {
        if (mBackdropImage == null) {
            Logger.d(TAG, "BackdropImage is NULL");
            return;
        }
        if (mMovie == null || mMovie.getBackdropPath() == null || getActivity() == null) return;
        Utils.setImageFromCacheToImageView(getActivity(),
                mBackdropImage,
                mMovie.getBackdropPath(),
                Constants.BACKDROP_IMAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        super.onCreateView(inflater, parent, savedInstanceState);

        View v;

        if (getActivity().findViewById(R.id.detailFragmentContainer) != null) {
            v = inflater.inflate(R.layout.fragment_movie_detail, parent, false);
            getActivity().findViewById(R.id.detailFragmentContainer).setVisibility(View.VISIBLE);
        } else {
            v = inflater.inflate(R.layout.activity_movie_detail, parent, false);
            Toolbar toolbar = (Toolbar) v.findViewById(R.id.detail_toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            mAppBarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.toolbar_layout);
            if (mAppBarLayout != null && mMovie != null) {
                mAppBarLayout.setTitle(mMovie.getTitle());
            }
            loadBackdrop();
        }

        mFollowButton = (FloatingActionButton) v.findViewById(R.id.follow_action_button);
        if (TMDbISELApplication.mListType == Constants.MOVIES_UPCOMING && mFollowButton != null) {
            mFollowButton.setVisibility(View.VISIBLE);
            mFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TMDbISELApplication.FAVOURITES.contains(mMovie.getId())) {
                        Snackbar.make(view, "Movie removed from favorites.", Snackbar.LENGTH_LONG).show();
                        TMDbISELApplication.FAVOURITES.remove(mMovie.getId());
                    } else {
                        Snackbar.make(view, "Movie add to favorites.\nI will notify you when this movie become now playing.", Snackbar.LENGTH_LONG).show();
                        TMDbISELApplication.FAVOURITES.add(mMovie.getId());
                    }
                }
            });
        }


        mMoviePoster = (ImageView) v.findViewById(R.id.moviePoster);
        mSinopseField = (TextView) v.findViewById(R.id.sinopse);
        mSinopselabel = (TextView) v.findViewById(R.id.sinopse_label);
        mSinopselabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSinopseField.setVisibility(mSinopseField.isShown() ? View.GONE : View.VISIBLE);
            }
        });

        mReviewsContainer = (android.support.v7.widget.CardView) v.findViewById(R.id.reviews_container);
        mReviewsBag = (LinearLayout) v.findViewById(R.id.reviews_bag);
        v.findViewById(R.id.reviews_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReviewsBag.setVisibility(mReviewsBag.isShown() ? View.GONE : View.VISIBLE);
            }
        });

        mSinopseContainer = (CardView) v.findViewById(R.id.sinopse_container);
        mOriginalLanguageField = (TextView) v.findViewById(R.id.detail_original_language);
        mReleaseDateField = (TextView) v.findViewById(R.id.detail_release_date);
        mPopularityField = (TextView) v.findViewById(R.id.detail_popularity);
        mVoteAverageField = (TextView) v.findViewById(R.id.detail_vote_average);
        mHomepageField = (TextView) v.findViewById(R.id.detail_homepage);
        mBackdropImage = (ImageView) v.findViewById(R.id.backdrop);
        mHomepageContainer = (LinearLayout) v.findViewById(R.id.detail_homepage_linear_layout);
        mProductionCompaniesContainer = (LinearLayout) v.findViewById(R.id.container_production_companies);
        mProductionCompanies = (LinearLayout) v.findViewById(R.id.detail_production_companies_container);

        String movieId = getArguments().getString(EXTRA_MOVIE_ID);
        new AsyncTask<String, Void, Movie>() {

            @Override
            protected Movie doInBackground(String... params) {
                return TMDbISELApplication.MOVIE_REPOSITORY_CP.getMovie(Integer.parseInt(params[0]));
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             */
            @Override
            protected void onPostExecute(Movie movie) {
                if (movie == null) return;

                mMovie = movie;
                updateUI();

            }
        }.execute(movieId);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(ImageService.NOTIFICATION));
        Logger.d(TAG, "Broadcast registered!!!");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_MOVIE, mMovie);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        Logger.d(TAG, "Broadcast unregistered!!!");
        super.onPause();
    }
}

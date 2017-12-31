package pt.isel.pdm.li51n.g4.tmdbisel.presentation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.isel.pdm.li51n.g4.tmdbisel.R;
import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Constants;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;
import pt.isel.pdm.li51n.g4.tmdbisel.presentation.base.LoggingFragment;
import pt.isel.pdm.li51n.g4.tmdbisel.services.ImageService;

public class MovieListFragment extends LoggingFragment {

    public SimpleMovieItemRecyclerViewAdapter mAdapter;
    private List<Movie> mMovies;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * This BroadcastReceiver handles ImageService Notification
     */
    private BroadcastReceiver mImageServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.getInt(ImageService.EXTRA_RESULT) == Activity.RESULT_OK) {
                Logger.d(TAG, "Broadcast received!!!!!");
                synchronized (this) {
                    String key = bundle.getString(ImageService.EXTRA_IMAGE_URL).substring(1);
                    ImageView aux = TMDbISELApplication.sImageViewsToUpdate.get(key);
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
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mImageServiceBroadcastReceiver, new IntentFilter(ImageService.NOTIFICATION));
//        getActivity().registerReceiver(mMovieRefreshServiceBroadcastReceiver, new IntentFilter(MovieCollectionRefresherService.NOTIFICATION));
        Logger.d(TAG, "Broadcast registered!!!");
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mImageServiceBroadcastReceiver);
//        getActivity().unregisterReceiver(mMovieRefreshServiceBroadcastReceiver);
        Logger.d(TAG, "Broadcast unregistered!!!");
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(Utils.getListTitle(getContext(), TMDbISELApplication.mListType));
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment oldDetail = fm.findFragmentByTag(Constants.TAG_MOVIE_DETAIL_FRAGMENT);
        if (oldDetail != null) {
            ft.remove(oldDetail).commit();
            Logger.e(TAG, new Exception("Removed OLD DETAIL FRAGMENT"));
        }
        setRetainInstance(true);
        updateUI(String.valueOf(TMDbISELApplication.mListType), -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movie_list, container, false);
        v.setTag(TAG);
        final FragmentActivity c = getActivity();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.movie_list);
        mLayoutManager = new LinearLayoutManager(c);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SimpleMovieItemRecyclerViewAdapter(c);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return v;
    }


    public synchronized void updateUI(String listType, final int selectPos) {
        // Update Data set off of the UIThread:
        new AsyncTask<String, Void, List<Movie>>() {
            @Override
            protected List<Movie> doInBackground(String... params) {
                return TMDbISELApplication.MOVIE_REPOSITORY_CP.getMoviesByCriteria(params[0]);
            }
            @Override
            protected void onPostExecute(List<Movie> movies) {
                mMovies = movies;
                mAdapter.setSelectedPos(selectPos);
                mAdapter.notifyDataSetChanged();
                hideProgressBar();
            }
        }.execute(listType);
    }

    public void hideProgressBar() {
        getActivity().findViewById(R.id.progress_bar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
    }

    public class SimpleMovieItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleMovieItemRecyclerViewAdapter.ViewHolder> {

        public final String TAG = this.getClass().getSimpleName();
        private MovieListActivity mMovieListActivity;

        private int selectedPos = -1;
        public SimpleMovieItemRecyclerViewAdapter(FragmentActivity mla) {
            mMovieListActivity = (MovieListActivity) mla;
        }

        public int getSelectedPos() {
            return selectedPos;
        }

        public void setSelectedPos(int pos) {
            selectedPos = pos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);

            final ViewHolder vh = new ViewHolder(view);
            vh.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(selectedPos);
                    selectedPos = vh.position;
                    notifyItemChanged(selectedPos);
                    mMovieListActivity.onMovieSelected(vh.mItem);
                }
            });
            return vh;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mMovies.get(position);

            holder.position = position;
            if (position == selectedPos) {
                holder.mView.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.mView.setBackgroundColor(Color.WHITE);
            }

            Utils.setImageFromCacheToImageView(getActivity(), holder.mMovieAvatar, holder.mItem.getPosterPath(), Constants.POSTER_IMAGE);

            holder.mMovieTitle.setText(holder.mItem.getTitle());
        }

        @Override
        public int getItemCount() {
            // mMovies might not have been initialized:
            return mMovies == null ? 0 : mMovies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final CircleImageView mMovieAvatar;
            public final TextView mMovieTitle;
            public Movie mItem;
            public int position;
            public String filename;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mMovieAvatar = (CircleImageView) view.findViewById(R.id.avatar);
                mMovieTitle = (TextView) view.findViewById(R.id.movie_list_item_title);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mMovieTitle.getText() + "'";
            }
        }
    }
}

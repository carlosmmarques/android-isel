package pt.isel.pdm.li51n.g4.tmdbisel.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Utils;

public class DownloadThread extends BaseThread {

    private static final int TYPE_BITMAP    = 0;
    private static final int TYPE_JSON      = 1;
    private static final String TAG = DownloadThread.class.getSimpleName();
    private Callback mCallback;

    private DownloadThread(Handler responseHandler, Callback callback) {
        super(TAG, responseHandler);
        mCallback = callback;
    }

    @Override
    void handleMessageInternal(Message message) {
        DownloadBag bag = (DownloadBag) message.obj;
        handleRequest(bag, message.what);
    }

    public void queueImageDownload(String url, ImageView view) {
        mRequestMap.put(url, view);
//        queueTask(url, TYPE_BITMAP);
    }

    public void queueMovieListDownload(int listType, @Nullable String query, View view){
        mRequestMap.put(Integer.toString(listType), view);
        queueTask(listType, query, TYPE_JSON);
//        mWorkerHandler.obtainMessage(listType)
//                .sendToTarget();
    }

    private void queueTask(int listType, String query, int type){
        Logger.i(TAG, "Added " + listType + " to the queue.");
        mWorkerHandler.obtainMessage(type, new DownloadBag(listType,query)).sendToTarget();
    }

    private void handleRequest(final DownloadBag bag, final int type) {
        switch (type){
            case TYPE_JSON:
                try {
                    List<Movie> movieList = new ArrayList<>();
                    Logger.d(TAG, "ListType: " + bag.listType + " \n let's get the movies......");
                    movieList.addAll(TMDbISELApplication.getMovieInfoProvider().getMovieListInfo(Integer.toString(bag.listType),
                            Utils.getLanguage(((Context) mCallback)), bag.query, 1));
                    if(!movieList.isEmpty()) TMDbISELApplication.MOVIE_REPOSITORY_CP.addAndUpdateAllWithCriteria(
                            movieList,
                            String.valueOf(TMDbISELApplication.mListType)
                    );
                } catch (Exception e) {
                    Logger.e(TAG, e);
                }
                mRequestMap.remove(Integer.toString(bag.listType));
                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onMovieListDownloaded();
                    }
                });


        }
    }

    public interface Callback {
        void onImageDownloaded(String url, ImageView view, Bitmap bitmap);

        void onMovieListDownloaded();
    }

    public class DownloadBag{
        int listType;
        String query;

        public DownloadBag(int listType, String query) {
            this.listType = listType;
            this.query = query;
        }

        public int getListType() {
            return listType;
        }

        public String getQuery() {
            return query;
        }
    }
}

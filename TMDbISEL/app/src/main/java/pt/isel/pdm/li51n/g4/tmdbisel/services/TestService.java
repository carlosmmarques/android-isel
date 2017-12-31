package pt.isel.pdm.li51n.g4.tmdbisel.services;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDbDB.TMDbDBContract;
import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;

public class TestService extends TMDbService{

    public static final String TAG = TestService.class.getSimpleName();
    private static Context mContext;
    private static int mListType;

    @Override
    protected void doWork(Intent intent) {
        testGetMoviesByCriteria();
    }

    private void testGetMoviesByCriteria(){
        ContentResolver resolver = mContext.getContentResolver();
        List<Movie> result =new ArrayList<>();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Cursor cursor = resolver.query(
                TMDbDBContract.MovieContract.CONTENT_URI,
                null,
                null,
                new String[]{String.valueOf(mListType)},
                TMDbDBContract.MovieContract.SORT_ORDER_DEFAULT
        );

        if(cursor != null){
            Logger.d(TAG, "cursor isn't null, lets get Movies ");
            while (cursor.moveToNext()){
                result.add(new Movie(cursor));
            }
            cursor.close();
        }
        Logger.d(TAG, "Já está!!!!!");
    }

    public static Intent NewInstance(Context ctx, int listType) {
        mContext = ctx;
        mListType = listType;
        return new Intent(ctx, TestService.class);
    }
}

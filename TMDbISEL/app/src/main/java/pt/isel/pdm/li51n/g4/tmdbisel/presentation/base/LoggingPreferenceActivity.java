package pt.isel.pdm.li51n.g4.tmdbisel.presentation.base;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.ListView;

import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;

public class LoggingPreferenceActivity extends PreferenceActivity {

    protected final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.d(TAG, String.format("onCreate and Bundle is%s null", savedInstanceState != null ? " not" : ""));
    }

    @Override
    protected void onStop() {
        Logger.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Logger.d(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult");
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        Logger.d(TAG, "onContentChanged");
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Logger.d(TAG, "onListItemClick");
    }

}

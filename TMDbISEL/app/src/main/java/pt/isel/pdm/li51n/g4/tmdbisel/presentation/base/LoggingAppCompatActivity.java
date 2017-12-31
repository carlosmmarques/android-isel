package pt.isel.pdm.li51n.g4.tmdbisel.presentation.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;

public abstract class LoggingAppCompatActivity extends AppCompatActivity{

    protected final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.d(TAG, String.format("onCreate and Bundle is%s null", savedInstanceState != null ? " not": ""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d(TAG, "onRestart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onPause() {
        Logger.d(TAG, "onPause");
        super.onPause();
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
}

package pt.isel.pdm.li51n.g4.tmdbisel.presentation.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import pt.isel.pdm.li51n.g4.tmdbisel.helpers.Logger;

public class LoggingFragmentActivity extends FragmentActivity {

    protected final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate");
    }
}

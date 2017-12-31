package pt.isel.pdm.li51n.g4.tmdbisel.presentation.settings;

import android.os.Bundle;

import pt.isel.pdm.li51n.g4.tmdbisel.presentation.base.LoggingAppCompatActivity;

public class SettingsActivity extends LoggingAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Display the fragment as the main content
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }
}

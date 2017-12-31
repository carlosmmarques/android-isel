package pt.isel.pdm.li51n.g4.tmdbisel.presentation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import pt.isel.pdm.li51n.g4.tmdbisel.R;
import pt.isel.pdm.li51n.g4.tmdbisel.TMDbISELApplication;
import pt.isel.pdm.li51n.g4.tmdbisel.presentation.base.LoggingAppCompatActivity;

public abstract class SingleFragmentActivity extends LoggingAppCompatActivity {



    protected abstract Fragment createFragment();

    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if(fragment == null){
            fragment = createFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
        if (findViewById(R.id.detailFragmentContainer) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            TMDbISELApplication.mTwoPane = true;
        }
    }
}

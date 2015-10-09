package com.conichi.tictactoe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.conichi.tictactoe.R;
import com.conichi.tictactoe.fragment.HomeFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends ActionBarActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.ad_view)
    AdView mAdView;

    //used to monitor the backStackEntryCount
    private int backStackEntryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        manageAppLifecycle();

        ButterKnife.bind(this);

        setToolbar();
        setOnBackStackListener();

        setAds();

        //sets the HomeFragment
        setCurrentFragment(new HomeFragment());
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    //if user presses back, gets returned to home
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //workaround that prevents the app restarting itself if it was active and pushed in background
    //by pressing Home button, and then started again through launcher (tapping on app icon)
    private void manageAppLifecycle() {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
    }

    //sets the setSupportActionBar to Toolbar
    private void setToolbar() {
        setSupportActionBar(toolbar);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    //if the user presses back, monitor the backStackEntryCount and act accordingly
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            finish();
        }
    }

    //sets the OnBackStackListener
    private void setOnBackStackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() < backStackEntryCount) {
                    getSupportFragmentManager().findFragmentById(R.id.fragmentContainerHome).onResume();
                }
                backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                setActionBarArrowDependingOnFragmentsBackStack();
            }
        });
    }

    //display or hide the Home arrow in the Toolbar depending on the backStackEntryCount
    private void setActionBarArrowDependingOnFragmentsBackStack() {
        if (backStackEntryCount == 1) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //set ads
    private void setAds() {
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("E795DD1781EB4872B74A11117D72868F")
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    //sets a current fragment for the activity with a fade animation
    public void setCurrentFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.add(R.id.fragmentContainerHome, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

}

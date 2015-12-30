package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.bignerdranch.android.criminalintent.model.Crime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by janv on 29-Dec-15.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class CrimeListActivityRoboEmptyListTest {

    private CrimeListActivity crimeListActivity;
    private RecyclerView recyclerView;
    private FrameLayout twoPaneDetails;

    @Before
    public void setup() {
        // mock the database
        CrimeIntentApplication.crimeLab = new TestCrimeLab();

        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        crimeListActivity = Robolectric.setupActivity(CrimeListActivity.class);
        recyclerView = (RecyclerView) crimeListActivity.findViewById(R.id.crime_recycler_view);
        twoPaneDetails = (FrameLayout) crimeListActivity.findViewById(R.id.detail_fragment_container);
    }

    @Test
    public void recyclerViewEmpty() {
        assertNotNull("RecyclerView could not be found", recyclerView);
        assertEquals("RecyclerView is not empty", 0, recyclerView.getAdapter().getItemCount());
        assertNull("details section of two panes not expected, but found", twoPaneDetails);
    }

    @Test
    public void clickMenuItemNewCrime() {
        // Get shadow
        ShadowActivity shadowActivity = Shadows.shadowOf(crimeListActivity);

        // Click menu item for new crime
        shadowActivity.clickMenuItem(R.id.menu_item_new_crime);

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);

        //assert
        assertEquals("Crimes list is empty", 1, CrimeIntentApplication.crimeLab.getCrimes().size());
        Crime crime = CrimeIntentApplication.crimeLab.getCrimes().get(0);
        assertEquals("RecyclerView is empty", 1, recyclerView.getAdapter().getItemCount());
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(CrimePagerActivity.class.getName()));
        assertThat(shadowIntent.getExtras().getLong(CrimePagerActivity.EXTRA_CRIME_ID), equalTo(crime.getId()));
    }

    @Test
    public void changeOrientation() {
        crimeListActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}

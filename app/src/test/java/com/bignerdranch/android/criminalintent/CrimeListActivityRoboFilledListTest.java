package com.bignerdranch.android.criminalintent;

import android.content.Intent;
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
public class CrimeListActivityRoboFilledListTest {

    private CrimeListActivity crimeListActivity;
    private RecyclerView recyclerView;
    private FrameLayout twoPaneDetails;
    private Crime crime1, crime2;

    @Before
    public void setup() {
        // mock the database with two crimes
        CrimeIntentApplication.crimeLab = new TestCrimeLab();
        crime1 = CrimeListFragment.createEmtyCrime();
        crime2 = CrimeListFragment.createEmtyCrime();
        CrimeIntentApplication.crimeLab.addCrime(crime1);
        CrimeIntentApplication.crimeLab.addCrime(crime2);

        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        crimeListActivity = Robolectric.setupActivity(CrimeListActivity.class);
        recyclerView = (RecyclerView) crimeListActivity.findViewById(R.id.crime_recycler_view);
        twoPaneDetails = (FrameLayout) crimeListActivity.findViewById(R.id.detail_fragment_container);
        // to force making the viewholders, see http://stackoverflow.com/questions/27052866/android-robolectric-click-recyclerview-item
        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 10000);
    }

    @Test
    public void recyclerViewNotEmpty() {
        assertEquals("Crimes list does not have two items", 2, CrimeIntentApplication.crimeLab.getCrimes().size());
        assertNotNull("RecyclerView could not be found", recyclerView);
        assertEquals("RecyclerView does not have two items", 2, recyclerView.getAdapter().getItemCount());
        assertNull("details section of two panes not expected, but found", twoPaneDetails);
    }

    @Test
    public void clickFirstElementInListView() {
        // Get shadows
        ShadowActivity shadowActivity = Shadows.shadowOf(crimeListActivity);
        CrimeListFragment.CrimeHolder viewHolderToClick = (CrimeListFragment.CrimeHolder) recyclerView.findViewHolderForAdapterPosition(0);

        // perform click on viewholder item
        Shadows.shadowOf(viewHolderToClick.itemView).getOnClickListener().onClick(viewHolderToClick.itemView);

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);

        //assert
        assertEquals("Crimes list does not have two items", 2, CrimeIntentApplication.crimeLab.getCrimes().size());
        assertNotNull("RecyclerView could not be found", recyclerView);
        assertEquals("RecyclerView does not have two items", 2, recyclerView.getAdapter().getItemCount());
        Crime crime = viewHolderToClick.mCrime;
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(CrimePagerActivity.class.getName()));
        assertThat(shadowIntent.getExtras().getLong(CrimePagerActivity.EXTRA_CRIME_ID), equalTo(crime.getId()));
    }

    @Test
    public void clickSecondElementInListView() {
        // Get shadows
        ShadowActivity shadowActivity = Shadows.shadowOf(crimeListActivity);
        CrimeListFragment.CrimeHolder viewHolderToClick = (CrimeListFragment.CrimeHolder) recyclerView.findViewHolderForAdapterPosition(1);

        // perform click on viewholder ite;
        Shadows.shadowOf(viewHolderToClick.itemView).getOnClickListener().onClick(viewHolderToClick.itemView);

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);

        //assert
        assertEquals("Crimes list does not have two items", 2, CrimeIntentApplication.crimeLab.getCrimes().size());
        assertNotNull("RecyclerView could not be found", recyclerView);
        assertEquals("RecyclerView does not have two items", 2, recyclerView.getAdapter().getItemCount());
        Crime crime = viewHolderToClick.mCrime;
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(CrimePagerActivity.class.getName()));
        assertThat(shadowIntent.getExtras().getLong(CrimePagerActivity.EXTRA_CRIME_ID), equalTo(crime.getId()));
    }
}

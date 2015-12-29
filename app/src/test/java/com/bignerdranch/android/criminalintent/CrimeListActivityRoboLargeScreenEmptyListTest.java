package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test case for a large screen: should be in two pane mode.
 *
 * Created by janv on 29-Dec-15.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, qualifiers="sw600dp")
@RunWith(RobolectricGradleTestRunner.class)
public class CrimeListActivityRoboLargeScreenEmptyListTest {

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
        assertNotNull("details section of two panes expected, but not found", twoPaneDetails);
    }

    @Test
    public void clickMenuItemNewCrime() {
        // Get shadow
        ShadowActivity shadowActivity = Shadows.shadowOf(crimeListActivity);

        // Click menu item for new crime
        shadowActivity.clickMenuItem(R.id.menu_item_new_crime);

        // no intent started
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertNull(startedIntent);

        //model change
        assertEquals("Crimes list is empty", 1, CrimeIntentApplication.crimeLab.getCrimes().size());
        Crime crime = CrimeIntentApplication.crimeLab.getCrimes().get(0);
        assertEquals("RecyclerView is empty", 1, recyclerView.getAdapter().getItemCount());

        // view change
        assertNotNull("crime title expected after item added/selected", crimeListActivity.findViewById(R.id.crime_title));
        assertNotNull("crime date expected after item added/selected", crimeListActivity.findViewById(R.id.crime_date));
        assertNotNull("crime solved expected after item added/selected", crimeListActivity.findViewById(R.id.crime_solved));
        assertNotNull("crime suspect expected after item added/selected", crimeListActivity.findViewById(R.id.crime_suspect));
        assertNotNull("crime photo expected after item added/selected", crimeListActivity.findViewById(R.id.crime_photo));
    }
}

package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bignerdranch.android.criminalintent.model.Crime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Test case for a large screen: should be in two pane mode.
 *
 * Created by janv on 29-Dec-15.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, qualifiers="sw600dp")
@RunWith(RobolectricGradleTestRunner.class)
public class CrimeListActivityRoboLargeScreenFilledListTest {

    private CrimeListActivity crimeListActivity;
    private RecyclerView recyclerView;
    private FrameLayout twoPaneDetails;
    private Crime crime1, crime2;

    @Before
    public void setup() {
        // mock the database with two crimes
        CrimeIntentApplication.crimeLab = new TestCrimeLab();
        crime1 = CrimeListFragment.createEmtyCrime();
        crime1.setTitle("crime1 test");
        crime1.setDate(new Date(156463));
        crime1.setSolved(true);
        crime2 = CrimeListFragment.createEmtyCrime();
        crime2.setTitle("crime2 test");
        crime2.setDate(new Date(4604));
        crime2.setSolved(false);
        crime2.setSuspect("suspectTest");
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
        assertEquals("Crimes list is not three", 3, CrimeIntentApplication.crimeLab.getCrimes().size());
        Crime crime = CrimeIntentApplication.crimeLab.getCrimes().get(0);
        assertEquals("RecyclerView is not three", 3, recyclerView.getAdapter().getItemCount());

        // view change
        assertDetailsFields();
    }

    @Test
    public void clickFirstElementInListView() {
        // Get shadows
        ShadowActivity shadowActivity = Shadows.shadowOf(crimeListActivity);
        CrimeListFragment.CrimeHolder viewHolderToClick = (CrimeListFragment.CrimeHolder) recyclerView.findViewHolderForAdapterPosition(0);

        // perform click on viewholder item
        Shadows.shadowOf(viewHolderToClick.itemView).getOnClickListener().onClick(viewHolderToClick.itemView);

        // no intent started
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertNull(startedIntent);

        //model same
        assertEquals("Crimes list does not have two items", 2, CrimeIntentApplication.crimeLab.getCrimes().size());
        assertNotNull("RecyclerView could not be found", recyclerView);
        assertEquals("RecyclerView does not have two items", 2, recyclerView.getAdapter().getItemCount());

        // view change
        assertDetailsFields();
        assertDetailsValues(crime1);
    }

    @Test
    public void clickSecondElementInListView() {
        // Get shadows
        ShadowActivity shadowActivity = Shadows.shadowOf(crimeListActivity);
        CrimeListFragment.CrimeHolder viewHolderToClick = (CrimeListFragment.CrimeHolder) recyclerView.findViewHolderForAdapterPosition(1);

        // perform click on viewholder item
        Shadows.shadowOf(viewHolderToClick.itemView).getOnClickListener().onClick(viewHolderToClick.itemView);

        // no intent started
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertNull(startedIntent);

        //model same
        assertEquals("Crimes list does not have two items", 2, CrimeIntentApplication.crimeLab.getCrimes().size());
        assertNotNull("RecyclerView could not be found", recyclerView);
        assertEquals("RecyclerView does not have two items", 2, recyclerView.getAdapter().getItemCount());

        // view change
        assertDetailsFields();
        assertDetailsValues(crime2);
    }

    private void assertDetailsFields() {
        assertNotNull("crime title expected after item added/selected", crimeListActivity.findViewById(R.id.crime_title));
        assertNotNull("crime date expected after item added/selected", crimeListActivity.findViewById(R.id.crime_date));
        assertNotNull("crime solved expected after item added/selected", crimeListActivity.findViewById(R.id.crime_solved));
        assertNotNull("crime suspect expected after item added/selected", crimeListActivity.findViewById(R.id.crime_suspect));
        assertNotNull("crime photo expected after item added/selected", crimeListActivity.findViewById(R.id.crime_photo));
    }

    private void assertDetailsValues(Crime crime) {
        assertEquals("title not correct", crime.getTitle(), ((TextView) crimeListActivity.findViewById(R.id.crime_title)).getText().toString());
        assertEquals("date not correct", crime.getDate().toString(), ((Button) crimeListActivity.findViewById(R.id.crime_date)).getText().toString());
        assertEquals("solved not correct", crime.getSolved().booleanValue(), ((CheckBox) crimeListActivity.findViewById(R.id.crime_solved)).isChecked());
        String suspectExpected = crime.getSuspect();
        if (suspectExpected == null || suspectExpected.isEmpty()) {
            suspectExpected = crimeListActivity.getString(R.string.crime_suspect_text);
        }
        assertEquals("suspect not correct", suspectExpected, ((Button) crimeListActivity.findViewById(R.id.crime_suspect)).getText().toString());
    }
}

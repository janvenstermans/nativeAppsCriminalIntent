package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bignerdranch.android.criminalintent.model.Crime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.util.ActivityController;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by janv on 29-Dec-15.
 */
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class CrimePagerActivityRoboTest {

    private static String APP_PACKAGE_NAME = "com.example";

    // ActivityController is a Robolectric class that drives the Activity lifecycle
    private ActivityController<CrimePagerActivity> controller;
    private CrimePagerActivity crimePagerActivity;
    private Crime crime;

    protected EditText mTitleField;
    protected Button mDateButton;
    protected CheckBox mSolvedCheckbox;
    protected Button mSuspectButton;
    protected ImageView mPhotoView;

    protected LinearLayout landscapeLayout;

    @Before
    public void setup() {
        // mock the database
        CrimeIntentApplication.crimeLab = new TestCrimeLab();
        crime = CrimeListFragment.createEmtyCrime();
        crime.setTitle("Someone left the door open.");
        CrimeIntentApplication.crimeLab.addCrime(crime);
        controller = Robolectric.buildActivity(CrimePagerActivity.class);
    }

    @Test
    public void viewDisplaysCrimeInfo() {
        createActivity();
        bindViewElements();

        assertNotNull("mTitleField could not be found", mTitleField);
        assertEquals("title not correct", crime.getTitle(), mTitleField.getText().toString());
        assertNotNull("mDateButton could not be found", mDateButton);
        assertEquals("date not correct", crime.getDate().toString(), mDateButton.getText().toString());
        assertNotNull("mSolvedCheckbox could not be found", mSolvedCheckbox);
        assertEquals("solved checkbox not correct", crime.getSolved().booleanValue(), mSolvedCheckbox.isChecked());
        assertNotNull("mSuspectButton could not be found", mSuspectButton);
        assertEquals("suspect text is not default value", crimePagerActivity.getString(R.string.crime_suspect_text), mSuspectButton.getText().toString());
        assertNotNull("mPhotoView could not be found", mPhotoView);
    }

    @Test
    public void viewDisplaysChangedCrimeInfo() {
        String title = "titleTest";
        String suspect = "suspectTest";
        Date date = new Date(121683151);
        boolean solved = true;
        crime.setTitle(title);
        crime.setDate(date);
        crime.setSolved(solved);
        crime.setSuspect(suspect);

        createActivity();
        bindViewElements();

        assertNotNull("mTitleField could not be found", mTitleField);
        assertEquals("title not correct", title, mTitleField.getText().toString());
        assertNotNull("mDateButton could not be found", mDateButton);
        assertEquals("date not correct", date.toString(), mDateButton.getText().toString());
        assertNotNull("mSolvedCheckbox could not be found", mSolvedCheckbox);
        assertEquals("solved checkbox not correct", solved, mSolvedCheckbox.isChecked());
        assertNotNull("mSuspectButton could not be found", mSuspectButton);
        assertEquals("suspect not correct", suspect, mSuspectButton.getText().toString());
        assertNotNull("mPhotoView could not be found", mPhotoView);
        assertNull("expected portrait layout, but got landscape", landscapeLayout);
    }

    @Test
    public void onClickSuspectButtonWithoutResponseApp() {
        createActivity();
        bindViewElements();

        // Get shadows and expected values
        ShadowActivity shadowActivity = Shadows.shadowOf(crimePagerActivity);
        Intent expectedIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        disallowIntentResolve(expectedIntent);

        mSuspectButton.performClick();

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        //assert
        assertNull(startedIntent);
    }

    @Test
    public void onClickSuspectButtonWithResponseApp() {
        createActivity();
        bindViewElements();

        // Get shadows and expected values
        ShadowActivity shadowActivity = Shadows.shadowOf(crimePagerActivity);
        Intent expectedIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        allowIntentResolve(expectedIntent);

        mSuspectButton.performClick();

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);

        //assert
        assertThat(shadowIntent.getAction(), equalTo(expectedIntent.getAction()));
        assertThat(shadowIntent.getData(), equalTo(expectedIntent.getData()));


        // apply result, see http://robolectric.blogspot.be/2010/12/testing-startactivityforresult-and.html
//        shadowActivity.receiveResult(expectedIntent, Activity.RESULT_OK, );
        //TODO: finish up
    }

    @Test
    public void onClickSendReportButtonWithoutResponseApp() {
        createActivity();
        bindViewElements();

        // Get shadows and expected values
        ShadowActivity shadowActivity = Shadows.shadowOf(crimePagerActivity);
        Intent expectedIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        expectedIntent.setType("text/plain");
        disallowIntentResolve(expectedIntent);

        crimePagerActivity.findViewById(R.id.crime_report).performClick();

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        //assert
        assertNull(startedIntent);
    }

    @Test
    public void onClickSendReportButtonWithResponseApp() {
        createActivity();
        bindViewElements();

        // Get shadows and expected values
        ShadowActivity shadowActivity = Shadows.shadowOf(crimePagerActivity);
        Intent expectedIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        expectedIntent.setType("text/plain");
        allowIntentResolve(expectedIntent);

        crimePagerActivity.findViewById(R.id.crime_report).performClick();

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);

        //assert
        assertThat(shadowIntent.getAction(), equalTo(Intent.ACTION_CHOOSER));
        Object extraIntent = shadowIntent.getExtras().get(Intent.EXTRA_INTENT);
        assertNotNull(extraIntent);

        // get target intent
        Intent targetIntent = (Intent) extraIntent;
        ShadowIntent shadowTargetIntent = Shadows.shadowOf(targetIntent);

        //assert
        assertThat(shadowTargetIntent.getAction(), equalTo(expectedIntent.getAction()));
        assertThat(shadowTargetIntent.getData(), equalTo(expectedIntent.getData()));
        assertThat(shadowTargetIntent.getType(), equalTo(expectedIntent.getType()));
    }

    @Test
    public void onClickMakePhotButtonWithResponseApp() {
        createActivity();
        bindViewElements();
        CrimeIntentApplication.crimeLab.getPhotoFile(crime);

        // Get shadows and expected values
        ShadowActivity shadowActivity = Shadows.shadowOf(crimePagerActivity);
        Intent expectedIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri expectedUri = Uri.fromFile(CrimeIntentApplication.crimeLab.getPhotoFile(crime));
        allowIntentResolve(expectedIntent);

        crimePagerActivity.findViewById(R.id.crime_camera).performClick();

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);

        //assert
        assertThat(shadowIntent.getAction(), equalTo(expectedIntent.getAction()));
        assertTrue(shadowIntent.getExtras().containsKey(MediaStore.EXTRA_OUTPUT));
        assertThat((Uri) shadowIntent.getExtras().get(MediaStore.EXTRA_OUTPUT), equalTo(expectedUri));
    }

    @Test
    public void changeOrientation() {
        // TODO
        // see http://stackoverflow.com/questions/6063505/how-do-you-force-a-configuration-change-in-an-android-robolectric-test
//        createActivity();
//        toggleOrientation();
//        crimePagerActivity = recreateActivity();
//        bindViewElements();
//
//        assertNotNull("expected landscape crime fragment, got portrait", landscapeLayout);
    }

    // helper methods

    private void createActivity() {
        crimePagerActivity = createWithIntent(crime);
    }

    private void bindViewElements() {
            mTitleField = (EditText) crimePagerActivity.findViewById(R.id.crime_title);
        mDateButton = (Button) crimePagerActivity.findViewById(R.id.crime_date);
        mSolvedCheckbox = (CheckBox) crimePagerActivity.findViewById(R.id.crime_solved);
        mSuspectButton = (Button) crimePagerActivity.findViewById(R.id.crime_suspect);
        mPhotoView = (ImageView) crimePagerActivity.findViewById(R.id.crime_photo);
        landscapeLayout = (LinearLayout) crimePagerActivity.findViewById(R.id.crime_detail_landscape_layout);
    }

    // Activity creation that allows intent extras to be passed in
    private CrimePagerActivity createWithIntent(Crime crime) {
        Intent intent = CrimePagerActivity.newIntent(RuntimeEnvironment.application, crime.getId());
        return controller
                .withIntent(intent)
                .create()
                .start()
                .resume()
                .visible()
                .get();
    }

    private void allowIntentResolve(Intent intent) {
        ResolveInfo info = new ResolveInfo();
        info.isDefault = true;
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.packageName = APP_PACKAGE_NAME;
        info.activityInfo = new ActivityInfo();
        info.activityInfo.applicationInfo = applicationInfo;
        info.activityInfo.name = "Example";

        RobolectricPackageManager rpm = RuntimeEnvironment.getRobolectricPackageManager();
        rpm.addResolveInfoForIntent(intent, info);
    }

    private void disallowIntentResolve(Intent intent) {
        RobolectricPackageManager rpm = RuntimeEnvironment.getRobolectricPackageManager();
        rpm.removeResolveInfosForIntent(intent, APP_PACKAGE_NAME);
    }

    private void toggleOrientation() {
        int currentOrientation = crimePagerActivity.getResources().getConfiguration().orientation;
        boolean isPortraitOrUndefined = currentOrientation == Configuration.ORIENTATION_PORTRAIT || currentOrientation == Configuration.ORIENTATION_UNDEFINED;
        int toOrientation = isPortraitOrUndefined ? Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT;
        RuntimeEnvironment.application.getResources().getConfiguration().orientation = toOrientation;
    }

    private CrimePagerActivity recreateActivity() {
        Bundle bundle = new Bundle();
        ShadowActivity shadowIntent = Shadows.shadowOf(crimePagerActivity);
        controller.saveInstanceState(bundle).pause().stop().destroy();
        return controller
                .create(bundle)
                .start()
                .restoreInstanceState(bundle)
                .resume()
                .visible()
                .get();
    }
}

package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bignerdranch.android.criminalintent.model.Crime;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String LOG_TAG = "CrimeFragment";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;

    private Crime mCrime;
    private File mPhotoFile;

    @Bind(R.id.crime_title)
    protected EditText mTitleField;
    @Bind(R.id.crime_date)
    protected Button mDateButton;
    @Bind(R.id.crime_solved)
    protected CheckBox mSolvedCheckbox;
    @Bind(R.id.crime_suspect)
    protected Button mSuspectButton;
    @Bind(R.id.crime_photo)
    protected ImageView mPhotoView;

    private Callbacks mCallbacks;

    @BindString(R.string.crime_suspect_text) String suspectDefaultString;
    // int (for pixel size) or float (for exact value) field
    @BindDimen(R.dimen.crime_photo_width) int crimePhotoWidthPx;
    @BindDimen(R.dimen.crime_photo_heigth) int crimePhotoHeightPx;

    private ICrimeLab crimeLab;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    public static CrimeFragment newInstance(long crimeId) {
        Bundle args = new Bundle();
        args.putLong(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long crimeId = getArguments().getLong(ARG_CRIME_ID);
        crimeLab = getICrimeLab();
        mCrime = crimeLab.getCrime(crimeId);
        mPhotoFile = crimeLab.getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        crimeLab.updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        ButterKnife.bind(this, v);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getActivity() == null) {
                    return;
                }
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox.setChecked(mCrime.getSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        updatePhotoView();
        updateSuspect();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, String.format("CrimeFragment's onActivityResult called with request code %d " +
                "and result code %d", requestCode, resultCode));
        if (resultCode != Activity.RESULT_OK) {
            Log.i(LOG_TAG, String.format("activity result not ok"));
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Log.i(LOG_TAG, String.format("activity response of date request"));
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Log.i(LOG_TAG, String.format("activity response of contact request"));
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            ContentResolver resolver = getActivity().getContentResolver();
            Cursor c = resolver
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                c.moveToFirst();

                String suspect = c.getString(0);
                Log.i(LOG_TAG, String.format("contact name of suspect is %s", suspect));
                mCrime.setSuspect(suspect);
                updateCrime();
                updateSuspect();
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Log.i(LOG_TAG, String.format("activity response of photo request"));
            updateCrime();
            updatePhotoView();
        }
    }

    @OnClick(R.id.crime_suspect)
    public void onChooseSuspect() {
        // see http://code.tutsplus.com/tutorials/android-essentials-using-the-contact-picker--mobile-2017
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        PackageManager pm = getActivity().getPackageManager();
        ComponentName cn = contactPickerIntent.resolveActivity(pm);
        if (cn == null) {
            Toast.makeText(getContext(), "Cannot pick contact", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(LOG_TAG, "start contact picker intent");
            startActivityForResult(contactPickerIntent, REQUEST_CONTACT);
        }
    }

    @OnClick(R.id.crime_camera)
    public void onChoosePhoto() {
        // see http://developer.android.com/training/camera/photobasics.html
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoIntent.resolveActivity(getActivity().getPackageManager()) == null) {
            Toast.makeText(getContext(), "Cannot select photo", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(LOG_TAG, "start photo picker intent");
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            startActivityForResult(photoIntent, REQUEST_PHOTO);
        }
    }

    @OnClick(R.id.crime_report)
    public void onSendReport() {
        // see http://www.tutorialspoint.com/android/android_sending_email.htm
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        String[] TO = {""};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report of Crime " + mCrime.getId());
        emailIntent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
        // the attachment
        if(mPhotoFile != null) {
            emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + mPhotoFile.getPath()));
        }
        if (emailIntent.resolveActivity(getActivity().getPackageManager()) == null) {
            Toast.makeText(getContext(), "Cannot send email", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(LOG_TAG, "start sending email picker intent");
            startActivity(Intent.createChooser(emailIntent, "send mail..."));
        }
    }

    private void updateCrime() {
        crimeLab.updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void updateSuspect() {
        String suspect = mCrime.getSuspect();
        if (suspect != null && !suspect.isEmpty()) {
            mSuspectButton.setText(suspect);
        } else {
            mSuspectButton.setText(suspectDefaultString);
        }
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.getSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView() {
        Picasso.with(getContext())
                .load(mPhotoFile)
                .resize(crimePhotoWidthPx, crimePhotoHeightPx)
                .into(mPhotoView);
    }

    private ICrimeLab getICrimeLab() {
        return ((CrimeIntentApplication) getActivity().getApplication()).crimeLab;
    }
}

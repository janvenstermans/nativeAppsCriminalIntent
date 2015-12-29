package com.bignerdranch.android.criminalintent;

import com.bignerdranch.android.criminalintent.model.Crime;

import java.io.File;
import java.util.List;

/**
 * Created by janv on 29-Dec-15.
 */
public interface ICrimeLab {
    void addCrime(Crime crime);

    List<Crime> getCrimes();

    Crime getCrime(long id);

    File getPhotoFile(Crime crime);

    void updateCrime(Crime crime);
}

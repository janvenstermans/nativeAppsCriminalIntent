package com.bignerdranch.android.criminalintent;

import android.os.Environment;

import com.bignerdranch.android.criminalintent.model.Crime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by janv on 29-Dec-15.
 */
public class TestCrimeLab implements ICrimeLab {

    private static long nextNewId = 1L;

    TreeMap<Long, Crime> crimeMap = new TreeMap<>();

    @Override
    public void addCrime(Crime crime) {
        crime.setId(nextNewId);
        updateCrime(crime);
        nextNewId = nextNewId + 1;
    }

    @Override
    public List<Crime> getCrimes() {
        return new ArrayList<>(crimeMap.values());
    }

    @Override
    public Crime getCrime(long id) {
        return crimeMap.get(id);
    }

    @Override
    public File getPhotoFile(Crime crime) {
        return new File(CrimeLab.getPhotoFilename(crime));
    }

    @Override
    public void updateCrime(Crime crime) {
        if (crime.getId() != null) {
            crimeMap.put(crime.getId(), crime);
        }
    }
}

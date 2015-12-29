package com.bignerdranch.android.criminalintent;

import android.app.Application;

/**
 * Created by janv on 29-Dec-15.
 */
public class CrimeIntentApplication extends Application {

    public static ICrimeLab crimeLab;

    @Override
    public void onCreate() {
        super.onCreate();
        if (crimeLab == null) {
            crimeLab = new CrimeLab(this);
        }
    }
}

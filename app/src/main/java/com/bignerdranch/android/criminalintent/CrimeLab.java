package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.DaoMaster;
import com.bignerdranch.android.criminalintent.model.DaoSession;

import java.io.File;
import java.util.List;

/**
 * {@link ICrimeLab} implementation using {@link DaoSession}.
 */
public class CrimeLab implements ICrimeLab {
    private static CrimeLab sCrimeLab;

    private final Context mContext;
    private final DaoSession daoSession;

    public CrimeLab(Context context) {
        mContext = context.getApplicationContext();

        //create db and make a session to it.
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "crimelab-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }


    @Override
    public void addCrime(Crime crime) {
        daoSession.getCrimeDao().insertOrReplace(crime);
    }

    @Override
    public List<Crime> getCrimes() {
        return daoSession.getCrimeDao().loadAll();
    }

    @Override
    public Crime getCrime(long id) {
        return daoSession.getCrimeDao().load(id);
    }

    @Override
    public File getPhotoFile(Crime crime) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, getPhotoFilename(crime));
    }

    @Override
    public void updateCrime(Crime crime) {
        daoSession.getCrimeDao().insertOrReplace(crime);
    }

    public static String getPhotoFilename(Crime crime) {
        return "IMG_" + crime.getId().toString() + ".jpg";
    }
}

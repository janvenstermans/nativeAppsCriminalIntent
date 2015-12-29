package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bignerdranch.android.criminalintent.model.Crime;
import com.bignerdranch.android.criminalintent.model.DaoMaster;
import com.bignerdranch.android.criminalintent.model.DaoSession;

import java.io.File;
import java.util.List;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private DaoSession daoSession;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        //create db and make a session to it.
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "crimelab-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }


    public void addCrime(Crime crime) {
        daoSession.getCrimeDao().insertOrReplace(crime);
    }

    public List<Crime> getCrimes() {
        return daoSession.getCrimeDao().loadAll();
    }

    public Crime getCrime(long id) {
        return daoSession.getCrimeDao().load(id);
    }

    public File getPhotoFile(Crime crime) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, getPhotoFilename(crime));
    }

    public void updateCrime(Crime crime) {
        daoSession.getCrimeDao().insertOrReplace(crime);
    }

    public static String getPhotoFilename(Crime crime) {
        return "IMG_" + crime.getId().toString() + ".jpg";
    }
}

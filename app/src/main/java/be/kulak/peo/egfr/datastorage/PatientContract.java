package be.kulak.peo.egfr.datastorage;

import android.provider.BaseColumns;

/**
 * Created by elias on 02/05/17.
 */

public class PatientContract {
    public static class PatientEntry implements BaseColumns{
        public static final String TABLE_NAME = "patient";
        public static final String COLUMN_FIRST_NAME = "firstname";
        public static final String COLUMN_LAST_NAME = "lastname";
        public static final String COLUMN_BIRTHDAY = "birthday";

    }
}

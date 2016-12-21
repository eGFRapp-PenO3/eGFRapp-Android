package be.kulak.peo.egfr;

import android.provider.BaseColumns;

/**
 * Created by elias on 21/12/16.
 */

public final class HistoryContract {
    private HistoryContract(){}

    public static final String type_int = " INTEGER,";
    public static final String type_string = " TEXT,";
    public static final String type_double = " REAL,";

    public static class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_ID = "id";

        public static final String[][] COLUMNS = {
                {"fn", type_string},
                {"ln", type_string},
                {"date", type_string},
                {"age", type_double},
                {"sex", type_int},
                {"scr", type_double},
                {"cisc", type_double},
                {"hgt", type_double},
                {"wgt", type_double}
        };
    }
}

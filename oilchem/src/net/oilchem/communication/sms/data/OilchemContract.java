package net.oilchem.communication.sms.data;

import android.provider.BaseColumns;
import android.util.Log;

public final class OilchemContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_OILCHEM_SMS =
            "CREATE TABLE " + OilchemSmsEntry.TABLE_NAME + " (" +
                    OilchemSmsEntry._ID + " INTEGER PRIMARY KEY," +
                    OilchemSmsEntry.COLUMN_NAME_SMS_MSGID + TEXT_TYPE + COMMA_SEP +
                    OilchemSmsEntry.COLUMN_NAME_SMS_TITLE + TEXT_TYPE + COMMA_SEP +
                    OilchemSmsEntry.COLUMN_NAME_SMS_CONTENT + TEXT_TYPE + COMMA_SEP +
                    OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID + TEXT_TYPE + COMMA_SEP +
                    OilchemSmsEntry.COLUMN_NAME_SMS_GROUPNAME + TEXT_TYPE + COMMA_SEP +
                    OilchemSmsEntry.COLUMN_NAME_SMS_TS + TEXT_TYPE + COMMA_SEP +
                    OilchemSmsEntry.COLUMN_NAME_SMS_REPLIES + TEXT_TYPE +
                    " );";

    private static final String SQL_DELETE_OILCHEM_SMS = "DROP TABLE IF EXISTS " + OilchemSmsEntry.TABLE_NAME;

    public static String getSqlCreateOilchemSms() {
        Log.d("sql", "sql " + SQL_CREATE_OILCHEM_SMS);
        return SQL_CREATE_OILCHEM_SMS;
    }

    public static String getSqlDeleteOilchemSms() {
        return SQL_DELETE_OILCHEM_SMS;
    }

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private OilchemContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class OilchemSmsEntry implements BaseColumns {
        public static final String TABLE_NAME = "oilchem_sms";
        public static final String COLUMN_NAME_SMS_MSGID = "msgId";
        public static final String COLUMN_NAME_SMS_TITLE = "title";
        public static final String COLUMN_NAME_SMS_CONTENT = "content";
        public static final String COLUMN_NAME_SMS_TS = "ts";
        public static final String COLUMN_NAME_SMS_GROUPID = "group_id";
        public static final String COLUMN_NAME_SMS_GROUPNAME = "group_name";
        public static final String COLUMN_NAME_SMS_REPLIES = "replies";
    }
}

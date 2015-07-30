package net.oilchem.communication.sms.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OilchemDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 2;
//	public static final String DATABASE_NAME = "oilchem";
	public static  String DATABASE_NAME;
    public static Context ctx;
	
	public OilchemDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(OilchemContract.getSqlCreateOilchemSms());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion ==1 && newVersion==2){
               db.execSQL(" alter table "+ OilchemContract.OilchemSmsEntry.TABLE_NAME+
                       " add ["+OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_REPLIES+"] text ");
            db.execSQL(" alter table "+ OilchemContract.OilchemSmsEntry.TABLE_NAME+
                    " add ["+OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_MSGID+"] text ");
            db.execSQL(" update "+ OilchemContract.OilchemSmsEntry.TABLE_NAME+
                    " set "+OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_MSGID+"='0' " +
                    " where "+OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_MSGID+" is null ");
        }else{
            db.execSQL(OilchemContract.getSqlDeleteOilchemSms());
            onCreate(db);
        }

//        SharedPreferenceUtil.clear(ctx, Constant.SHAREDREFERENCES_CONFIG);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}

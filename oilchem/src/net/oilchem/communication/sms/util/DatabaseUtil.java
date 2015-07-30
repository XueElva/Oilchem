package net.oilchem.communication.sms.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import net.oilchem.communication.sms.Constant;
import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.data.OilchemContract;
import net.oilchem.communication.sms.data.OilchemDbHelper;
import net.oilchem.communication.sms.data.OilchemContract.OilchemSmsEntry;
import net.oilchem.communication.sms.data.model.SmsInfo;

import java.util.ArrayList;

public class DatabaseUtil {

	private OilchemDbHelper mDbHelper;

	public DatabaseUtil() {
		mDbHelper = new OilchemDbHelper(
				OilchemApplication.getContextFromApplication());
	}

	private static class SingletonHolder {
		private static DatabaseUtil instance = null;

		public static DatabaseUtil getInstance() {
			if (null == instance) {
				instance = new DatabaseUtil();
				clearExpiredData();
			}
			return instance;
		}

	}

	public static void clearDatabaseUtilInstance() {
		SingletonHolder.instance = null;
	}

	public static DatabaseUtil getInstance() {
		return SingletonHolder.getInstance();
	}

	public void insert(ArrayList<SmsInfo> messages) {
		for (int i = 0; i < messages.size(); i++) {
			SmsInfo sms = messages.get(i);
			if (!hasSmsInfo(sms)) {
				insert(sms);
			}
		}
	}

	public boolean hasSmsInfo(SmsInfo info) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.rawQuery("select count(*) from "
					+ OilchemContract.OilchemSmsEntry.TABLE_NAME + " where "
					+ OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS
					+ "=?;", new String[] { info.getTs() });
			// Cursor c = db.rawQuery("SELECT COUNT(*) FROM " +
			// OilchemContract.OilchemSmsEntry.TABLE_NAME, null);
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
			return count > 0;
		} catch (Exception e) {
		}
		return true;
	}

	public long insert(SmsInfo sms) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_MSGID,
				sms.getMsgId());
		values.put(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TITLE,
				sms.getGroupName());
		values.put(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_CONTENT,
				sms.getContent());
		values.put(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
				sms.getTs());
		values.put(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID,
				sms.getGroupId());
		values.put(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPNAME,
				sms.getGroupName());
		values.put(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_REPLIES,
				sms.getReplies());
		return db.insert(OilchemContract.OilchemSmsEntry.TABLE_NAME, null,
				values);
	}

	public long update(SmsInfo sms) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_REPLIES,
				sms.getReplies());

		long rowAffected = db.update(
				OilchemContract.OilchemSmsEntry.TABLE_NAME, values,
				OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_MSGID + " = ?",
				new String[] { sms.getMsgId() });
		return rowAffected;
	}

	public void delete(SmsInfo sms) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(OilchemContract.OilchemSmsEntry.TABLE_NAME,
				OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS + " = ?",
				new String[] { sms.getTs() });
	}

	/**
	 * ts: delete all infos where ts < given ts;
	 */
	public void delete(String ts) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(OilchemContract.OilchemSmsEntry.TABLE_NAME,
				OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS + " < ?",
				new String[] { ts });
	}

	public static void clearExpiredData() {
		String cacheTime = SharedPreferenceUtil.getString(
				Constant.SHAREDREFERENCES_CONFIG,
				Constant.SHAREDREFERENCES_CONFIG_CONFIGURATION_CACHETIME,
				OilchemApplication
						.getResourceString(R.string.cachetime_forever));
		String whereClause = "";
		if (TextUtils.equals(cacheTime, OilchemApplication
				.getResourceString(R.string.cachetime_forever))) {
			return;
		} else if (TextUtils.equals(cacheTime,
				OilchemApplication.getResourceString(R.string.cachetime_four))) {
			whereClause = String.format(
					" %s <= '%s';",
					OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
					String.valueOf(System.currentTimeMillis() - 120 * 3600 * 24
							* 1000));
		} else if (TextUtils.equals(cacheTime,
				OilchemApplication.getResourceString(R.string.cachetime_three))) {
			whereClause = String.format(
					" %s <= '%s';",
					OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
					String.valueOf(System.currentTimeMillis() - 90 * 3600 * 24
							* 1000));
		} else if (TextUtils.equals(cacheTime,
				OilchemApplication.getResourceString(R.string.cachetime_two))) {
			whereClause = String.format(
					" %s <= '%s' ",
					OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS,
					String.valueOf(System.currentTimeMillis() - 60 * 3600 * 24
							* 1000));
		}
		SQLiteDatabase db = new OilchemDbHelper(
				OilchemApplication.getContextFromApplication())
				.getReadableDatabase();
		db.delete(OilchemContract.OilchemSmsEntry.TABLE_NAME, whereClause, null);
	}

	/**
	 * 清除缓存
	 * 
	 * @param clear
	 * @return
	 */
	public static int clearExpiredData(boolean clear) {
		if (clear) {
			SQLiteDatabase db = new OilchemDbHelper(
					OilchemApplication.getContextFromApplication())
					.getReadableDatabase();
			SharedPreferenceUtil.setString(Constant.SHAREDREFERENCES_CONFIG,
					Constant.SHAREDREFERENCES_WELCOME_LASTTS, "");
			ArrayList<String> collectedList = XmlUtil.getCollectedList();
			String[] where = new String[collectedList.size()];
			StringBuffer notIn = new StringBuffer();
			for (int i = 0; i < collectedList.size(); i++) {
				where[i]=collectedList.get(i);
				notIn.append("group_id!=? ");
				if (i < collectedList.size() - 1) {
					notIn.append("and ");
				}
			}
			return db.delete(OilchemContract.OilchemSmsEntry.TABLE_NAME,
					notIn.toString(), where);

		}
		return 0;
	}

	public ArrayList<SmsInfo> query(String whereClause) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selectQuery = "SELECT * FROM "
				+ OilchemContract.OilchemSmsEntry.TABLE_NAME + whereClause
				+ " ORDER BY "
				+ OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS + " DESC";
		Cursor c = db.rawQuery(selectQuery, null);
		Log.d("query", selectQuery);
		if (c != null)
			c.moveToFirst();
		ArrayList<SmsInfo> smsList = new ArrayList<SmsInfo>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			SmsInfo info = new SmsInfo();
			info.setTs(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS)));
			info.setMsgId(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_MSGID)));
			info.setTitle(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TITLE)));
			info.setContent(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_CONTENT)));
			info.setGroupName(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPNAME)));
			info.setGroupId(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID)));
			info.setReplies(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_REPLIES)));
			smsList.add(info);
		}
		c.close();
		return smsList;
	}

	public ArrayList<SmsInfo> query(String keyword, String lastTs, int offset,
			int limit) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selectQuery = "SELECT * FROM "
				+ OilchemContract.OilchemSmsEntry.TABLE_NAME
				+ " WHERE "
				+ OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TITLE
				+ " LIKE '%"
				+ keyword
				+ "%'"
				+ " OR "
				+ OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_CONTENT
				+ " LIKE '%"
				+ keyword
				+ "%' "
				+ "AND "
				+ OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS
				+ " > '"
				+ lastTs
				+ "'"
				+ " ORDER BY "
				+ OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS
				+ " DESC"
				+ String.format(" LIMIT %S, %s", String.valueOf(offset),
						String.valueOf(limit));
		Cursor c = db.rawQuery(selectQuery, null);
		Log.d("query", selectQuery);
		if (c != null)
			c.moveToFirst();
		ArrayList<SmsInfo> smsList = new ArrayList<SmsInfo>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			SmsInfo info = new SmsInfo();
			info.setTs(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TS)));
			info.setMsgId(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_MSGID)));
			info.setTitle(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_TITLE)));
			info.setContent(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_CONTENT)));
			info.setGroupId(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPID)));
			info.setGroupName(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_GROUPNAME)));
			info.setReplies(c.getString(c
					.getColumnIndex(OilchemContract.OilchemSmsEntry.COLUMN_NAME_SMS_REPLIES)));
			smsList.add(info);
		}
		c.close();
		return smsList;
	}
}

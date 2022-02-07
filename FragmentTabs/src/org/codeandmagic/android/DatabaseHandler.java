package org.codeandmagic.android;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "TrailDatabase";
	private static final String TABLE_TRAILDATA = "traildata";

	private static final String KEY_ID = "id";
	private static final String KEY_TYPE = "type";
	private static final String KEY_SUMMARY = "summary";
	private static final String KEY_FULLDATA = "fulldata";

	public DatabaseHandler(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_TRAILDATA_TABLE = "CREATE TABLE " + TABLE_TRAILDATA + "("
						+ KEY_ID + " INTEGER, " + KEY_TYPE + " INTEGER, "
						+ KEY_SUMMARY + " TEXT," + KEY_FULLDATA + " TEXT" + ")";
		Log.d("DATABASE", CREATE_TRAILDATA_TABLE);
		db.execSQL(CREATE_TRAILDATA_TABLE);

		/*
		String insert = "INSERT INTO " + TABLE_SCORES + " (" + KEY_MAP + ", " + KEY_NAME + ", " + KEY_SCORE
				 			+ ") VALUES (0, 'ABC', '1.4')";
		db.execSQL(insert);

		 insert = "INSERT INTO " + TABLE_SCORES + " (" + KEY_MAP + ", " + KEY_NAME + ", " + KEY_SCORE
	 			+ ") VALUES (0, 'ABC', '1.4')";
db.execSQL(insert);
 insert = "INSERT INTO " + TABLE_SCORES + " (" + KEY_MAP + ", " + KEY_NAME + ", " + KEY_SCORE
	+ ") VALUES (0, 'ABC', '1.4')";
db.execSQL(insert);*/

		//addScore(new HighScore(0,1.4f,"TES"));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// drop older table if it exists
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAILDATA);

		onCreate(db);
	}

	public void addData(int type, TrailData trailData)
	{
		int dbID;
		if(type == 1)
		{
			// values inserted from download come with their own online uid
			dbID = Integer.parseInt(trailData.getUID());
		}
		else
		{
			// get the next value
			int max = -1;
			List<TrailData> data = getTrailData(0, true);
			for(int i = 0; i < data.size(); i++)
			{
				int curValue = Integer.parseInt(data.get(i).getUID());
				if(curValue > max)
				{
					max = curValue;
				}
			}
			max++;
			dbID = max;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, dbID);
		values.put(KEY_TYPE, type);
		values.put(KEY_SUMMARY, trailData.convertToXMLString(false));
		values.put(KEY_FULLDATA, trailData.convertToXMLString(true));

		try{
			db.beginTransaction();
			db.insertOrThrow(TABLE_TRAILDATA, null, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	public List<TrailData> getTrailData(int type, boolean useSummary)
	{
		List<TrailData> mapList = new ArrayList<TrailData>();
		String selectQuery = "SELECT * FROM " + TABLE_TRAILDATA + " WHERE " + KEY_TYPE + " = ?";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, new String [] { String.valueOf(type) } );

		if(cursor.moveToFirst()) {
			do {
				TrailData trailData;
				if(useSummary)
				{
					trailData = new TrailData(cursor.getString(2));
				}
				else
				{
					trailData = new TrailData(cursor.getString(3));
				}
				trailData.setUID(cursor.getString(0));

				mapList.add(trailData);
			} while(cursor.moveToNext());
		}
		db.close();
		return mapList;
	}

	public TrailData getTrailData(int id, int type)
	{
		String selectQuery = "SELECT * FROM " + TABLE_TRAILDATA + " WHERE " + KEY_ID
							+ " = ? AND " + KEY_TYPE + " = ?";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery,
					new String [] { String.valueOf(id), String.valueOf(type) } );

		TrailData trailData = null;
		if(cursor.moveToFirst()) {
			do {
				trailData = new TrailData(cursor.getString(3));
				Log.d("DatabaseHandler", cursor.getString(3));
				trailData.setUID(cursor.getString(0));

			} while(cursor.moveToNext());
		}
		db.close();

		return trailData;
	}

	public void deleteData(int id, int type)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TRAILDATA, KEY_ID + "=? AND " + KEY_TYPE + "=?",
					new String[] { String.valueOf(id), String.valueOf(type) });
		db.close();
	}

	public void clearDatabase()
	{
		Log.d("DATABASE", "Clearing Database");
		SQLiteDatabase db = this.getWritableDatabase();
		onUpgrade(db, 1, 1);
		db.close();
	}
}


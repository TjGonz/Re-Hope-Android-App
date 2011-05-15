package co.uk.rehope.android;

import java.util.HashMap;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReHopeDB {
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_TABLE_EVENTS = "create table if not exists events (id integer primary key autoincrement, title text, date text, content text, imageURL text, cityURL text);";
	private static final String CREATE_TABLE_MESSAGES = "create table if not exists messages (id integer primary key autoincrement, title text, content text, verse text, date text, imageURL text, mp3URL text);";
	private static final String CREATE_TABLE_FEATURES = "create table if not exists features (id integer primary key autoincrement, title text, date text, imageURL text, link text);";
	private static final String CREATE_TABLE_GLOBAL = "create table if not exists global (id integer primary key autoincrement, lastRefresh integer);";

	private String DATABASE_NAME = "rehope";

	private SQLiteDatabase db;

	public ReHopeDB(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		db.execSQL(CREATE_TABLE_EVENTS);
		db.execSQL(CREATE_TABLE_MESSAGES);
		db.execSQL(CREATE_TABLE_FEATURES);
		db.execSQL(CREATE_TABLE_GLOBAL);
		db.setVersion(DATABASE_VERSION);
		db.close();
	}

	public boolean saveEvent(Context ctx, HashMap event) {

		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		ContentValues values = new ContentValues();
		values.put("imageURL", event.get("image").toString());
		values.put("title", event.get("title").toString());
		values.put("content", event.get("content").toString());
		values.put("date", event.get("date").toString());
		values.put("cityURL", event.get("city_link").toString());
		values.put("date", event.get("date").toString());
		boolean returnValue = db.insert("events", null, values) > 0;
		db.close();

		return (returnValue);
	}

	public Vector<HashMap<String, Object>> loadEvents(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		Vector<HashMap<String, Object>> returnVector = new Vector<HashMap<String, Object>>();
		Cursor c = db.query("events", new String[] { "imageURL", "title",
				"content", "date", "cityURL" }, null, null, null, null, null);

		int numRows = c.getCount();
		c.moveToFirst();

		for (int i = 0; i < numRows; ++i) {
			if (c.getString(0) != null) {
				HashMap<String, Object> returnHash = new HashMap<String, Object>();
				returnHash.put("imageURL", c.getString(0));
				returnHash.put("title", c.getString(1));
				returnHash.put("content", c.getString(2));
				returnHash.put("date", c.getString(3));
				returnHash.put("cityURL", c.getString(4));
				returnVector.add(i, returnHash);
			}
			c.moveToNext();
		}
		c.close();
		db.close();

		return returnVector;
	}

	public void clearEvents(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		// delete existing values
		db.delete("events", null, null);
		db.close();
	}

	public boolean saveMessage(Context ctx, HashMap event) {

		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		ContentValues values = new ContentValues();
		values.put("title", event.get("title").toString());
		values.put("content", event.get("content").toString());
		// values.put("verse", event.get("verse").toString());
		values.put("date", event.get("date").toString());
		values.put("imageURL", event.get("image").toString());
		values.put("mp3URL", event.get("mp3_url").toString());
		boolean returnValue = db.insert("messages", null, values) > 0;
		db.close();

		return (returnValue);
	}

	public Vector<HashMap<String, Object>> loadMessages(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		Vector<HashMap<String, Object>> returnVector = new Vector<HashMap<String, Object>>();
		Cursor c = db.query("messages", new String[] { "title", "content",
				"date", "imageURL", "mp3URL" }, null, null, null, null, null);

		int numRows = c.getCount();
		c.moveToFirst();

		for (int i = 0; i < numRows; ++i) {
			if (c.getString(0) != null) {
				HashMap<String, Object> returnHash = new HashMap<String, Object>();
				returnHash.put("title", c.getString(0));
				returnHash.put("content", c.getString(1));
				// returnHash.put("verse", c.getString(3));
				returnHash.put("date", c.getString(2));
				returnHash.put("imageURL", c.getString(3));
				returnHash.put("mp3URL", c.getString(4));
				returnVector.add(i, returnHash);
			}
			c.moveToNext();
		}
		c.close();
		db.close();

		return returnVector;
	}

	public void clearMessages(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		// delete existing values
		db.delete("messages", null, null);
		db.close();
	}

	public boolean saveFeature(Context ctx, HashMap data) {

		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		ContentValues values = new ContentValues();
		// values.put("title", event.get("title").toString());
		// values.put("date", event.get("date").toString());
		values.put("imageURL", data.get("image").toString());
		values.put("link", data.get("link").toString());
		boolean returnValue = db.insert("features", null, values) > 0;
		db.close();

		return (returnValue);
	}

	public Vector<HashMap<String, Object>> loadFeatures(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		Vector<HashMap<String, Object>> returnVector = new Vector<HashMap<String, Object>>();
		Cursor c = db.query("features", new String[] { "imageURL", "link" },
				null, null, null, null, null);

		int numRows = c.getCount();
		c.moveToFirst();

		for (int i = 0; i < numRows; ++i) {
			if (c.getString(0) != null) {
				HashMap<String, Object> returnHash = new HashMap<String, Object>();
				// returnHash.put("title", c.getString(0));
				// returnHash.put("date", c.getString(1));
				returnHash.put("imageURL", c.getString(0));
				returnHash.put("link", c.getString(1));
				returnVector.add(i, returnHash);
			}
			c.moveToNext();
		}
		c.close();
		db.close();

		return returnVector;
	}

	public void clearFeatures(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		// delete existing values
		db.delete("features", null, null);
		db.close();
	}

	public void setLastRefresh(Context ctx, boolean firstRun) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		ContentValues values = new ContentValues();
		values.put("lastRefresh", System.currentTimeMillis()); // set to current
																// time
		if (firstRun) {
			boolean returnValue = db.insert("global", null, values) > 0;
			if (returnValue) {
			}
			;
		} else {
			boolean returnValue = db.update("global", values, "id=1", null) > 0;
			if (returnValue) {
			}
			;
		}

		db.close();
	}

	public long getLastRefresh(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);

		Cursor c = db.query("global", new String[] { "lastRefresh" }, "id=1",
				null, null, null, null);
		int numRows = c.getCount();
		c.moveToFirst();
		long returnValue = 0;
		if (numRows == 1) {
			returnValue = c.getLong(0);
		}
		c.close();
		db.close();
		return returnValue;
	}

}

package com.nostra13.example.universalimageloader;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wzw.ic.mvc.ViewItem;

public class IcDatabase {
	private static final String TAG = "IcDatabase";
	
	private static final String DATABASE_NAME = "ic.db";
	private static final int DATABASE_VERSION = 1;

	private Context mCtx;
	private DatabaseOpenHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
    		"CREATE TABLE hearts (" +
    		"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
    		"label TEXT," +
    		"imageUrl TEXT NOT NULL," +
    		"nodeUrl TEXT," +
    		"story TEXT);";
    
    private volatile static IcDatabase instance;

	/** Returns singleton class instance */
	public static IcDatabase getInstance() {
		if (instance == null) {
			synchronized (IcDatabase.class) {
				if (instance == null) {
					instance = new IcDatabase();
				}
			}
		}
		return instance;
	}
    
    protected IcDatabase() {}
    
    public synchronized void open(Context context) {
    	mCtx = context;
    	mDbHelper = new DatabaseOpenHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    public long addViewItemToHearts(ViewItem item) {
    	removeViewItemFromHearts(item);
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put("label", item.getLabel());
        initialValues.put("imageUrl", item.getImageUrl());
        initialValues.put("story", item.getStory());
        initialValues.put("nodeUrl", item.getNodeUrl());
        return mDb.insert("hearts", null, initialValues);
    }
    
    public boolean removeViewItemFromHearts(ViewItem item) {
    	return mDb.delete("hearts", "imageUrl='"+item.getImageUrl()+"'", null) > 0;
    }
    
    public boolean isViewItemInHearts(ViewItem item) {
		Cursor mCursor = mDb.query(true, "hearts", new String[] {"_id"}, "imageUrl='"+item.getImageUrl()+"'", null, null, null, null, "1");
		boolean isIn = (mCursor != null && mCursor.moveToFirst());
		if (mCursor != null) {
			mCursor.close();
		}
		return isIn;
    }

    public List<ViewItem> fetchAllViewItemsInHearts(int offset, int limit) {
    	List<ViewItem> pageViewItems = null;
		
    	Cursor cursor = mDb.query("hearts", null, null, null, null, null, null, offset+","+limit);
    	if (cursor != null) {
			cursor.moveToFirst();
			pageViewItems = new ArrayList<ViewItem>();
			while (cursor.isAfterLast() == false) {
				String label = cursor.getString(cursor.getColumnIndex("label"));
				String nodeUrl = cursor.getString(cursor.getColumnIndex("nodeUrl"));
				String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
				String story = cursor.getString(cursor.getColumnIndex("story"));
				ViewItem item = new ViewItem(label, nodeUrl, imageUrl, 0);
				item.setStory(story);
				pageViewItems.add(item);
				cursor.moveToNext();
	        }
			cursor.close();
		}
    	
    	return pageViewItems;
    }
    
    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
    	DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS hearts");
            onCreate(db);
        }        
    }
}

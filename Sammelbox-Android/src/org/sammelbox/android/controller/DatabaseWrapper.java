package org.sammelbox.android.controller;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseWrapper {
	private static DatabaseOpenHelper databaseOpenHelper = null;
	private static SQLiteDatabase singleDBInstance = null;
	
	public static SQLiteDatabase getSQLiteDatabaseInstance(Context context) {
		if (singleDBInstance != null) {
			return singleDBInstance;
		}
		
		DatabaseOpenHelper openHelper = new DatabaseOpenHelper(context);
		singleDBInstance = openHelper.getReadableDatabase();
		
		return singleDBInstance;
	}
	
	public static void closeDBConnection(SQLiteDatabase db) {
		databaseOpenHelper.close();
	}
    
	public static Cursor executeRawSQLQuery(SQLiteDatabase database, String rawQuery) {
		return database.rawQuery(rawQuery, null);
	}
	
	public static Cursor executeRawSQLQuery(SQLiteDatabase database, String rawQuery, String argument) {
		return database.rawQuery(rawQuery, new String[] { argument} );
	}
	
	public static Cursor executeRawSQLQuery(SQLiteDatabase database, String rawQuery, String[] arguments) {
		return database.rawQuery(rawQuery, arguments);
	}
}

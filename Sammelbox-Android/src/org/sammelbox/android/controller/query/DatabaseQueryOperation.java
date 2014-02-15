package org.sammelbox.android.controller.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sammelbox.R;
import org.sammelbox.android.controller.AppState;
import org.sammelbox.android.controller.DatabaseWrapper;
import org.sammelbox.android.model.FieldType;
import org.sammelbox.android.model.SimplifiedAlbumItemResultSet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class DatabaseQueryOperation {
	private static final int MAX_NUMBER_OF_STARS = 5;

	public static Map<String, String> getAlbumNamesToAlbumTablesMapping(Context context) {
		Cursor cursor = DatabaseWrapper.executeRawSQLQuery(
				DatabaseWrapper.getSQLiteDatabaseInstance(context), "select * from album_master_table");
		
		final Map<String,String> albumNameToTableName = new HashMap<String, String>();
		
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() != true) {
				albumNameToTableName.put(cursor.getString(cursor.getColumnIndex("album_name")), 
						cursor.getString(cursor.getColumnIndex("album_table_name")));
				
				cursor.moveToNext();
			}
		}
		cursor.close();
		
		return albumNameToTableName;
	}
	
	public static SimplifiedAlbumItemResultSet getAllAlbumItemsFromAlbum(Context context) {
		String selectedTableName = AppState.getAlbumNameToTableName(context).get(AppState.getSelectedAlbum());
		Cursor cursor = DatabaseWrapper.executeRawSQLQuery(
				DatabaseWrapper.getSQLiteDatabaseInstance(context), "select * from " + selectedTableName);
		
		return getAlbumItems(context, cursor);
	}
	
	public static SimplifiedAlbumItemResultSet getAlbumItems(Context context, Cursor cursor) {
		SimplifiedAlbumItemResultSet simplifiedAlbumItemResultSet = new SimplifiedAlbumItemResultSet();
		
		String selectedTableName = AppState.getAlbumNameToTableName(context).get(AppState.getSelectedAlbum());
		Map<String, FieldType> fieldNameToTypeMapping = 
				retrieveFieldnameToFieldTypeMapping(DatabaseWrapper.getSQLiteDatabaseInstance(context), context, selectedTableName);
		
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() != true) {
				Long itemID = cursor.getLong(cursor.getColumnIndex("id"));
				
				Drawable placeHolderImage = context.getResources().getDrawable(R.drawable.placeholder);
				StringBuilder data = new StringBuilder();
				
				for (String fieldName : fieldNameToTypeMapping.keySet()) {
					data.append("<b>" + fieldName + "</b>: " + readToStringByFieldnameAndType(
							cursor, fieldNameToTypeMapping, fieldName, selectedTableName) + "<br>");
				}
				
				Drawable primaryImage = retrievePrimaryImage(
						context, selectedTableName, String.valueOf(itemID));
				if (primaryImage == null) {
					primaryImage = placeHolderImage;
				}
				
				simplifiedAlbumItemResultSet.addSimplifiedAlbumItem(itemID, primaryImage, data.toString());
				cursor.moveToNext();
			}
		}
		cursor.close();
		
		return simplifiedAlbumItemResultSet;
	}
	
	private static Drawable retrievePrimaryImage(Context context, String albumTableName, String albumItemId) {
		Cursor cursor = DatabaseWrapper.executeRawSQLQuery(
				DatabaseWrapper.getSQLiteDatabaseInstance(context),
				"select * from " + albumTableName + "_pictures where album_item_foreign_key = ?", albumItemId);
		
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() != true) {				
				String thumbnailName = cursor.getString(cursor.getColumnIndex("thumbnail_picture_filename")); 
				Drawable drawable = Drawable.createFromPath(Environment.getExternalStorageDirectory() + "/Sammelbox/thumbnails/" + thumbnailName);
				cursor.close();
				
				return drawable;
			}
		}
		
		return null;
	}
	
	public static Map<String, FieldType> retrieveFieldnameToFieldTypeMapping(SQLiteDatabase database, Context context, String albumTableName) {
		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<FieldType> fieldTypes = new ArrayList<FieldType>();
		
		Cursor columnCursor = database.rawQuery("PRAGMA table_info(" + albumTableName + "_typeinfo)", null);
		if (columnCursor.moveToFirst()) {
		    do {
		    	String fieldName = columnCursor.getString(1);
		    	if (!DatabaseQueryHelper.isSpecialField(fieldName)) {
		    		fieldNames.add(fieldName);
		    	}
		    } while (columnCursor.moveToNext());
		}
		
		Cursor cursor = DatabaseWrapper.executeRawSQLQuery(
				DatabaseWrapper.getSQLiteDatabaseInstance(context),
				"select * from " + albumTableName + "_typeinfo");
				
		if (cursor.moveToFirst()) {
			for (String fieldName : fieldNames) {
				if (!DatabaseQueryHelper.isSpecialField(fieldName)) {
					fieldTypes.add(FieldType.valueOf(cursor.getString(cursor.getColumnIndex(fieldName))));	
				}
			}		
		}
		cursor.close();
		
		Map<String, FieldType> fieldNameToTypeMapping = new LinkedHashMap<String, FieldType>();
		for (int i=0; i<fieldNames.size(); i++) {
			fieldNameToTypeMapping.put(fieldNames.get(i), fieldTypes.get(i));
		}
		
		return fieldNameToTypeMapping;
	}
	
	public static String readToStringByFieldnameAndType(Cursor cursor, Map<String, FieldType> fieldNamesToTypes, String fieldName, String albumTableName) {		
		if (fieldNamesToTypes.get(fieldName).equals(FieldType.TEXT)) {
			return cursor.getString(cursor.getColumnIndex(fieldName));
		} else if (fieldNamesToTypes.get(fieldName).equals(FieldType.INTEGER)) {
			return String.valueOf(cursor.getInt(cursor.getColumnIndex(fieldName)));
		} else if (fieldNamesToTypes.get(fieldName).equals(FieldType.DECIMAL)) {
			return String.valueOf(cursor.getDouble(cursor.getColumnIndex(fieldName)));
		} else if (fieldNamesToTypes.get(fieldName).equals(FieldType.DATE)) {
			return String.valueOf(cursor.getLong(cursor.getColumnIndex(fieldName)));
		} else if (fieldNamesToTypes.get(fieldName).equals(FieldType.OPTION)) {
			return cursor.getString(cursor.getColumnIndex(fieldName));
		} else if (fieldNamesToTypes.get(fieldName).equals(FieldType.STAR_RATING)) {
			int numberOfStars = cursor.getInt(cursor.getColumnIndex(fieldName));
			StringBuffer stringBuffer = new StringBuffer();
			for (int i=0; i<MAX_NUMBER_OF_STARS; i++) {
				if (i < numberOfStars) {
					stringBuffer.append("\u2605");
				} else {
					stringBuffer.append("\u2606");
				}
			}
			
			return stringBuffer.toString(); 
		} else if (fieldNamesToTypes.get(fieldName).equals(FieldType.URL)) {
			return cursor.getString(cursor.getColumnIndex(fieldName));
		}
		
		return null;
	}
	
	public static ArrayList<Drawable> getImages(Context context, String albumTableName, Long albumItemID) {
		Cursor cursor = DatabaseWrapper.executeRawSQLQuery(
				DatabaseWrapper.getSQLiteDatabaseInstance(context),
				"select * from " + albumTableName + "_pictures where album_item_foreign_key = ?", 
				String.valueOf(albumItemID));

		ArrayList<Drawable> galleryImages = new ArrayList<Drawable>();
		
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() != true) {				
				String thumbnailName = cursor.getString(cursor.getColumnIndex("thumbnail_picture_filename")); 
				galleryImages.add(Drawable.createFromPath(Environment.getExternalStorageDirectory() + "/Sammelbox/thumbnails/" + thumbnailName));
				
				cursor.moveToNext();
			}
		}

		cursor.close();
		
		return galleryImages;
	}
	
	public static ArrayList<String> getPathsToFullImages(Context context, String albumName, String albumTableName, Long albumItemID) {
		Cursor cursor = DatabaseWrapper.executeRawSQLQuery(
				DatabaseWrapper.getSQLiteDatabaseInstance(context),
				"select * from " + albumTableName + "_pictures where album_item_foreign_key = ?", 
				String.valueOf(albumItemID));

		ArrayList<String> galleryImages = new ArrayList<String>();
		
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() != true) {				
				String originalName = cursor.getString(cursor.getColumnIndex("original_picture_filename")); 
				galleryImages.add(Environment.getExternalStorageDirectory() + "/Sammelbox/album-pictures/" + albumName + "/" + originalName);
				
				cursor.moveToNext();
			}
		}

		cursor.close();
		
		return galleryImages;
	}
}

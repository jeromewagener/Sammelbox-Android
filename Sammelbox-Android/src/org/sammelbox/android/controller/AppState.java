package org.sammelbox.android.controller;

import java.util.Map;

import org.sammelbox.android.controller.query.DatabaseQueryOperation;
import org.sammelbox.android.model.SimplifiedAlbumItemResultSet;

import android.content.Context;

public class AppState {
	private static String selectedAlbum = null;
	private static Long selectedAlbumItemID = null;
	private static Map<String,String> albumNameToTableName = null;
	/** The last simplified album item result set that was retrieved */
	private static SimplifiedAlbumItemResultSet simplifiedAlbumItemResultSet = null;
	
	public static String getSelectedAlbum() {
		return selectedAlbum;
	}

	public static void setSelectedAlbum(String selectedAlbum) {
		AppState.selectedAlbum = selectedAlbum;
	}

	public static Map<String,String> getAlbumNameToTableName(Context context) {
		if (albumNameToTableName == null) {
			albumNameToTableName = DatabaseQueryOperation.getAlbumNamesToAlbumTablesMapping(context);
		}
		
		return albumNameToTableName;
	}

	public static void setAlbumNameToTableName(Map<String,String> albumNameToTableName) {
		AppState.albumNameToTableName = albumNameToTableName;
	}

	public static SimplifiedAlbumItemResultSet getSimplifiedAlbumItemResultSet() {
		return simplifiedAlbumItemResultSet;
	}

	public static void setSimplifiedAlbumItemResultSet(
			SimplifiedAlbumItemResultSet simplifiedAlbumItemResultSet) {
		AppState.simplifiedAlbumItemResultSet = simplifiedAlbumItemResultSet;
	}

	public static void setSelectedAlbumItemID(Long itemID) {
		AppState.selectedAlbumItemID = itemID;
	}
	
	public static Long getSelectedAlbumItemID() {
		return AppState.selectedAlbumItemID;
	}
}

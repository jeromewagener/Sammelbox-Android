package org.sammelbox.android.controller.filesystem;

import java.io.File;

import android.os.Environment;

public class FileSystemLocations {
	public static final String SAMMELBOX_HOME = Environment.getExternalStorageDirectory() + "/sammelbox/";
	public static final String SAVED_SEARCHES_XML_FILE = SAMMELBOX_HOME + "app-data/saved-searches.xml";
	public static final String ALBUM_PICTURES_FOLDER = SAMMELBOX_HOME + "album-pictures";
	
	private static final File albumPicturesFolder = new File(ALBUM_PICTURES_FOLDER);
	
	public static boolean isAlbumImagesFolderAvailable() {
		return albumPicturesFolder.exists();
	}
}

package org.sammelbox.android.controller;

import android.os.Environment;

public class FileSystemLocations {
	public static final String SAMMELBOX_HOME = Environment.getExternalStorageDirectory() + "/sammelbox/";
	public static final String SAVED_SEARCHES_XML_FILE = SAMMELBOX_HOME + "app-data/saved-searches.xml";
}

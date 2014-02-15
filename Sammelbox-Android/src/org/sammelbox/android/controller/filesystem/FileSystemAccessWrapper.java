package org.sammelbox.android.controller.filesystem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.sammelbox.android.AppParams;

import android.util.Log;

public class FileSystemAccessWrapper {
	private static final int ONE_KB_BUFFER_SIZE = 1024;

	/**
	 * Unzips a file to the specified location, recreating the original file structure within it
	 * @param zipLocation The path to the future location of the zip file.
	 * @param folderLocation The path to the source directory.
	 */
	public static void unzipFileToFolder(String zipLocation, String folderLocation) {		
		try {
			ZipFile zipFile = new ZipFile(new File(zipLocation), ZipFile.OPEN_READ);

			Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();

			while (zipFileEntries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

				if (!entry.isDirectory()) {
					File destFile = new File(folderLocation, entry.getName());
					File destinationParent = destFile.getParentFile();

					destinationParent.mkdirs();

					FileOutputStream fileOutputStream = new FileOutputStream(destFile);
					InputStream inputStream = zipFile.getInputStream(entry);

					// Copy the bits from inputStream to fileOutputStream
					byte[] buf = new byte[ONE_KB_BUFFER_SIZE];
					int len;

					while ((len = inputStream.read(buf)) > 0) {
						fileOutputStream.write(buf, 0, len);
					}

					inputStream.close();
					fileOutputStream.close();
				}
			}

			zipFile.close();
		} catch (IOException ioe) {
			Log.e(AppParams.LOG_TAG, "An error occurred while unzipping " +
					"[" + folderLocation + "] to [" + zipLocation + "] ", ioe);
		}

	}
	
	/** Reads the specified file into a string
	 * @param filePath the path to the file that should be read
	 * @return the content of the specified file as a string or an empty string if the file does not exist */
	public static String readFileAsString(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return "";
		}

		byte[] buffer = new byte[(int) new File(filePath).length()];

		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
			bufferedInputStream.read(buffer);
			bufferedInputStream.close();

			return new String(buffer, Charset.defaultCharset());
		} catch (IOException ioe) {
			Log.e(AppParams.LOG_TAG, "An error occurred while reading [" + filePath + "]", ioe);
		}

		return new String(buffer, Charset.defaultCharset());
	}

	public static void deleteFile(String pathToFile) {
		if(!(new File(pathToFile)).delete()) {
			Log.e(AppParams.LOG_TAG, "Could not delete file: " + pathToFile);
		}		
	}
	
	public static boolean isSynchronized() {
		File sammelboxHome = new File(FileSystemLocations.SAMMELBOX_HOME);
		
		// if synchronized, there must be more than one file within the workspace
		// (One temporary database file might exist)
		return sammelboxHome.exists() && sammelboxHome.listFiles().length > 1; 
	}
}

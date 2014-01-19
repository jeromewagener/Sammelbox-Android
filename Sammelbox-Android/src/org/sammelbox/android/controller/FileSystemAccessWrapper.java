package org.sammelbox.android.controller;

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

					try {
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
					} catch (IOException ioe) {
						ioe.printStackTrace(); //TODO
					}
				}
			}

			zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace(); // TODO
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
		} catch (Exception e) {
			// TODO log or message
			//LOGGER.error("An error occured while reading the file (" + filePath + ") into a string", e);
		}

		return new String(buffer, Charset.defaultCharset());
	}
}

package org.sammelbox.android.controller.query;

public class DatabaseQueryHelper {
	static boolean isSpecialField(String fieldName) {
		return fieldName.equals("id") || fieldName.equals("content_version") ||
				fieldName.equals("schema_version") || fieldName.equals("typeinfo");
	}
}

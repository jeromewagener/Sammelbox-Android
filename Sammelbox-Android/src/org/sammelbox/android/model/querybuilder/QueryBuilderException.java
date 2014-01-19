package org.sammelbox.android.model.querybuilder;

public class QueryBuilderException extends Exception {
	private static final long serialVersionUID = 8009279995814569681L;

	public QueryBuilderException(String message) {
		super(message);
	}

	public QueryBuilderException(String message, Throwable cause) {
		super(message, cause);
	}
}

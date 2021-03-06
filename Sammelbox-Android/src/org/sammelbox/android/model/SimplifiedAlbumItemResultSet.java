package org.sammelbox.android.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.drawable.Drawable;

/** A simplified album item container. Each album item has at most one primary drawable, 
 * and all data (all fields) are compiled into a single string */
public class SimplifiedAlbumItemResultSet {
	/** For each album item, the id is stored in parallel to its primary image and its data */
	private List<Long> itemIDs = new ArrayList<Long>();
	
	/** For each album item, all data is compiled into a single string */
	private List<String> data = new ArrayList<String>();

	/** For each album item, a single drawable is used as the "primary" image */
	private List<Drawable> images = new ArrayList<Drawable>();

	public void clear() {
		data.clear();
		images.clear();
	}

	/** Each album item has at most one primary drawable, 
	 * and all data (all fields) must be compiled into a single string */
	public void addSimplifiedAlbumItem(Long itemID, Drawable image, String data) {
		this.itemIDs.add(itemID);
		this.images.add(image);
		this.data.add(data);
	}

	public Long[] getResultSetItemIDsAsArray() {
		return Arrays.copyOf(itemIDs.toArray(), itemIDs.toArray().length, Long[].class);
	}
	
	public String[] getResultSetDataAsStringArray() {
		return Arrays.copyOf(data.toArray(), data.toArray().length, String[].class);
	}

	public Drawable[] getResultSetImagesAsDrawableArray() {
		return Arrays.copyOf(images.toArray(), images.toArray().length, Drawable[].class);
	}
}

package org.sammelbox.android.view.adapter;

import org.sammelbox.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** A list container adapter to show album items */
public class AlbumItemListAdapter extends ArrayAdapter<String>{
	private final Activity context;
	private final Long[] itemIDs;
	private final Drawable[] images;
	private final String[] data;
	
	public AlbumItemListAdapter(Activity context, Long[] itemIDs, Drawable[] images, String[] data) {
		super(context, R.layout.album_list_item, data);
		this.context = context;
		this.itemIDs = itemIDs;
		this.data = data;
		this.images = images;

	}
	
	public Long getIDByPosition(int position) {
		return itemIDs[position];
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.album_list_item, null, true);
		TextView albumItemData = (TextView) rowView.findViewById(R.id.albumItemData);
		albumItemData.setText(Html.fromHtml(data[position]));
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.mainAlbumItemPicture);
		imageView.setImageDrawable(images[position]);
		return rowView;
	}
}

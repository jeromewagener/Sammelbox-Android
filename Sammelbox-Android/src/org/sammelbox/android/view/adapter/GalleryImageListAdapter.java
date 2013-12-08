package org.sammelbox.android.view.adapter;

import org.sammelbox.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class GalleryImageListAdapter extends ArrayAdapter<Drawable>{
	private final Activity context;
	private final Drawable[] images;
	
	public GalleryImageListAdapter(Activity context, Drawable[] images) {
		super(context, R.layout.gallery_list_item, images);
		
		this.context = context;
		this.images = images;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.gallery_list_item, null, true);
		
		ImageView galleryImage = (ImageView) rowView.findViewById(R.id.imgGalleryPicture);
		galleryImage.setImageDrawable(images[position]);
		
		return rowView;
	}
}

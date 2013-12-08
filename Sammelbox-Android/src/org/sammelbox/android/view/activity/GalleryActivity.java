package org.sammelbox.android.view.activity;

import java.util.ArrayList;
import java.util.Arrays;

import org.sammelbox.R;
import org.sammelbox.android.GlobalState;
import org.sammelbox.android.controller.DatabaseQueryOperation;
import org.sammelbox.android.view.adapter.GalleryImageListAdapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class GalleryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		String selectedAlbum = GlobalState.getSelectedAlbum();
		String selectedAlbumTable = GlobalState.getAlbumNameToTableName(this).get(selectedAlbum);
		
		ArrayList<Drawable> galleryImages = DatabaseQueryOperation.getImages(this, selectedAlbumTable, GlobalState.getSelectedAlbumItemID());
		GalleryImageListAdapter galleryImageList = new GalleryImageListAdapter(
				this, Arrays.copyOf(galleryImages.toArray(), galleryImages.toArray().length, Drawable[].class));
		
		ListView imageGallery = (ListView)findViewById(R.id.listAlbumItemImages);
		imageGallery.setAdapter(galleryImageList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}

}

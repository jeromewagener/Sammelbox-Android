package org.sammelbox.android.view.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sammelbox.R;
import org.sammelbox.android.controller.AppState;
import org.sammelbox.android.controller.filesystem.FileSystemLocations;
import org.sammelbox.android.controller.query.DatabaseQueryOperation;
import org.sammelbox.android.view.adapter.GalleryImageListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class GalleryActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		String selectedAlbum = AppState.getSelectedAlbum();
		String selectedAlbumTable = AppState.getAlbumNameToTableName(this).get(selectedAlbum);
		
		final ArrayList<Drawable> galleryImages = DatabaseQueryOperation.getImages(this, selectedAlbumTable, AppState.getSelectedAlbumItemID());
		GalleryImageListAdapter galleryImageList = new GalleryImageListAdapter(
				this, Arrays.copyOf(galleryImages.toArray(), galleryImages.toArray().length, Drawable[].class));
		
		ListView imageGallery = (ListView)findViewById(R.id.listAlbumItemImages);
		imageGallery.setAdapter(galleryImageList);
		
		if (!FileSystemLocations.isAlbumImagesFolderAvailable()) {
			TextView lblClickHint = (TextView) findViewById(R.id.lblClickHint);
			lblClickHint.setVisibility(View.VISIBLE);
			lblClickHint.setText(getResources().getString(R.string.album_pictures_not_available));
		} else {
			imageGallery.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {				
					List<String> pathsToFullImages = DatabaseQueryOperation.getPathsToFullImages(
							GalleryActivity.this, AppState.getSelectedAlbum(), 
							AppState.getAlbumNameToTableName(GalleryActivity.this).get(
									AppState.getSelectedAlbum()), AppState.getSelectedAlbumItemID());
					
					ImageViewerActivity.setPathToImage(pathsToFullImages.get(position));
					
					Intent openImageViewer = new Intent(GalleryActivity.this, ImageViewerActivity.class);
	                startActivity(openImageViewer);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}
}

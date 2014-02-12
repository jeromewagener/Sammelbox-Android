package org.sammelbox.android.view.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sammelbox.R;
import org.sammelbox.android.GlobalState;
import org.sammelbox.android.controller.DatabaseQueryOperation;
import org.sammelbox.android.view.adapter.GalleryImageListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class GalleryActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		String selectedAlbum = GlobalState.getSelectedAlbum();
		String selectedAlbumTable = GlobalState.getAlbumNameToTableName(this).get(selectedAlbum);
		
		final ArrayList<Drawable> galleryImages = DatabaseQueryOperation.getImages(this, selectedAlbumTable, GlobalState.getSelectedAlbumItemID());
		GalleryImageListAdapter galleryImageList = new GalleryImageListAdapter(
				this, Arrays.copyOf(galleryImages.toArray(), galleryImages.toArray().length, Drawable[].class));
		
		ListView imageGallery = (ListView)findViewById(R.id.listAlbumItemImages);
		imageGallery.setAdapter(galleryImageList);
		imageGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO to be refactored
				if (!(new File(Environment.getExternalStorageDirectory() + "/Sammelbox/album-pictures/").exists())) {
					// TODO i18n
					Toast.makeText(GalleryActivity.this, "Full images have not been synchronized", Toast.LENGTH_LONG).show();
					return;
				}
				
				List<String> pathsToFullImages = DatabaseQueryOperation.getPathsToFullImages(
						GalleryActivity.this, GlobalState.getSelectedAlbum(), 
						GlobalState.getAlbumNameToTableName(GalleryActivity.this).get(GlobalState.getSelectedAlbum()), GlobalState.getSelectedAlbumItemID());
				
				ImageViewerActivity.setPathToImage(pathsToFullImages.get(position));
				
				Intent openImageViewer = new Intent(GalleryActivity.this, ImageViewerActivity.class);
                startActivity(openImageViewer);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}
}

package org.sammelbox.android.view.activity;

import org.sammelbox.R;
import org.sammelbox.android.controller.AppState;
import org.sammelbox.android.model.SimplifiedAlbumItemResultSet;
import org.sammelbox.android.view.adapter.AlbumItemListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumItemBrowserActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_item_browser);
		
		TextView heading = (TextView)findViewById(R.id.lblAlbumItemBrowserHeading);
		heading.setText(AppState.getSelectedAlbum());
		
		SimplifiedAlbumItemResultSet simplifiedAlbumItemResultSet = AppState.getSimplifiedAlbumItemResultSet();
		
		AlbumItemListAdapter adapter = new AlbumItemListAdapter(
				AlbumItemBrowserActivity.this,
				simplifiedAlbumItemResultSet.getResultSetItemIDsAsArray(),
				simplifiedAlbumItemResultSet.getResultSetImagesAsDrawableArray(),
				simplifiedAlbumItemResultSet.getResultSetDataAsStringArray());
		
		ListView albumItems = (ListView)findViewById(R.id.albumItems);
		albumItems.setAdapter(adapter);
		albumItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AppState.setSelectedAlbumItemID(((AlbumItemListAdapter) parent.getAdapter()).getIDByPosition(position));
				
				Intent openImageGallery = new Intent(AlbumItemBrowserActivity.this, GalleryActivity.class);
                startActivity(openImageGallery);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album_item_browse, menu);
		return true;
	}

}

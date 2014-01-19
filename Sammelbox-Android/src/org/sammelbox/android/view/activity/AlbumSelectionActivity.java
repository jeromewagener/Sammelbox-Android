package org.sammelbox.android.view.activity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sammelbox.R;
import org.sammelbox.android.GlobalState;
import org.sammelbox.android.controller.DatabaseQueryOperation;
import org.sammelbox.android.controller.DatabaseWrapper;
import org.sammelbox.android.controller.managers.SavedSearchManager;
import org.sammelbox.android.controller.managers.SavedSearchManager.SavedSearch;
import org.sammelbox.android.model.querybuilder.QueryBuilderException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AlbumSelectionActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_selection);
		
		final Map<String,String> albumNameToTableName = GlobalState.getAlbumNameToTableName(this);
		final String[] albumNames = Arrays.copyOf(albumNameToTableName.keySet().toArray(), albumNameToTableName.keySet().toArray().length, String[].class);
			
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, albumNames);
		ListView albumList = (ListView)findViewById(R.id.listAlbums);
		albumList.setAdapter(adapter);
		
		albumList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				GlobalState.setSelectedAlbum(albumNames[position]);
				GlobalState.setSimplifiedAlbumItemResultSet(DatabaseQueryOperation.getAllAlbumItemsFromAlbum(AlbumSelectionActivity.this));
				
				Intent openAlbumItemListToBrowse = new Intent(AlbumSelectionActivity.this, AlbumItemBrowserActivity.class);
                startActivity(openAlbumItemListToBrowse);
            }
        });
		
		SavedSearchManager.initialize(this);
		final List<String> savedSearchesNames = SavedSearchManager.getSavedSearchesNames();
		String[] savedSearchesNamesArray = new String[savedSearchesNames.size()];
	    savedSearchesNamesArray = savedSearchesNames.toArray(savedSearchesNamesArray);
	    
	    ArrayAdapter<String> savedSearchesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, savedSearchesNamesArray);
		ListView savedSearchesList = (ListView)findViewById(R.id.listSavedSearches);
		savedSearchesList.setAdapter(savedSearchesAdapter);
		
		savedSearchesList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {				
				SavedSearch savedSearch = SavedSearchManager.getSavedSearchByName(savedSearchesNames.get(position));
				GlobalState.setSelectedAlbum(savedSearch.getAlbum());
								
				try {
					GlobalState.setSimplifiedAlbumItemResultSet(
							DatabaseQueryOperation.getAlbumItems(AlbumSelectionActivity.this, 
									DatabaseWrapper.executeRawSQLQuery(
											DatabaseWrapper.getSQLiteDatabaseInstance(
													AlbumSelectionActivity.this),
													savedSearch.getSQLQueryString(AlbumSelectionActivity.this))));
				} catch (QueryBuilderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent openAlbumItemListToBrowse = new Intent(AlbumSelectionActivity.this, AlbumItemBrowserActivity.class);
                startActivity(openAlbumItemListToBrowse);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album_selection, menu);
		return true;
	}

}

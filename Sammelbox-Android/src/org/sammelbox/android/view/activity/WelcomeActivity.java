package org.sammelbox.android.view.activity;

import java.io.File;

import org.sammelbox.R;
import org.sammelbox.android.controller.DatabaseWrapper;
import org.sammelbox.android.controller.filesystem.FileSystemAccessWrapper;
import org.sammelbox.android.controller.filesystem.FileSystemLocations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        // Initialize folder structure
        File sammelboxHome = new File(FileSystemLocations.SAMMELBOX_HOME);
        if (!sammelboxHome.exists()) {        	
        	sammelboxHome.mkdir();
        }
        
        // Browse album items and searches
        ImageButton btnOpenBrowseAlbumsAndSearches = (ImageButton) findViewById(R.id.btnOpenBrowseAlbumsAndSearches);
        btnOpenBrowseAlbumsAndSearches.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (FileSystemAccessWrapper.isSynchronized()) {            	
            		Intent openAlbumSelection = new Intent(WelcomeActivity.this, AlbumSelectionActivity.class);
                	startActivity(openAlbumSelection);
            	} else {
            		Toast.makeText(WelcomeActivity.this, 
            				getResources().getString(R.string.synchronize_first), Toast.LENGTH_LONG).show();
            	}
            }
        });
        
        // Search for particular items
        ImageButton btnOpenSearchForAlbumItems = (ImageButton) findViewById(R.id.btnOpenSearchForAlbumItems);
        btnOpenSearchForAlbumItems.setOnClickListener(new OnClickListener() {
        	@Override
            public void onClick(View v) {
	        	if (FileSystemAccessWrapper.isSynchronized()) {            	
	            	Intent openSearchForAlbumItems = new Intent(WelcomeActivity.this, SearchActivity.class);
	                startActivity(openSearchForAlbumItems);
	        	} else {
	        		Toast.makeText(WelcomeActivity.this, 
	        				getResources().getString(R.string.synchronize_first), Toast.LENGTH_LONG).show();
	        	}
        	}
        });
        
        // Open the synchronization
        ImageButton btnOpenSynchronization = (ImageButton) findViewById(R.id.btnOpenSynchronization);
        btnOpenSynchronization.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSynchronization = new Intent(WelcomeActivity.this, SynchronizationActivity.class);
                startActivity(openSynchronization);
            }
        });
        
        // Open the help
        ImageButton btnOpenHelp = (ImageButton) findViewById(R.id.btnOpenHelp);
        btnOpenHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHelp = new Intent(WelcomeActivity.this, HelpActivity.class);
                startActivity(openHelp);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        DatabaseWrapper.closeDBConnection(DatabaseWrapper.getSQLiteDatabaseInstance(WelcomeActivity.this));
    }
}

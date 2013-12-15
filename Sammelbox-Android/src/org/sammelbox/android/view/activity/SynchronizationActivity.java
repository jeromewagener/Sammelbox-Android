package org.sammelbox.android.view.activity;

import org.sammelbox.R;
import org.sammelbox.android.controller.sync.SyncServiceClient;
import org.sammelbox.android.controller.sync.SynchronizationMessageHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SynchronizationActivity extends Activity {

	private SyncServiceClient syncServiceClient = SyncServiceClient.Default.getDefaultInstance();
	private SynchronizationMessageHandler synchronizationMessageHandler = new SynchronizationMessageHandler(this, syncServiceClient);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchronization);
		
		// TODO apparently networking is done on the main thread. This is weird since
		// Soutils wraps everything in its own thread. This needs to be investigated.
		// However, for the meantime, the following avoids the problem (NetworkOnMainThreadException 
		// on newer versions of Android)
		// See http://developer.android.com/reference/android/os/NetworkOnMainThreadException.html
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

		
		final ImageButton syncButton = (ImageButton) findViewById(R.id.btnSynchronize);
		final Button cancelSyncButton = (Button) findViewById(R.id.btnCancelSynchronization);
		
		syncButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				syncButton.setEnabled(false);
				cancelSyncButton.setEnabled(true);
				
				syncServiceClient.startListeningForHashedSyncCodeBeacons(synchronizationMessageHandler);
				
				TextView syncInstructions = (TextView) findViewById(R.id.lblSynchronizationInstructions);
				syncInstructions.setText(R.string.search_for_sync_partner);
			}
		});
		
		cancelSyncButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				syncButton.setEnabled(true);
				cancelSyncButton.setEnabled(false);
				
				syncServiceClient.stopListeningForHashedSyncCodeBeacons();
				syncServiceClient.stopCommunicationChannel();
				syncServiceClient.stopFileTransferClient();
				
				TextView syncInstructions = (TextView) findViewById(R.id.lblSynchronizationInstructions);
				syncInstructions.setText(R.string.click_to_sync);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.synchronization, menu);
		return true;
	}

}

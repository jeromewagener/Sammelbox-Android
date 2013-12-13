package org.sammelbox.android.view.activity;

import java.lang.ref.WeakReference;

import org.sammelbox.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jeromewagener.soutils.android.beaconing.BeaconReceiver;

public class SynchronizationActivity extends Activity {
	private BeaconReceiver beaconReceiver = null;
	private final MessageHandler messageHandler = new MessageHandler(this);
	private static boolean identificationInputBoxShown = false;
	
	private static class MessageHandler extends Handler {
		private final WeakReference<SynchronizationActivity> synchronizationActivity;
		
		public MessageHandler(SynchronizationActivity activity) {
			synchronizationActivity = new WeakReference<SynchronizationActivity>(activity);
		}

		@Override
		public void handleMessage(Message message) {
			SynchronizationActivity activity = synchronizationActivity.get();
			
			if (activity != null) {
				String senderIpAddress = message.getData().getString("senderIpAddress");
				String receivedMessage = message.getData().getString("receivedMessage");
				
				Toast.makeText(synchronizationActivity.get(), senderIpAddress + " : " + receivedMessage, Toast.LENGTH_SHORT).show();
				
				if (!identificationInputBoxShown && receivedMessage.startsWith("<sammelbox id_beacon=") &&
						receivedMessage.endsWith("</sammelbox>")) {
					identificationInputBoxShown = true;
					
					AlertDialog.Builder alert = new AlertDialog.Builder(synchronizationActivity.get());

					alert.setTitle("Enter Synchronization Code");
					alert.setMessage("Please enter the synchronization code shown by Sammelbox on your computer");

					// Set an EditText view to get user input 
					final EditText input = new EditText(synchronizationActivity.get());
					alert.setView(input);

					alert.setPositiveButton("Code entered", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = input.getText().toString();
							// Do something with value!
						}
					});

					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							final Button syncButton = (Button) synchronizationActivity.get().findViewById(R.id.btnSynchronize);
							syncButton.setText(R.string.synchronize_button);
							syncButton.setEnabled(true);
						}
					});

					alert.show();
				}
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchronization);
		
		final Button syncButton = (Button) findViewById(R.id.btnSynchronize);
		syncButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				beaconReceiver = new BeaconReceiver(messageHandler);
				beaconReceiver.start();
				
				identificationInputBoxShown = false;
				syncButton.setText("Synchronizing...");
				syncButton.setEnabled(false);
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

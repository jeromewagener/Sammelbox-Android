package org.sammelbox.android.controller.sync;

import java.lang.ref.WeakReference;

import org.sammelbox.R;
import org.sammelbox.android.controller.FileSystemAccessWrapper;
import org.sammelbox.android.controller.FileSystemLocations;
import org.sammelbox.android.view.activity.SynchronizationActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jeromewagener.soutils.Utilities;

public class SynchronizationMessageHandler extends Handler {
	private final WeakReference<SynchronizationActivity> synchronizationActivity;
	private static boolean identificationInputBoxShown = false;
	private static SyncServiceClient syncServiceClient;
	
	public SynchronizationMessageHandler(SynchronizationActivity activity, SyncServiceClient syncServiceClient) {
		synchronizationActivity = new WeakReference<SynchronizationActivity>(activity);
		SynchronizationMessageHandler.syncServiceClient = syncServiceClient;
	}
	
	@Override
	public void handleMessage(Message message) {
		SynchronizationActivity activity = synchronizationActivity.get();
		
		if (activity != null) {
			final String senderIpAddress = message.getData().getString("senderIpAddress");
			final String receivedMessage = message.getData().getString("receivedMessage");
							
			if (!identificationInputBoxShown && receivedMessage.startsWith("sammelbox-desktop:sync-code:")) {
				identificationInputBoxShown = true;
				showSyncCodeInputDialogAndConnectIfCodeIsValid(senderIpAddress, receivedMessage);
				
			} else if (receivedMessage.equals("sammelbox-desktop:accept-transfer")) {				
				syncServiceClient.openFileTransferClient(FileSystemLocations.SAMMELBOX_HOME + "sync.zip", senderIpAddress);
				TextView syncInstructions = (TextView) synchronizationActivity.get().findViewById(R.id.lblSynchronizationInstructions);
				syncInstructions.setText(R.string.transferring_data);
				
			} else if (receivedMessage.equals("sammelbox-desktop:transfer-finished")) {
				FileSystemAccessWrapper.unzipFileToFolder(FileSystemLocations.SAMMELBOX_HOME + "sync.zip", FileSystemLocations.SAMMELBOX_HOME);
				
				TextView syncInstructions = (TextView) synchronizationActivity.get().findViewById(R.id.lblSynchronizationInstructions);
				syncInstructions.setText(R.string.sync_successful);
				
				syncServiceClient.stopCommunicationChannel();
				
				while (syncServiceClient.getFileTransferProgressPercentage() != 1) { // TODO nullify file transfer client
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO avoid busy waiting. log exception
						e.printStackTrace();
					}
					
					syncServiceClient.stopFileTransferClient();
				}
				
				final Button cancelSyncButton = (Button) synchronizationActivity.get().findViewById(R.id.btnCancelSynchronization);
				cancelSyncButton.setEnabled(false);
			}
		}
	}
	
	private void showSyncCodeInputDialogAndConnectIfCodeIsValid(final String senderIpAddress, final String receivedMessage) {
		AlertDialog.Builder syncCodeInputAlertDialog = new AlertDialog.Builder(synchronizationActivity.get());

		syncCodeInputAlertDialog.setTitle(R.string.enter_code_dialog_title);
		syncCodeInputAlertDialog.setMessage(R.string.enter_code_dialog_message);
		
		final EditText syncCodeInputText = new EditText(synchronizationActivity.get());
		syncCodeInputText.setInputType(InputType.TYPE_CLASS_NUMBER);
		syncCodeInputAlertDialog.setView(syncCodeInputText);

		syncCodeInputAlertDialog.setPositiveButton(R.string.code_entered, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = syncCodeInputText.getText().toString();
				
				if (Utilities.stringToMD5(value).equals(receivedMessage.split(":")[2])) {
					syncServiceClient.stopListeningForHashedSyncCodeBeacons();
					syncServiceClient.startCommunicationChannel(senderIpAddress, SynchronizationMessageHandler.this);
					syncServiceClient.sendMessage("sammelbox-android:connect:" + value);
				} else {
					Toast.makeText(synchronizationActivity.get(), R.string.wrong_code_entered_abort, Toast.LENGTH_LONG).show();
					syncServiceClient.stopListeningForHashedSyncCodeBeacons();
					resetButtons();							
				}
			}
		});

		syncCodeInputAlertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				syncServiceClient.stopListeningForHashedSyncCodeBeacons();
				resetButtons();
			}
		});

		syncCodeInputAlertDialog.show();
	}
	
	private void resetButtons() {
		final Button cancelSyncButton = (Button) synchronizationActivity.get().findViewById(R.id.btnCancelSynchronization);
		cancelSyncButton.setEnabled(false);
		final ImageButton syncButton = (ImageButton) synchronizationActivity.get().findViewById(R.id.btnSynchronize);
		syncButton.setEnabled(true);
	}
}
package org.sammelbox.android.controller.sync;

import java.security.NoSuchAlgorithmException;

import org.sammelbox.R;
import org.sammelbox.android.AppParams;
import org.sammelbox.android.controller.filesystem.FileSystemAccessWrapper;
import org.sammelbox.android.controller.filesystem.FileSystemLocations;
import org.sammelbox.android.view.activity.SynchronizationActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jeromewagener.soutils.messaging.SoutilsMessage;
import com.jeromewagener.soutils.messaging.SoutilsObserver;
import com.jeromewagener.soutils.utilities.Soutilities;

public class SyncObserver implements SoutilsObserver {
	private static final long UPDATE_INTERVAL_IN_MS = 200;
	
	private SynchronizationActivity synchronizationActivity;
	private SyncServiceClient syncServiceClient;
	private boolean identificationInputBoxShown = false;
	
	public SyncObserver(SyncServiceClient syncServiceClient, SynchronizationActivity synchronizationActivity) {
		this.syncServiceClient = syncServiceClient;
		this.synchronizationActivity = synchronizationActivity;
	}

	public void handleSoutilsMessage(SoutilsMessage soutilsMessage) {
		final String messageContent = soutilsMessage.getContent();
		final String senderAddress = soutilsMessage.getSenderAddress();
		
		Log.i(AppParams.LOG_TAG, 
			  soutilsMessage.getMessageType() + ":" + soutilsMessage.getContent() + ":" + soutilsMessage.getSenderAddress(), 
			  soutilsMessage.getThrowable());
		
		if (!identificationInputBoxShown && messageContent.startsWith("sammelbox-desktop:sync-code:")) {
			identificationInputBoxShown = true;
			
			synchronizationActivity.runOnUiThread(new Runnable(){
			    @Override
			    public void run() {
			    	showSyncCodeInputDialogAndConnectIfCodeIsValid(senderAddress, messageContent);
			    }
			});

		} else if (messageContent.startsWith("sammelbox-desktop:accept-transfer:")) {
			long numberOfBytesToBeTransferred = Long.valueOf(messageContent.split(":")[2]);
			syncServiceClient.openFileTransferClient(
					FileSystemLocations.SAMMELBOX_HOME + "sync.zip", senderAddress, this, numberOfBytesToBeTransferred);
			
			synchronizationActivity.runOnUiThread(new Runnable(){
			    @Override
			    public void run() {
			    	TextView syncInstructions = (TextView) synchronizationActivity.findViewById(R.id.lblSynchronizationInstructions);
					syncInstructions.setText(R.string.transferring_data);
					
					ProgressBar transferProgress = (ProgressBar)synchronizationActivity.findViewById(R.id.pbTransferProgress);
					transferProgress.setVisibility(View.VISIBLE);
			    }
			});
			
			while (!syncServiceClient.isFileTransferFinished()) {
				try {
					Thread.sleep(UPDATE_INTERVAL_IN_MS);
					
					synchronizationActivity.runOnUiThread(new Runnable(){
					    @Override
					    public void run() {
					    		ProgressBar transferProgress = (ProgressBar)synchronizationActivity.findViewById(R.id.pbTransferProgress);
					    		transferProgress.setProgress(syncServiceClient.getFileTransferProgressPercentage());
							}
						});
				} catch (InterruptedException interruptedException) {
					Log.e(AppParams.LOG_TAG, "An error occured while updating the progress bar", interruptedException);
				}
			}

		} else if (messageContent.equals("sammelbox-desktop:transfer-finished")) {
			String zipFile = FileSystemLocations.SAMMELBOX_HOME + "sync.zip";
			FileSystemAccessWrapper.unzipFileToFolder(zipFile, FileSystemLocations.SAMMELBOX_HOME);
			FileSystemAccessWrapper.deleteFile(zipFile);
			
			synchronizationActivity.runOnUiThread(new Runnable(){
			    @Override
			    public void run() {
			    	TextView syncInstructions = (TextView) synchronizationActivity.findViewById(R.id.lblSynchronizationInstructions);
					syncInstructions.setText(R.string.sync_successful);
					
					ProgressBar transferProgress = (ProgressBar)synchronizationActivity.findViewById(R.id.pbTransferProgress);
					transferProgress.setVisibility(View.GONE);
			    }
			});
			
			syncServiceClient.stopCommunicationChannel();
			while (!syncServiceClient.isFileTransferFinished()) {
				try {
					Thread.sleep(UPDATE_INTERVAL_IN_MS);
				} catch (InterruptedException interruptedException) {
					Log.e(AppParams.LOG_TAG, "An error occured while updating the progress bar", interruptedException);
				}
			}
			
			syncServiceClient.stopFileTransferClient();
			synchronizationActivity.runOnUiThread(new Runnable(){
			    @Override
			    public void run() {
			    	final Button cancelSyncButton = (Button) synchronizationActivity.findViewById(R.id.btnCancelSynchronization);
					cancelSyncButton.setEnabled(false);
			    }
			});
		}
	}
	
	private void showSyncCodeInputDialogAndConnectIfCodeIsValid(final String senderIpAddress, final String receivedMessage) {
		AlertDialog.Builder syncCodeInputAlertDialog = new AlertDialog.Builder(synchronizationActivity);

		syncCodeInputAlertDialog.setTitle(R.string.enter_code_dialog_title);
		syncCodeInputAlertDialog.setMessage(R.string.enter_code_dialog_message);

		final EditText syncCodeInputText = new EditText(synchronizationActivity);
		syncCodeInputText.setInputType(InputType.TYPE_CLASS_NUMBER);
		syncCodeInputAlertDialog.setView(syncCodeInputText);

		syncCodeInputAlertDialog.setPositiveButton(R.string.code_entered, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = syncCodeInputText.getText().toString();

				String md5HashedSyncCode = "";
				try {
					md5HashedSyncCode = Soutilities.stringToMD5(value);
				} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
					Log.e(AppParams.LOG_TAG, "Cannot compute hash", noSuchAlgorithmException);
				}
				
				if (md5HashedSyncCode.equals(receivedMessage.split(":")[2])) {
					syncServiceClient.stopListeningForHashedSyncCodeBeacons();
					syncServiceClient.startCommunicationChannel(senderIpAddress, SyncObserver.this);
					syncServiceClient.sendMessage("sammelbox-android:connect:" + value);
				} else {
					Toast.makeText(synchronizationActivity, R.string.wrong_code_entered_abort, Toast.LENGTH_LONG).show();
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
		final Button cancelSyncButton = (Button) synchronizationActivity.findViewById(R.id.btnCancelSynchronization);
		cancelSyncButton.setEnabled(false);
		final ImageButton syncButton = (ImageButton) synchronizationActivity.findViewById(R.id.btnSynchronize);
		syncButton.setEnabled(true);
	}
}
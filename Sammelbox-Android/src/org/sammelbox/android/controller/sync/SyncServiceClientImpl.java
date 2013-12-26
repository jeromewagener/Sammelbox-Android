package org.sammelbox.android.controller.sync;

import org.sammelbox.android.controller.GlobalParameters;

import com.jeromewagener.soutils.beaconing.BeaconReceiver;
import com.jeromewagener.soutils.communication.Communication;
import com.jeromewagener.soutils.filetransfer.FileTransferClient;
import com.jeromewagener.soutils.messaging.SoutilsObserver;

public class SyncServiceClientImpl implements SyncServiceClient {
	private BeaconReceiver beaconReceiver = null;
	private FileTransferClient fileTransferClient = null;
	private Communication communication = null;
	
	@Override
	public void startListeningForHashedSyncCodeBeacons(SoutilsObserver soutilsObserver) {
		if (beaconReceiver == null) {
			beaconReceiver = new BeaconReceiver(GlobalParameters.BEACON_PORT, soutilsObserver);
			beaconReceiver.start();
		}
	}
	
	@Override
	public void stopListeningForHashedSyncCodeBeacons() {
		if (beaconReceiver != null) {
			beaconReceiver.done();
			beaconReceiver = null;
		}
	}

	@Override
	public void startCommunicationChannel(String hostIpAddress, SoutilsObserver soutilsObserver) {
		if (communication == null) {
			communication = new Communication(hostIpAddress, GlobalParameters.COMMUNICATION_PORT, soutilsObserver);
			communication.start();
		}
	}

	@Override
	public void stopCommunicationChannel() {
		if (communication != null) {
			communication.done();
			communication = null;
		}
	}
	
	@Override
	public void sendMessage(String message) {
		if (communication != null) {
			communication.sendMessage(message);
		}
	}

	@Override
	public void openFileTransferClient(
			String storageLocationAsAbsolutPath, String ipAddress, SoutilsObserver soutilsObserver, long numberOfBytesToBeTransferred) {
		if (fileTransferClient == null) {
			fileTransferClient = new FileTransferClient(
					storageLocationAsAbsolutPath, ipAddress, GlobalParameters.FILE_TRANSFER_PORT, soutilsObserver, numberOfBytesToBeTransferred);
			fileTransferClient.start();
		}
	}
	
	@Override
	public int getFileTransferProgressPercentage() {		
		return fileTransferClient.getFileTransferPercentage();
	}
	
	@Override
	public boolean isFileTransferFinished() {
		return fileTransferClient == null ? true : fileTransferClient.isDone();
	}
	
	@Override
	public void stopFileTransferClient() {
		if (fileTransferClient != null) {
			fileTransferClient.setDone(true);
			fileTransferClient = null;
		}
	}
}

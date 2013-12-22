package org.sammelbox.android.controller.sync;

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
			beaconReceiver = new BeaconReceiver(5454, soutilsObserver); // TODO define port
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
			communication = new Communication(hostIpAddress, 12345, soutilsObserver); // TODO define port
			communication.start();
		} else {
			//TODO
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
	public void openFileTransferClient(String storageLocationAsAbsolutPath, String ipAddress, SoutilsObserver soutilsObserver, long numberOfBytesToBeTransferred) {
		if (fileTransferClient == null) {
			fileTransferClient = new FileTransferClient(storageLocationAsAbsolutPath, ipAddress, 6565, soutilsObserver, numberOfBytesToBeTransferred); // TODO define port
			fileTransferClient.start();
		} else {
			// TODO
		}
	}
	
	@Override
	public int getFileTransferProgressPercentage() {
		if (fileTransferClient == null) {
			return -1;
		}
		
		return fileTransferClient.getFileTransferPercentage();
	}
	
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

package org.sammelbox.android.controller.sync;

import com.jeromewagener.soutils.android.beaconing.BeaconReceiver;
import com.jeromewagener.soutils.android.networking.Communication;
import com.jeromewagener.soutils.filetransfer.FileTransferClient;

public class SyncServiceClientImpl implements SyncServiceClient {
	private BeaconReceiver beaconReceiver = null;
	private FileTransferClient fileTransferClient = null;
	private Communication communication = null;
	
	@Override
	public void startListeningForHashedSyncCodeBeacons(SynchronizationMessageHandler synchronizationMessageHandler) {
		if (beaconReceiver == null) {
			beaconReceiver = new BeaconReceiver(synchronizationMessageHandler);
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
	public void startCommunicationChannel(String hostIpAddress, SynchronizationMessageHandler handler) {
		if (communication == null) {
			communication = new Communication(hostIpAddress, 12345, handler); // TODO define port
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
	public void openFileTransferClient(String storageLocationAsAbsolutPath, String ipAddress) {
		if (fileTransferClient == null) {
			fileTransferClient = new FileTransferClient(storageLocationAsAbsolutPath, ipAddress);
			fileTransferClient.start();
		} else {
			// TODO
		}
	}
	
	@Override
	public int getFileTransferProgressPercentage() {
		return fileTransferClient.isDone() ? 1 : 0; // TODO return percentage
	}
	
	@Override
	public void stopFileTransferClient() {
		if (fileTransferClient != null) {
			fileTransferClient.setDone(true);
			fileTransferClient = null;
		}
	}
}

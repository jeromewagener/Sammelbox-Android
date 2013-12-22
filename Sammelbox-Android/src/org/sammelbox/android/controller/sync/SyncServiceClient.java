package org.sammelbox.android.controller.sync;

import com.jeromewagener.soutils.messaging.SoutilsObserver;


public interface SyncServiceClient {
	public void startListeningForHashedSyncCodeBeacons(SoutilsObserver soutilsObserver);
	public void stopListeningForHashedSyncCodeBeacons();
	public void startCommunicationChannel(String hostIpAddress, SoutilsObserver soutilsObserver);
	public void sendMessage(String message);
	public void stopCommunicationChannel();
	public void openFileTransferClient(
		String storageLocationAsAbsolutPath, String ipAddress, SoutilsObserver soutilsObserver, long numberOfBytesToBeTransferred);
	public int getFileTransferProgressPercentage();
	public boolean isFileTransferFinished();
	public void stopFileTransferClient();
	
	public static class Default {
		public static SyncServiceClient getDefaultInstance() {
			return new SyncServiceClientImpl();
		}
	}
}

package org.sammelbox.android.controller.sync;


public interface SyncServiceClient {
	public void startListeningForHashedSyncCodeBeacons(SynchronizationMessageHandler synchronizationMessageHandler);
	public void stopListeningForHashedSyncCodeBeacons();
	public void startCommunicationChannel(String hostIpAddress, SynchronizationMessageHandler handler);
	public void sendMessage(String message);
	public void stopCommunicationChannel();
	public void openFileTransferClient(String storageLocationAsAbsolutPath, String ipAddress);
	public int getFileTransferProgressPercentage();
	public void stopFileTransferClient();
	
	public static class Default {
		public static SyncServiceClient getDefaultInstance() {
			return new SyncServiceClientImpl();
		}
	}
}

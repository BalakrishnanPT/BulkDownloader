package balakrishnan.me.bulkdownloader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedHashMap;

public class MessagerHandler {

    /**
     * This method used to send current status of downloaded image
     *
     * @param messageID unique id
     * @param params    message to sent
     */
    public static void sendMessage(int messageID, @Nullable String params, @Nullable Bundle bundle) {
        String TAG = "sendMessage";
        Messenger mActivityMessenger = null;

        if (ImageDownloaderHelper.mHandler != null) {
            mActivityMessenger = new Messenger(ImageDownloaderHelper.mHandler);
        } else {
            //Logic for progressing the notification
        }
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.

        if (mActivityMessenger == null)
            return;

        Message m = Message.obtain();
        m.what = messageID;
        m.obj = params;
        m.setData(bundle);
        try {
            mActivityMessenger.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.");
        }

    }

    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return false;
        }
    });

    /**
     * This handler is used for getting Success / Failure message from Work Manager Downloading image
     */
    protected static class IncomingMessageHandler extends Handler {
        int total, success = 0, failure = 0, percentage;
        ImageDownloaderHelper.DownloadStatus downloadStatus;
        private String TAG = "IncomingMessageHandler";
        private LinkedHashMap<String, ProgressModel> trackRecord = new LinkedHashMap<>();

        public IncomingMessageHandler(int total, ImageDownloaderHelper.DownloadStatus status) {
            this.downloadStatus = status;
            this.total = total;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (msg.getData() != null) {
                        if (msg.getData().getBoolean("state", false)) {
                            success++;
                        } else {
                            failure++;
                        }
                        if (downloadStatus != null)
                            downloadStatus.DownloadedItems(total, ((success + failure) * 100) / total, success, failure);
                    }
                    break;
                case 2:
                    if (msg.getData() != null) {

                        trackRecord.put(msg.getData().getString("url"), new ProgressModel(msg.getData().getFloat("fileSize", (float) 0.0), msg.getData().getFloat("currentSize", (float) 0.0)));
                        if (downloadStatus != null) {
                            downloadStatus.CurrentDownloadPercentage(trackRecord);
                        }
                    }

            }
        }

    }

}

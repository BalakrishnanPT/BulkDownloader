package balakrishnan.me.downloader;

import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import balakrishnan.me.bulkdownloader.BaseApplication;

public class SubApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Receiver receiver = new Receiver();

        IntentFilter filter = new IntentFilter(BaseApplication.BULK_DOWNLOADER_NOTIFICATION);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
}

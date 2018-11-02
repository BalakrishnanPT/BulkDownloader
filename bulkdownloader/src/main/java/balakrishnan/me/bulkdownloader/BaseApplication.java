package balakrishnan.me.bulkdownloader;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
    private static Context context;
    public static String BULK_DOWNLOADER_NOTIFICATION = "bulk_downloader.notification";
    static LocalData localData;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        localData = new LocalData(getAppContext());
    }

    public static Context getAppContext() {
        return context;
    }

    public static LocalData getLocalData() {
        return localData;
    }
}

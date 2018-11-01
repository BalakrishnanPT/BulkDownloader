package balakrishnan.me.bulkdownloader;

import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class ImageDownloaderHelper {

    public static WorkManager mWorkManager = WorkManager.getInstance();
    public static MessagerHandler.IncomingMessageHandler mHandler;
    public static LocalData dbLocalData = new LocalData(BaseApplication.getAppContext());
    private static Constraints mConstraint = getConstraint();
    private static ArrayList<String> urls = null;
    private static String url = null;
    private static DownloadStatus downloadStatus = null;
    private static int collectionId = 0;
    private static Observer<WorkStatus> observe = null;

    public ImageDownloaderHelper setConstraint(Constraints mConstraint) {
        ImageDownloaderHelper.mConstraint = mConstraint;
        return this;
    }

    public ImageDownloaderHelper setUrls(ArrayList<String> urls) {
        ImageDownloaderHelper.urls = urls;
        return this;
    }

    public ImageDownloaderHelper setUrl(String url) {
        ImageDownloaderHelper.url = url;
        return this;
    }

    public ImageDownloaderHelper setDownloadStatus(DownloadStatus downloadStatus) {
        ImageDownloaderHelper.downloadStatus = downloadStatus;
        return this;
    }

    public ImageDownloaderHelper setCollectionId(int collectionId) {
        ImageDownloaderHelper.collectionId = collectionId;
        return this;
    }

    public ImageDownloaderHelper addObserver(Observer<WorkStatus> observe) {
        ImageDownloaderHelper.observe = observe;
        return this;
    }

    public static void createImageDownloadWorkURl() throws ImageDownloaderException {
        if (url == null)
            throw new ImageDownloaderException(ImageDownloadingException.NULL_LIST);
        new APICall(downloadStatus, url, collectionId).execute();
    }


    public static String create() throws ImageDownloaderException {

        if (urls == null) throw new ImageDownloaderException(ImageDownloadingException.NULL_LIST);

        if (collectionId == 0)
            throw new ImageDownloaderException(ImageDownloadingException.COLLECTION_ID_MISSING);

        /*This handler is reference is used in ImageDownloaderWorker*/
        mHandler = new MessagerHandler.IncomingMessageHandler(urls.size(), downloadStatus);

        OneTimeWorkRequest.Builder listDownloadBuilder =
                new OneTimeWorkRequest.Builder(ImageDownloaderWorker.class)
                        .setConstraints(mConstraint);
//         Convert the list into string and store in SharedPreference
        dbLocalData.setStringPreferenceValue("value_" + collectionId, gsonToString(urls));
//        Store size of collection in SharedPreference
        dbLocalData.setIntegerPreferenceValue("size_value_" + collectionId, urls.size());
//        Set input data -> Key for stored list :: value_ + collectionId
        listDownloadBuilder.setInputData(new Data.Builder().putStringArray("value", new String[]{"value_" + collectionId, collectionId + ""}).build());
        OneTimeWorkRequest work = listDownloadBuilder.build();
        mWorkManager.enqueue(work);
//        Store UUID for later use
        dbLocalData.setStringPreferenceValue("imageDownloadWork" + collectionId, work.getId().toString());
        getObserver(collectionId, new Observer<WorkStatus>() {
            @Override
            public void onChanged(@Nullable WorkStatus workStatus) {
                if (workStatus.getState().isFinished())
                    removeFromWork(collectionId);
            }
        });
        if (observe != null) getObserver(collectionId, observe);
        return work.getId().toString();
    }

    public static void removeFromWork(int collectionID) {
        dbLocalData.delete("imageDownloadWork" + collectionID);
        dbLocalData.delete("size_value_" + collectionID);
    }

    public static void getObserver(int collectionID, Observer<WorkStatus> observe) {
        UUID uuid = UUID.fromString(dbLocalData.getStringPreferenceValue("imageDownloadWork" + collectionID));
//        mWorkManager.getStatusById(uuid).;
//        mWorkManager.getStatusById(uuid).observe(ProcessLifecycleOwner.get(), observe);
    }

    public static boolean isWorkRunning(int collectionId) {
        return TextUtils.isEmpty(dbLocalData.getStringPreferenceValue("imageDownloadWork" + collectionId));
    }

    /**
     * A interface created to know about the Download status
     */
    public interface DownloadStatus {
        /**
         * a method will be called when the items downloaded
         *
         * @param totalurls          total size of the url size
         * @param downloadPercentage downloaded percentage
         * @param successPercent     success percentage
         * @param failurePercent     failure percentage
         */
        void DownloadedItems(int totalurls, int downloadPercentage, int successPercent, int failurePercent);

        void CurrentDownloadPercentage(LinkedHashMap<String, ProgressModel> trackRecord);
    }

    public static Constraints getConstraint() {
        return new Constraints.Builder()
                // For Work that requires Internet Connectivity
                .setRequiredNetworkType(NetworkType.CONNECTED)
//                // For work that require minimum battery level i.e Rendering video
                .setRequiresBatteryNotLow(true)
                .build();
    }

    /**
     * a method to convert Gson to String
     *
     * @param arrayList list of String to convert json
     * @return it return the json
     */
    public static String gsonToString(List<String> arrayList) {
        Gson gson = new Gson();
        return gson.toJson(arrayList);

    }

    private static class APICall extends AsyncTask<Void, Void, String> {
        DownloadStatus downloadStatus;
        String downloadFrom;
        int collectionId;

        public APICall(DownloadStatus downloadStatus, String url, int collectionId) {
            this.downloadStatus = downloadStatus;
            this.downloadFrom = url;
            this.collectionId = collectionId;
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            try {
                //TODO :Change into Builder method
                new ImageDownloaderHelper()
                        .setUrls(new UrlHelper().setSource(strings).setUrlType(UrlHelper.UrlType.IMAGE).getUrl())
                        .setCollectionId(collectionId)
                        .create();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... strings) {
            Request request = new Request.Builder().url(downloadFrom).build();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final String[] reqResponse = new String[2];

            CustomDownloadClient.getClient(false).newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    countDownLatch.countDown();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    reqResponse[1] = response.body().string();
                    countDownLatch.countDown();
                }

            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return reqResponse[1];
        }
    }


}



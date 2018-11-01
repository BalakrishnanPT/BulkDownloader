package balakrishnan.me.bulkdownloader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageDownloaderWorker extends Worker {
    private String TAG = getClass().getSimpleName();
    private static Gson gson = new Gson();
    private List<String> g;
    private LocalData localData;
    private String fileName;

    public ImageDownloaderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * a method to convert Gson to String
     *
     * @param arrayList list of String to convert json
     * @return it return the json
     */
    public static synchronized String gsonToString(List<String> arrayList) {
        return gson.toJson(arrayList);
    }

    @NonNull
    @Override
    public Result doWork() {
        String[] value = getInputData().getStringArray("value");
        if (value == null) return Result.FAILURE;
        fileName = value[0];
        localData = new LocalData(getApplicationContext());
        g = toList(localData.getStringPreferenceValue(value[0]));
        final CountDownLatch startSignal = new CountDownLatch(g.size());
        for (String s : g) {
            Log.d(TAG, "run: " + android.util.Patterns.WEB_URL.matcher(s).matches());

            final Result[] result = {Result.SUCCESS};
            final Request request = new Request.Builder()
                    .url(s)
                    .build();

            OkHttpClient client = CustomDownloadClient.getClient(true);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("state", false);
                    MessagerHandler.sendMessage(1, "status", bundle);
                    result[0] = Result.FAILURE;
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    response.body().string();
                    response.body().close();
                    removeFromList(call.request().url().toString());
                    result[0] = Result.SUCCESS;
                    startSignal.countDown();
                }
            });
        }
        try {
            startSignal.await();
            if (toList(localData.getStringPreferenceValue(fileName)).size() == 0)
                ImageDownloaderHelper.removeFromWork(Integer.parseInt(value[1]));
            return Result.SUCCESS;
        } catch (InterruptedException e) {
            Log.d("InterruptedException", "doWork: ");
            e.printStackTrace();
            return Result.RETRY;
        }
    }


    List<String> toList(String g) {
        Gson gson = new Gson();
        return gson.fromJson(g,
                new TypeToken<List<String>>() {
                }.getType());
    }

    private synchronized void removeFromList(String s) {
        List<String> g = toList(localData.getStringPreferenceValue(fileName));
        g.remove(s);
        localData.setStringPreferenceValue(fileName, gsonToString(g));
    }

}
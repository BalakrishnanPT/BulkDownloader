package balakrishnan.me.downloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.LinkedHashMap;

import balakrishnan.me.bulkdownloader.ImageDownloaderException;
import balakrishnan.me.bulkdownloader.ImageDownloaderHelper;
import balakrishnan.me.bulkdownloader.ProgressModel;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            new ImageDownloaderHelper().setDownloadStatus(getCallback())
                    .setUrl("https://5bc9d0eb57adaa001375b1c6.mockapi.io/sampleget")
                    .setCollectionId(1)
                    .createImageDownloadWorkURl();
        } catch (ImageDownloaderException e) {
            e.printStackTrace();
        }
    }

    public ImageDownloaderHelper.DownloadStatus getCallback() {
        return new ImageDownloaderHelper.DownloadStatus() {
            @Override
            public void DownloadedItems(int totalurls, int downloadPercentage, int successPercent, int failurePercent) {
//               You can get Total Image Downloaded progress here
            }

            @Override
            public void CurrentDownloadPercentage(LinkedHashMap<String, ProgressModel> trackRecord) {
//              You can get current file downloaded progress here.
                Log.d(TAG, "CurrentDownloadPercentage: " + trackRecord.size());
                for (ProgressModel progressModel : trackRecord.values()) {
//                   Percentage downloaded
                    Log.d(TAG, "CurrentDownloadPercentage: getProgress: " + progressModel.getProgress());
//                    Downloaded Size in bytes
                    Log.d(TAG, "CurrentDownloadPercentage: getDownloadedSize: " + progressModel.getDownloadedSize());
//                    Downloaded Size in MB
                    Log.d(TAG, "CurrentDownloadPercentage: getDownloadedSizeInMB: " + progressModel.getDownloadedSizeInMB());
//                    File Size in bytes
                    Log.d(TAG, "CurrentDownloadPercentage: getFileSize: " + progressModel.getFileSize());
//                    File Size in MB
                    Log.d(TAG, "CurrentDownloadPercentage: getFileSizeInMB: " + progressModel.getFileSizeInMB());
                }
            }

        };
    }
}

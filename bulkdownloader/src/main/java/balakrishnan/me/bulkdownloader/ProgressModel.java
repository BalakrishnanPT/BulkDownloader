package balakrishnan.me.bulkdownloader;

public class ProgressModel {
    float fileSize = (float) 0.0;
    float downloadedSize = (float) 0.0;
    int progress = 0;

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public ProgressModel(float fileSize, float downloadedSize) {
        this.fileSize = fileSize;
        this.downloadedSize = downloadedSize;
        this.progress = (int) ((100 * downloadedSize) / fileSize);
    }

    public int getProgress() {
        return progress;
    }

    public float getFileSize() {
        return fileSize;
    }

    public float getFileSizeInMB() {
        return getSizeinMB(fileSize);
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public float getDownloadedSize() {
        return downloadedSize;
    }

    public float getDownloadedSizeInMB() {
        return getSizeinMB(downloadedSize);
    }

    public void setDownloadedSize(float downloadedSize) {
        this.downloadedSize = downloadedSize;
    }


    @Override
    public String toString() {
        return "ProgressModel{" +
                "fileSize=" + fileSize +
                ", downloadedSize=" + downloadedSize +
                ", progress=" + progress +
                '}';
    }

    private float getSizeinMB(float fileSizeInBytes) {
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        float fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        return fileSizeInKB / 1024;
    }
}

package balakrishnan.me.bulkdownloader;


import java.text.MessageFormat;

public class BulkDownloaderConstant {
    public static String DownloadStatusFileName(int collectionID) {
        return MessageFormat.format("size_value_{0}", collectionID);
    }
}

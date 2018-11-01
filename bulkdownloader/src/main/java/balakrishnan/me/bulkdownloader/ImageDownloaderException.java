package balakrishnan.me.bulkdownloader;

public class ImageDownloaderException extends Exception {

    int code;

    public ImageDownloaderException(int code) {
        super();
        this.code = code;
    }

    public ImageDownloaderException(ImageDownloadingException message) {
        super(message.text());
        this.code = message.value();
    }

    public ImageDownloaderException(ImageDownloadingException message, Throwable cause) {
        super(message.text(), cause);
        this.code = message.value();
    }

    public int getCode() {
        return code;
    }
}

enum ImageDownloadingException {
    NULL_LIST(1, "The List is Null"),
    COLLECTION_ID_MISSING(2, "Collection id is 0 or invalid");

    private final int value;
    private final String text;

    ImageDownloadingException(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int value() {
        return value;
    }

    public String text() {
        return text;
    }
}
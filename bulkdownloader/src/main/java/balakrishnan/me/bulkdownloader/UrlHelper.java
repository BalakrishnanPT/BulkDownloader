package balakrishnan.me.bulkdownloader;

import android.support.annotation.IntDef;
import android.util.Patterns;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.bulkdownloader.ImageDownloader.UrlHelper.UrlType.IMAGE;
import static com.example.bulkdownloader.ImageDownloader.UrlHelper.UrlType.NONE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class UrlHelper {

    public String source;
    @UrlType
    public int urlType = NONE;

    public UrlHelper() {
    }

    private static ArrayList<String> extractLinks(String text, @UrlType int urlType) {
        ArrayList<String> links = new ArrayList<String>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        switch (urlType) {
            case NONE:
                while (m.find())
                    links.add(m.group());
                break;
            case IMAGE:
                while (m.find())
                    if (isImageUrl(m.group()))
                        links.add(m.group());
                break;
        }
        return links;
    }

    private static boolean isImageUrl(String url) {
        String rx = "(?:https|http)?:/(?:/[^/]+)+\\.(?:jpg|gif|png|jpeg)";
        Pattern pat = Pattern.compile(rx);
        return pat.matcher(url).matches();
    }

    public UrlHelper setSource(String source) {
        this.source = source;
        return this;
    }

    public UrlHelper setUrlType(int urlType) {
        this.urlType = urlType;
        return this;
    }

    public ArrayList<String> getUrl() throws Exception {
        if (source == null) throw new Exception("Source text is null");
        return extractLinks(source, urlType);
    }

    @Retention(SOURCE)
    @IntDef({NONE, IMAGE})
    public @interface UrlType {
        int NONE = 0;
        int IMAGE = 1;
    }
}

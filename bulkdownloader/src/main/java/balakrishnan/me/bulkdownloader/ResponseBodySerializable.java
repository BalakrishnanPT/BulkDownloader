package balakrishnan.me.bulkdownloader;

import java.io.InputStream;
import java.io.Serializable;

public class ResponseBodySerializable implements Serializable {
    InputStream responseBody;

    public InputStream getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(InputStream responseBody) {
        this.responseBody = responseBody;
    }

    public ResponseBodySerializable(InputStream responseBody) {
        this.responseBody = responseBody;
    }

}

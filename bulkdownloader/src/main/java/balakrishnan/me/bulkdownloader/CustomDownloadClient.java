package balakrishnan.me.bulkdownloader;


import android.os.Bundle;
import android.support.annotation.RestrictTo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class CustomDownloadClient {
    public static OkHttpClient normalClient;
    public static OkHttpClient progressClient;

    /**
     * getClient used to get HttpClient
     *
     * @param flag set as true to get % of downloaded file, false to get normal client
     * @return
     */
    public static OkHttpClient getClient(Boolean flag) {
        final String TAG = "DownloadedProcess";
        if (flag) {

            if (progressClient == null) {
                progressClient = new OkHttpClient.Builder()
                        .addNetworkInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                final Response originalResponse = chain.proceed(chain.request());
                                return originalResponse.newBuilder()
                                        .body(new ProgressResponseBody(originalResponse.body(), new ProgressListener() {
                                            @Override
                                            public void update(long bytesRead, long contentLength, boolean done, String url) {
                                                if (done) {
                                                } else {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putInt("progress", (int) ((100 * bytesRead) / contentLength));
                                                    bundle.putFloat("fileSize", contentLength);
                                                    bundle.putFloat("currentSize", bytesRead);
                                                    bundle.putString("url", url);
                                                    MessagerHandler.sendMessage(2, "induvidualProgress", bundle);
                                                }
                                            }
                                        }, originalResponse.request().url().toString()))
                                        .build();
                            }
                        })
                        .build();
            }
            return progressClient;
        } else {
            if (normalClient == null)
                normalClient = new OkHttpClient();
            return normalClient;
        }
    }

    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done, String Url);
    }

    public static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;
        private String url;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener, String url) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
            this.url = url;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1, url);
                    return bytesRead;
                }
            };
        }
    }
}

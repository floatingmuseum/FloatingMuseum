package com.floatingmuseum.androidtest.functions.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Floatingmuseum on 2017/3/7.
 */

public class DownloadResponseBody extends ResponseBody {


    private ResponseBody body;
    private DownloadProgressListener listener;
    private BufferedSource source;

    public DownloadResponseBody(ResponseBody body, DownloadProgressListener listener) {
        this.body = body;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return body.contentType();
    }

    @Override
    public long contentLength() {
        return body.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (source == null) {
            source = Okio.buffer(source(body.source()));
        }
        return null;
    }

    private Source source(BufferedSource source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if (null != listener) {
                    listener.update(totalBytesRead, body.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}

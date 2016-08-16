package org.quanqi.androidnetworkdemo;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * A HttpStack implement witch can verify specified self-signed certification.
 */
public class SelfSignSslOkHttpStack extends HurlStack {

    private final OkHttpClient okHttpClient;

    private Map<String, SSLSocketFactory> socketFactoryMap;

    /**
     * Create a OkHttpStack with default OkHttpClient.
     */
    public SelfSignSslOkHttpStack(Map<String, SSLSocketFactory> factoryMap) {
        this(new OkHttpClient(), factoryMap);
    }

    /**
     * Create a OkHttpStack with a custom OkHttpClient
     * @param okHttpClient Custom OkHttpClient, NonNull
     */
    public SelfSignSslOkHttpStack(OkHttpClient okHttpClient, Map<String, SSLSocketFactory> factoryMap) {
        if (okHttpClient == null) {
            throw new NullPointerException("Client must not be null.");
        }
        this.okHttpClient = okHttpClient;
        this.socketFactoryMap = factoryMap;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        if ("https".equals(url.getProtocol()) && socketFactoryMap.containsKey(url.getHost())) {
            HttpsURLConnection connection = (HttpsURLConnection) new OkUrlFactory(okHttpClient).open(url);
            connection.setSSLSocketFactory(socketFactoryMap.get(url.getHost()));
            return connection;
        } else {
            return  new OkUrlFactory(okHttpClient).open(url);
        }
    }
}

package com.linqia.abmat;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assume;

public class TestingPrerequisites {

    public static boolean hasInternetAccess() {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            CloseableHttpResponse result = client.execute(new HttpGet(
                    "https://github.com/"));
            return result.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK;
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return false;
    }

    public static void assumeInternetAccess() {
        Assume.assumeTrue(hasInternetAccess());
    }
}

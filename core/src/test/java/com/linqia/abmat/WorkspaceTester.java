package com.linqia.abmat;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkspaceTester {

    private static final Logger LOG = LoggerFactory
            .getLogger(WorkspaceTester.class);

    private static boolean hasInternetAccess() {
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

    public static void main(String[] args) throws IOException {
        if (!hasInternetAccess()) {
            LOG.error("No internet access, so can't run test");
            return;
        }
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.readEnvironment().findGitDir().build();
        LOG.info("Branch: {}", repo.getBranch());
        LOG.info("Remotes: {}", repo.getRemoteNames());
        LOG.info("State: {}", repo.getRepositoryState());
        LOG.info("Root: {}", repo.getWorkTree());
    }
}

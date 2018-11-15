package com.aagproservices.jenkins.bitbucketsteps.service;

import com.aagproservices.jenkins.bitbucketsteps.BitbucketServer;
import com.aagproservices.jenkins.bitbucketsteps.util.HttpUtil;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.ws.rs.BadRequestException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

    private static final String SSL_INSTANCE_TYPE = "SSL";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String BASE_RESOURCE = "/rest/api/1.0";

    protected BitbucketServer bitbucketSite;
    private OkHttpClient client;
    private Map<String, String> defaultRequestHeaders;
    private Map<String, String> customRequestHeaders;

    BaseService(final BitbucketServer bitbucketSite) {
        this.bitbucketSite = bitbucketSite;
        this.customRequestHeaders = new HashMap<>();
        initClient();
        initHeaders();
    }

    protected String guessMediaType(final File file) {
        return guessMediaType(file.getAbsolutePath());
    }

    protected String guessMediaType(final String fileName) {
        try {
            String contentType =  Files.probeContentType(Paths.get(fileName));
            if(contentType == null) {
                contentType = "application/octet-stream";
            }
            return contentType;
        } catch (IOException ex) {
            return "application/octet-stream";
        }
    }

    RequestBody buildBodyForFileUpload(final String name, final String filePath, final String mediaType) {
        return buildBodyForFileUpload(name, filePath, mediaType, Collections.emptyList());
    }

    protected RequestBody buildBodyForFileUpload(final String name, final String filePath, final String mediaType, final List<MultipartField> additionalMultipartFields) {
        File sourceFile = new File(filePath);
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(name, sourceFile.getName(), RequestBody.create(MediaType.parse(mediaType), new File(filePath)));

        if (additionalMultipartFields != null && !additionalMultipartFields.isEmpty()) {
            for(MultipartField field : additionalMultipartFields) {
                multipartBuilder = multipartBuilder.addFormDataPart(
                        field.getKey(),
                        field.getValue()
                );
            }
        }

        return multipartBuilder.build();
    }

    protected Request buildRequest(final String project, final String repoSlug,
                                   final String requestResource, final String httpMethod,
                                   final RequestBody requestBody, final Map<String, String> queryParams) {
        Request.Builder requestBuilder = new Request.Builder();
        registerAllHeaders(requestBuilder);
        //clear the custom headers for the next upcoming request
        this.customRequestHeaders.clear();
        requestBuilder.url(buildUrl(
                bitbucketSite.getUrl()
                        + BASE_RESOURCE
                        + "/projects/" + project
                        + "/repos/" + repoSlug
                        + "/" + requestResource,
                queryParams));
        requestBuilder.method(httpMethod, requestBody);
        return requestBuilder.build();
    }

    protected JSONObject executeRequest(final Request request) throws JSONException, BadRequestException {
        try {
            OkHttpClient client = getClient();
            Response response = client.newCall(request).execute();

            JSONObject result = new JSONObject();
            ResponseBody respBody = response.body();
            if (respBody != null) {
                String respString = respBody.string();
                if(respString != null && respString.trim().length() > 0) {
                    result = new JSONObject(respString);
                }
            }

            if (!response.isSuccessful()) {
                String errorMsg = result.toString();
                try {
                    JSONArray errors = result.getJSONArray("errors");
                    if (errors != null) {
                        errorMsg = errors.getJSONObject(0).getString("message");
                    }
                } catch(Exception ex) {
                    //no problem - just use the whole result string
                }
                LOGGER.error("Error response from server: " + errorMsg);
                throw new BadRequestException(errorMsg);
            }
            return result;
        } catch (IOException e) {
            LOGGER.error("Error while executing request " + request.toString(), e);
            throw new IllegalArgumentException(e);
        }
    }

    private void initClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(this.bitbucketSite.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(this.bitbucketSite.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(this.bitbucketSite.getTimeout(), TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(bitbucketSite.getPoolSize(), 15, TimeUnit.SECONDS));

        if (bitbucketSite.getTrustAllCertificates()) {
            builder = installTrustManager(builder, HttpUtil.buildAllTrustingManager())
                    .hostnameVerifier((s, sslSession) -> true);
        }

        this.client = builder.build();
    }

    private OkHttpClient.Builder installTrustManager(OkHttpClient.Builder builder, final TrustManager[] allTrustingManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance(SSL_INSTANCE_TYPE);
            sslContext.init(null, allTrustingManager, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return builder.socketFactory(sslSocketFactory);
        } catch (NoSuchAlgorithmException e) {
            //We should never land here
            return builder;
        } catch (KeyManagementException e) {
            LOGGER.error("Something went wrong with the key-management", e.getMessage());
            return builder;
        }
    }

    private void initHeaders() {
        this.defaultRequestHeaders = new HashMap<>();
        this.defaultRequestHeaders.put(AUTHORIZATION_HEADER, Credentials.basic(bitbucketSite.getUserName(), bitbucketSite.getPassword()));
    }

    private void registerAllHeaders(final Request.Builder builder) {
        defaultRequestHeaders.forEach(builder::addHeader);
        customRequestHeaders.forEach(builder::addHeader);
    }

    private void addQueryParams(final HttpUrl.Builder urlBuilder, final Map<String, String> queryParams) {
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(urlBuilder::addQueryParameter);
        }
    }

    private HttpUrl buildUrl(final String url, final Map<String, String> queryParams) {
        HttpUrl urlObject = HttpUrl.parse(url);
        HttpUrl.Builder urlBuilder = urlObject.newBuilder();
        addQueryParams(urlBuilder, queryParams);
        return urlBuilder.build();
    }

    private void registerRequestHeader(final String name, final String value) {
        this.customRequestHeaders.put(name, value);
    }

    public OkHttpClient getClient() {
        return client;
    }

    protected static final class MultipartField {
        private String key;
        private String value;

        public MultipartField(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}

package com.jivesoftware.android.mobile.sdk.parser;

import com.jivesoftware.android.mobile.sdk.core.JiveCoreConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class HttpResponseParser<T> {

    @Nonnull
    private final JiveCoreExceptionFactory jiveCoreExceptionFactory;

    protected HttpResponseParser(@Nonnull JiveCoreExceptionFactory jiveCoreExceptionFactory) {
        this.jiveCoreExceptionFactory = jiveCoreExceptionFactory;
    }

    @Nullable
    public T parseResponse(@Nullable HttpResponse httpResponse) throws IOException {
        if (httpResponse == null) {
            throw new JiveCoreNullHttpResponseException();
        }
        StatusLine statusLine = httpResponse.getStatusLine();
        HttpEntity httpEntity = httpResponse.getEntity();
        int statusCode;
        if (statusLine == null) {
            statusCode = -1;
        } else {
            statusCode = statusLine.getStatusCode();
        }
        int statusCategoryCode = statusCode / 100;
        boolean isMobileGatewayResponse = isMobileGatewayResponse(httpResponse);
        if ((statusCategoryCode == 2) && !isMobileGatewayResponse) {
            T result = parseValidResponse(httpResponse, statusCode, httpEntity);
            return result;
        } else if (statusCategoryCode == 5) {
            throw new JiveCoreServerException(httpResponse);
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (httpEntity != null) {
                try {
                    httpEntity.writeTo(byteArrayOutputStream);
                } catch (IOException e) {
                    // ignore, we're already in an error state. The HTTP status error is more important.
                } finally {
                    httpEntity.consumeContent();
                }
            }
            byte[] contentBodyBytes = byteArrayOutputStream.toByteArray();
            if (isMobileGatewayResponse) {
                throw new JiveCoreMobileGatewayException(httpResponse);
            } else {
                IOException ioException = jiveCoreExceptionFactory.createException(httpResponse, statusCode, httpEntity, contentBodyBytes);
                throw ioException;
            }
        }
    }

    private boolean isMobileGatewayResponse(HttpResponse httpResponse) {
        Header firstMobileGatewayHeader = httpResponse.getFirstHeader(JiveCoreConstants.X_JIVE_MOBILE_GATEWAY);
        if (firstMobileGatewayHeader == null) {
            return false;
        } else {
            String firstMobileGatewayHeaderValue = firstMobileGatewayHeader.getValue();
            boolean isMobileGatewayResponse = "true".equalsIgnoreCase(firstMobileGatewayHeaderValue);
            return isMobileGatewayResponse;
        }
    }

    @Nullable
    protected abstract T parseValidResponse(
            @Nonnull HttpResponse httpResponse,
            int statusCode,
            @Nullable HttpEntity httpEntity) throws IOException;
}

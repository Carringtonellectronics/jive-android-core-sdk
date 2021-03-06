package com.jivesoftware.android.mobile.sdk.parser;

import com.jivesoftware.android.mobile.sdk.entity.ErrorEntity;
import com.jivesoftware.android.mobile.sdk.entity.SimpleErrorEntity;
import com.jivesoftware.android.mobile.sdk.json.JiveJson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class JiveCoreExceptionFactory {
    @Nonnull
    private final JiveJson jiveJson;

    public JiveCoreExceptionFactory(@Nonnull JiveJson jiveJson) {
        this.jiveJson = jiveJson;
    }

    @Nonnull
    public JiveCoreException createException(HttpResponse httpResponse, int statusCode, @Nullable HttpEntity httpEntity, byte[] contentBodyBytes) {
        ErrorEntity errorEntity;
        JiveCoreException errorEntityParseException;
        try {
            errorEntity = jiveJson.fromJson(new ByteArrayInputStream(contentBodyBytes), ErrorEntity.class);
            if (errorEntity == null) {
                errorEntityParseException = new JiveCoreUnparsedException("Parsed a null ErrorEntity", httpResponse, httpEntity, contentBodyBytes);
            } else {
                errorEntityParseException = null;
            }
        } catch (IOException e) {
            errorEntity = null;
            // We'll get here on JSON parse errors, caused by HTML content in a 404 response, for example.
            errorEntityParseException = new JiveCoreUnparsedException(e, httpResponse, httpEntity, contentBodyBytes);
        }

        if (isLoginRequiredError(statusCode, errorEntity)) {
            return new JiveCoreLoginRequiredException(httpResponse, errorEntity);
        }

        if (errorEntity == null) {
            return errorEntityParseException;
        }

        Integer errorCode = errorEntity.getErrorCode();
        if (isOAuthError(statusCode, errorEntity)) {
            if (errorCode == null) {
                return new JiveCoreOAuthException(httpResponse, errorEntity);
            } else if (JiveCoreOAuthTemporarilyUnavailableException.ERROR_CODE == errorCode) {
                return new JiveCoreOAuthTemporarilyUnavailableException(httpResponse, errorEntity);
            } else if (JiveCoreOAuthInvalidClientException.ERROR_CODE == errorCode) {
                return new JiveCoreOAuthInvalidClientException(httpResponse, errorEntity);
            } else {
                return new JiveCoreOAuthException(httpResponse, errorEntity);
            }
        }

        String apiErrorCode = errorEntity.getAPIErrorCode();
        return new JiveCoreAPIException(httpResponse, errorEntity, apiErrorCode);
    }

    public static boolean isLoginRequiredError(int responseCode, @Nullable ErrorEntity errorEntity) {
        if (responseCode == HttpStatus.SC_UNAUTHORIZED) {
            return true;
        } else if ((responseCode == HttpStatus.SC_BAD_REQUEST || responseCode == HttpStatus.SC_FORBIDDEN)
                && errorEntity instanceof SimpleErrorEntity) {
            Integer errorCode = errorEntity.getErrorCode();
            return errorCode == SimpleErrorEntity.OAuth2ErrorType.INVALID_GRANT.ordinal() ||
                    errorCode == SimpleErrorEntity.OAuth2ErrorType.ACCESS_DENIED.ordinal();
        }

        return false;
    }

    public static boolean isOAuthError(int responseCode, @Nullable ErrorEntity errorEntity) {
        if (responseCode == HttpStatus.SC_BAD_REQUEST && errorEntity instanceof SimpleErrorEntity) {
            final Integer errorCode = errorEntity.getErrorCode();

            return errorCode != -1 && errorCode != SimpleErrorEntity.OAuth2ErrorType.ACCESS_DENIED.ordinal() &&
                    errorCode != SimpleErrorEntity.OAuth2ErrorType.INVALID_GRANT.ordinal();
        }

        return false;
    }
}

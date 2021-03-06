package com.jivesoftware.android.mobile.sdk.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@JsonSerialize(include= NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleErrorEntity implements ErrorEntity {
    private String error;

    private String description;

    public SimpleErrorEntity() {}

    @JsonCreator
    public SimpleErrorEntity(@JsonProperty("error") String error, @JsonProperty("error_description") String description) {
        this.error = error;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getErrorCode() {
        Integer errorCode = -1;
        if (error != null) {
            try {
                OAuth2ErrorType errorType = OAuth2ErrorType.valueOf(error.toUpperCase());
                errorCode = errorType.ordinal();
            } catch (IllegalArgumentException e) {
                // Empty
            }
        }
        return errorCode;
    }

    @Override
    public String getAPIErrorCode() {
        return error == null ? null : error.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object) this).getClass() != o.getClass()) {
            return false;
        }

        SimpleErrorEntity that = (SimpleErrorEntity) o;

        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        return !(error != null ? !error.equals(that.error) : that.error != null);
    }

    @Override
    public int hashCode() {
        int result = error != null ? error.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleErrorEntity{" +
                "error='" + error + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * We can't change the order of these values. We can only append to them.
     */
    public enum OAuth2ErrorType {
        INVALID_REQUEST,
        INVALID_CLIENT,
        INVALID_GRANT,
        UNAUTHORIZED_CLIENT,
        UNSUPPORTED_GRANT_TYPE,
        INVALID_SCOPE,
        ACCESS_DENIED,
        UNSUPPORTED_RESPONSE_TYPE,
        SERVER_ERROR,
        TEMPORARILY_UNAVAILABLE
    }
}

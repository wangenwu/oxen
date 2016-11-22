package com.baren.bison.common.http;

import org.apache.http.Header;

import java.util.List;

/**
 * Created by user on 16/11/18.
 */
public class ResponseData {

    public static final int TIME_OUT_CODE = 504;

    private String content;
    private int statusCode;
    private List<Header> headers;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    private Exception exception;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public String toString() {
        return new StringBuilder().append("statusCode:").append(statusCode)
                .append(" content:").append(content)
                .append(" exception:").append(exception).toString();
    }
}

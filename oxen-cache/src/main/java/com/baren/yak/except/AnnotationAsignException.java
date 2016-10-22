package com.baren.yak.except;

/**
 * Created by user on 16/7/30.
 */
public class AnnotationAsignException extends Exception {

    private static final long serialVersionUID = -8079095237716455423L;

    public AnnotationAsignException(final Exception cause) {
        super(cause);
    }
    public AnnotationAsignException(String msg) {
        super(msg);
    }
}

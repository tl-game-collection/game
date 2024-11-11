package com.xiuxiu.core.service;

public class ServiceException extends RuntimeException {
    public ServiceException(Throwable err) {
        super(err);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable err) {
        super(message, err);
    }
}

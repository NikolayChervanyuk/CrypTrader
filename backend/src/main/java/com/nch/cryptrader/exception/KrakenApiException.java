package com.nch.cryptrader.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KrakenApiException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "KrakenApiException thrown";

    public KrakenApiException() {
        super();
        log.error(DEFAULT_MESSAGE + ": No further details");
    }
    public KrakenApiException(String errorMessage) {
        super(DEFAULT_MESSAGE + ": " + errorMessage);
        log.error(DEFAULT_MESSAGE + ": {}", errorMessage, this);
    }
}

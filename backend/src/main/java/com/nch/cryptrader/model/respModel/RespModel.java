package com.nch.cryptrader.model.respModel;

public interface RespModel<T> {

    T getData();
    String getErrorMessage();
}

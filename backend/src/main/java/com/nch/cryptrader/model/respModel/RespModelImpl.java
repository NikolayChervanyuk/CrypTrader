package com.nch.cryptrader.model.respModel;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RespModelImpl<T> implements RespModel<T> {
    private RespModelImpl(@NotNull T data) {
        this.data = data;
    }

    private RespModelImpl(@NotNull String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private T data;
    private String errorMessage;

    public static <T> RespModelImpl<T> of(@NotNull T data) {
        return new RespModelImpl<>(data);
    }

    public static <T> RespModelImpl<T> ofError(@NotNull String errorMessage) {
        return new RespModelImpl<>(errorMessage);
    }

    public static <T> RespModelImpl<T> serviceUnavailableError() {
        return new RespModelImpl<>(GenericErrorMessages.SERVICE_UNAVAILABLE.getMessage());
    }

    public static <T> RespModelImpl<T> unauthorizedError() {
        return new RespModelImpl<>(GenericErrorMessages.UNAUTHORIZED.getMessage());
    }

    @Getter
    public enum GenericErrorMessages {
        SERVICE_UNAVAILABLE("Service unavailable, try again later"),
        UNAUTHORIZED("Unauthorized! Authentication required or not enough user permissions");

        GenericErrorMessages(final String message) {
            this.message = message;
        }

        private final String message;
    }
}

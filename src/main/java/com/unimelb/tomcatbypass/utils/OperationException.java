package com.unimelb.tomcatbypass.utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OperationException extends Exception {
    public OperationException(String message) {
        super(message);
    }
}

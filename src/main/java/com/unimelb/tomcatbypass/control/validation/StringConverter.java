package com.unimelb.tomcatbypass.control.validation;

public interface StringConverter<T> {
    T getObjectFromString(String string);
}

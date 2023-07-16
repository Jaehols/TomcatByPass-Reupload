package com.unimelb.tomcatbypass.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ModelSetter<T> {
    T setModelAttributesFromDb(ResultSet rs) throws SQLException;
}

package com.unimelb.tomcatbypass.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QuerySetter {
    void setQueryParametersForDb(PreparedStatement preparedStatement) throws SQLException;
}

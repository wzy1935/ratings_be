package com.sc.ratings.utils;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import java.sql.*;
import java.util.Arrays;

public class IntegerArrayTypeHandler implements TypeHandler<Integer[]> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Integer[] parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.ARRAY);
        } else {
            Array array = ps.getConnection().createArrayOf("integer", parameter);
            ps.setArray(i, array);
        }
    }

    @Override
    public Integer[] getResult(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        return array != null ? (Integer[]) array.getArray() : null;
    }

    @Override
    public Integer[] getResult(ResultSet rs, int columnIndex) throws SQLException {
        Array array = rs.getArray(columnIndex);
        return array != null ? (Integer[]) array.getArray() : null;
    }

    @Override
    public Integer[] getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Array array = cs.getArray(columnIndex);
        return array != null ? (Integer[]) array.getArray() : null;
    }
}

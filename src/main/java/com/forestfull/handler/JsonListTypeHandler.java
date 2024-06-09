package com.forestfull.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.forestfull.entity.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@MappedTypes(Json.List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonListTypeHandler extends BaseTypeHandler<Json.List> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Json.List parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    public static Json.List convertToJsonList(String contents) {
        try {
            final JsonNode jsonNode = JsonTypeHandler.reader.readTree(contents);
            if (!jsonNode.isArray()) return null;

            final Json.List jsonList = new Json.List();
            final Json.List jsonArray = JsonTypeHandler.reader.readValue(contents, Json.List.class);

            for (Object object : jsonArray)
                jsonList.add(JsonTypeHandler.convertToJson(object));

            return jsonList;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Json.List getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertToJsonList(rs.getString(columnName));
    }

    @Override
    public Json.List getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertToJsonList(rs.getString(columnIndex));
    }

    @Override
    public Json.List getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertToJsonList(cs.getString(columnIndex));
    }
}
package com.forestfull.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import java.util.List;
import java.util.Map;

@Slf4j
@MappedTypes(Json.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonTypeHandler extends BaseTypeHandler<Json> {

    public static ObjectMapper mapper = new ObjectMapper();
    public static ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    public static ObjectReader reader = mapper.reader();

    public static <T> T read(Object var1, Class<T> var2) {
        try {
            return reader.readValue(String.valueOf(var1), var2);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Json parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    public static Json convertToJson(Object contents) {
        try {
            return convertToJson(writer.writeValueAsString(contents));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static Json convertToJson(String contents) {
        try {
            //노드 타입이 오브젝트가 아닐 때 null 리턴
            if (!reader.readTree(contents).isObject()) return null;

            final Json json = new Json();
            final Json jsonContents = reader.readValue(contents, Json.class);
            for (Map.Entry<String, Object> entry : jsonContents.entrySet()) {
                final boolean isArrayType = entry.getValue() instanceof List<?>;

                if (isArrayType) {
                    json.put(entry.getKey(), JsonListTypeHandler.convertToJsonList(writer.writeValueAsString(entry.getValue())));

                } else {
                    json.put(entry.getKey(), entry.getValue());
                }
            }

            return json;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Json getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertToJson(rs.getString(columnName));
    }

    @Override
    public Json getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertToJson(rs.getString(columnIndex));
    }

    @Override
    public Json getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertToJson(cs.getString(columnIndex));
    }
}
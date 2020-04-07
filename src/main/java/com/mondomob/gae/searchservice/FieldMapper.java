package com.mondomob.gae.searchservice;

import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mondomob.gae.searchservice.DateFieldFormatter.formatForSearch;
import static com.mondomob.gae.searchservice.DateFieldFormatter.isDate;

public class FieldMapper {

    public static final String NESTED_OBJECT_DELIMITER = "__";

    public List<Field> map(Map<String, Object> fields) {
        return toFields("", fields, false);
    }

    private List<Field> toFields(String prefix, Object fields, boolean isComplexObject) {
        List<Field> mappedFields = new ArrayList<>();

        if (fields instanceof Map) {
            Map<String, Object> fieldsMap = (Map<String, Object>) fields;
            for (String fieldName : fieldsMap.keySet()) {
                boolean isNested = prefix.length() > 0;
                String key = isNested ? String.format("%s%s%s", prefix, NESTED_OBJECT_DELIMITER, fieldName) : fieldName;
                Object field = fieldsMap.get(fieldName);
                mappedFields.addAll(toFields(key, field, isNested));
            }
        } else if (fields instanceof String) {
            if (!isComplexObject && isDate(fields)) {
                mappedFields.add(createDateField(prefix, fields));
            } else {
                mappedFields.add(createTextField(prefix, fields));
            }
        } else if (fields instanceof Boolean) {
            mappedFields.add(createTextField(prefix, fields.toString()));
        } else if (fields instanceof Number) {
            if (isComplexObject) {
                mappedFields.add(createTextField(prefix, fields.toString()));
            } else {
                mappedFields.add(createNumberField(prefix, fields));
            }
        } else if (fields instanceof List) {
            ((List) fields).forEach(v -> mappedFields.addAll(toFields(prefix, v, true)));
        } else {
            throw new RuntimeException(String.format("Unsupported type %s", fields.getClass().getSimpleName()));
        }

        return mappedFields;
    }

    private Field createDateField(String key, Object value) {
        Field.Builder fieldBuilder = Field.newBuilder();
        fieldBuilder.setName(key);
        Double dateToIndex = formatForSearch(value);
        fieldBuilder.setNumber(dateToIndex);
        return fieldBuilder.build();
    }

    private Field createTextField(String name, Object value) {
        Field.Builder fieldBuilder = Field.newBuilder();
        fieldBuilder.setName(name);
        fieldBuilder.setText((String) value);
        return fieldBuilder.build();
    }

    private Field createNumberField(String name, Object value) {
        Field.Builder fieldBuilder = Field.newBuilder();
        fieldBuilder.setName(name);
        fieldBuilder.setNumber(((Number) value).doubleValue());
        return fieldBuilder.build();
    }

}

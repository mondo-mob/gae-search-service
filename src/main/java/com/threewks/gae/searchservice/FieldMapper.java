package com.threewks.gae.searchservice;

import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.threewks.gae.searchservice.DateFieldFormatter.getDate;
import static com.threewks.gae.searchservice.DateFieldFormatter.isDate;

public class FieldMapper {

    public static final String NESTED_OBJECT_DELIMITER = "__";

    public List<Field> map(Map<String, Object> fields) {
        return toFields("", fields, false);
    }

    private List<Field> toFields(String prefix, Map<String, Object> fields, boolean isComplexObject) {
        List<Field> mappedFields = new ArrayList<>();

        for (String fieldName : fields.keySet()) {
            String key = prefix + fieldName;
            Object field = fields.get(fieldName);
            if (field instanceof String) {
                if (!isComplexObject && isDate(field)) {
                    mappedFields.add(createDateField(key, field));
                } else {
                    mappedFields.add(createTextField(key, field));
                }

            } else if (field instanceof Boolean) {
                mappedFields.add(createTextField(key, field.toString()));
            } else if (field instanceof Number) {
                if (isComplexObject) {
                    mappedFields.add(createTextField(key, field.toString()));
                } else {
                    mappedFields.add(createNumberField(key, field));
                }
            } else if (field instanceof List) {
                List<Map<String, Object>> values = (List<Map<String, Object>>) field;
                values.forEach(v -> mappedFields.addAll(toFields(key + NESTED_OBJECT_DELIMITER, v, true)));

            } else {
                mappedFields.addAll(toFields(key + NESTED_OBJECT_DELIMITER, (Map<String, Object>) field, true));
            }
        }

        return mappedFields;
    }

    private Field createDateField(String key, Object value) {
        Field.Builder fieldBuilder = Field.newBuilder();
        fieldBuilder.setName(key);
        Date date = getDate(value);
        System.out.println(String.format("indexing date %s", date));
        fieldBuilder.setDate(date);
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

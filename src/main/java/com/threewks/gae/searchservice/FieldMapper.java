package com.threewks.gae.searchservice;

import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.threewks.gae.searchservice.DateFieldFormatter.getDate;
import static com.threewks.gae.searchservice.DateFieldFormatter.isDate;

public class FieldMapper {

    public static final String NESTED_OBJECT_DELIMITER = "__";

    public List<Field> map(Map<String, Object> fields) {
        return toFields("", fields);
    }

    private List<Field> toFields(String prefix, Map<String, Object> fields) {
        List<Field> mappedFields = new ArrayList<>();
        for (String fieldName : fields.keySet()) {
            String key = prefix + fieldName;
            Object field = fields.get(fieldName);
            if (field instanceof  String) {
                if (isDate(field)){
                    mappedFields.add(createDateField(key, field));
                } else  {
                    mappedFields.add(createTextField(key, field));
                }
            } else if (field instanceof Number) {
                mappedFields.add(createNumberField(key, field));
            } else if (field instanceof List) {
                List<Map<String, Object>> values = (List<Map<String, Object>>) field;
                values.forEach(v -> mappedFields.addAll(toFields(key + NESTED_OBJECT_DELIMITER, v)));

            } else {
                mappedFields.addAll(toFields(key + NESTED_OBJECT_DELIMITER, (Map<String, Object>)field));
            }
        }

        return mappedFields;
    }

    private Field createDateField(String key, Object value) {
        Field.Builder fieldBuilder = Field.newBuilder();
        fieldBuilder.setName(key);
        fieldBuilder.setDate(getDate(value));
        return fieldBuilder.build();
    }

    private Field createTextField(String name, Object value){
        Field.Builder fieldBuilder = Field.newBuilder();
        fieldBuilder.setName(name);
        fieldBuilder.setText((String) value);
        return fieldBuilder.build();
    }

    private Field createNumberField(String name, Object value){
        Field.Builder fieldBuilder = Field.newBuilder();
        fieldBuilder.setName(name);
        fieldBuilder.setNumber(((Number) value).doubleValue());
        return fieldBuilder.build();
    }

}

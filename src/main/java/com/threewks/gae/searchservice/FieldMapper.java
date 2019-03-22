package com.threewks.gae.searchservice;

import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FieldMapper {

	public static final String NESTED_OBJECT_DELIMITER = "__";

	public List<Field> map(Map<String, Object> fields) {
		return toFields("", fields);
	}

	private List<Field> toFields(String prefix, Map<String, Object> fields) {
		List<Field> mappedFields = new ArrayList<>();
		for (String fieldName : fields.keySet()) {
			String key = prefix + fieldName;
			Object value = fields.get(fieldName);
			if (value instanceof String) {
				mappedFields.add(toField(key, (String) value));
			} else if (value instanceof List){
				List<Map<String, Object>> values = (List<Map<String, Object>>) value;
				values.forEach(v -> mappedFields.addAll(toFields(key + NESTED_OBJECT_DELIMITER, v)));
			} else {
				mappedFields.addAll(toFields(key + NESTED_OBJECT_DELIMITER, (Map<String, Object>)value));
			}
		}

		return mappedFields;
	}

	private Field toField(String name, String text) {
		Field.Builder fieldBuilder = Field.newBuilder();
		fieldBuilder.setName(name);
		fieldBuilder.setText(text);
		return fieldBuilder.build();
	}

}

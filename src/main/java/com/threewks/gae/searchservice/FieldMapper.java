package com.threewks.gae.searchservice;

import com.google.appengine.api.search.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FieldMapper {

	public List<Field> map(Map<String, Object> fields) {
		return toFields("", fields);
	}

	private List<Field> toFields(String prefix, Map<String, Object> fields) {
		List<Field> mappedFields = new ArrayList<>();
		for (String fieldName : fields.keySet()) {
			Object value = fields.get(fieldName);
			if (value instanceof String) {
				mappedFields.add(toField(prefix + fieldName, (String)value));
			} else {
				mappedFields.addAll(toFields(prefix + fieldName + "__", (Map<String, Object>)value));
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

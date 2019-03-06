package com.threewks.gae.searchservice;

import java.util.Map;

public class QueryOperation {

	private String entityName;
	private Map<String, Object> fields;

	public String getEntityName() {
		return entityName;
	}

	public Map<String, Object> getFields() {
		return fields;
	}
}

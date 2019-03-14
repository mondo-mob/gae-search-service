package com.threewks.gae.searchservice;

import java.util.Map;

public class QueryOperation {

	private String entityName;
	private Map<String, Predicate> fields;
	private QuerySort sort;

	public String getEntityName() {
		return entityName;
	}

	public Map<String, Predicate> getFields() {
		return fields;
	}

	public QuerySort getSort() {
		return sort;
	}
}

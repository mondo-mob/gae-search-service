package com.mondomob.gae.searchservice;

import java.util.Map;

public class IndexEntry {
	private String id;
	private Map<String, Object> fields;

	public String getId() {
		return id;
	}

	public Map<String, Object> getFields() {
		return fields;
	}
}

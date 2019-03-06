package com.threewks.gae.searchservice;

import java.util.Map;

public class IndexEntry {
	private String id;
	private Map<String, String> fields;

	public String getId() {
		return id;
	}

	public Map<String, String> getFields() {
		return fields;
	}
}

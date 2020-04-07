package com.mondomob.gae.searchservice;

public class Predicate {

	private String field;
	private String op;
	private Object value;

	public String getField() { return field; }

	public String getOp() {
		return op;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Predicate{" +
			"field='" + field + '\'' +
			", op='" + op + '\'' +
			", value=" + value +
			'}';
	}
}

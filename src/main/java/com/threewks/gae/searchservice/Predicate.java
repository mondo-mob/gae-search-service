package com.threewks.gae.searchservice;

public class Predicate  {

	private String op;
	private Object value;

	public String getOp() {
		return op;
	}

	public Object getValue() {
		return value;
	}

    @Override
    public String toString() {
        return "Predicate{" +
                "op='" + op + '\'' +
                ", value=" + value +
                '}';
    }
}

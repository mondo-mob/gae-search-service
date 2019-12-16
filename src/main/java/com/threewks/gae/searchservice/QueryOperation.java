package com.threewks.gae.searchservice;

import java.util.List;

public class QueryOperation {
    private String entityName;
    private List<Predicate> fields;
    private QuerySort sort;
    private QueryPage page;

    public String getEntityName() {
        return entityName;
    }

    public List<Predicate> getFields() {
        return fields;
    }

    public QuerySort getSort() {
        return sort;
    }

    public QueryPage getPage() {
        return page;
    }
}

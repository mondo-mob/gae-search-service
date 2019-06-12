package com.threewks.gae.searchservice;

import java.util.List;

public class QueryResults {
    private long resultCount;
    private int limit;
    private int offset;
    private List<String> ids;

    public QueryResults(long resultCount, int limit, int offset, List<String> ids) {
        this.resultCount = resultCount;
        this.limit = limit;
        this.offset = offset;
        this.ids = ids;
    }

    public long getResultCount() {
        return resultCount;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public List<String> getIds() {
        return ids;
    }
}

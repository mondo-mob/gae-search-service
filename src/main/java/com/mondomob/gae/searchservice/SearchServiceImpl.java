package com.mondomob.gae.searchservice;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.GetResponse;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchService;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mondomob.gae.searchservice.DateFieldFormatter.formatForSearch;
import static com.mondomob.gae.searchservice.DateFieldFormatter.isDate;

public class SearchServiceImpl {
    private static final Logger LOG = Logger.getLogger(SearchServiceImpl.class.getName());

    private final SearchService searchService;
    private final FieldMapper fieldMapper;

    public SearchServiceImpl() {
        this.fieldMapper = new FieldMapper();
        this.searchService = SearchServiceFactory.getSearchService();
    }

    public void index(IndexOperation operation) {
        LOG.info(String.format("Indexing %d entries", operation.getEntries().size()));
        List<Document> documents = operation.getEntries()
                .stream()
                .map(indexEntry -> {
                    Document.Builder docBuilder = Document.newBuilder();
                    docBuilder.setId(indexEntry.getId());

                    List<Field> fields = fieldMapper.map(indexEntry.getFields());
                    fields.forEach(docBuilder::addField);

                    return docBuilder.build();
                })
                .collect(Collectors.toList());

        Index index = getIndex(operation.getEntityName());
        index.put(documents);
    }

    public void delete(DeleteOperation operation) {
        LOG.info(String.format("Deleting index for %d entries", operation.getIds().size()));
        getIndex(operation.getEntityName()).delete(operation.getIds());
    }

    public int deleteAll(DeleteAllOperation operation) {
        LOG.info(String.format("Deleting all indexes for %s", operation.getEntityName()));
        int count = 0;
        Index index = getIndex(operation.getEntityName());
        GetRequest request = GetRequest.newBuilder().setReturningIdsOnly(true).setLimit(200).build();
        GetResponse<Document> response = index.getRange(request);

        // can only delete documents in blocks of 200 so we need to iterate until they're all gone
        while (!response.getResults().isEmpty()) {
            List<String> ids = new ArrayList<>();
            for (Document document : response) {
                ids.add(document.getId());
            }
            index.delete(ids);
            count += ids.size();
            response = index.getRange(request);
        }
        return count;
    }

    public QueryResults query(QueryOperation operation) {
        LOG.info(String.format("Running query for %s", operation.getEntityName()));
        String queryStr = toQuery(operation.getFields());
        LOG.info("queryString " + queryStr);

        Index index = getIndex(operation.getEntityName());
        Query query = Query.newBuilder()
                .setOptions(queryOptions(operation.getSort(), operation.getPage()))
                .build(queryStr);

        Results<ScoredDocument> results = index.search(query);

        return new QueryResults(
                results.getNumberFound(),
                Optional.ofNullable(operation.getPage()).map(QueryPage::getLimit).orElse(results.getNumberReturned()),
                Optional.ofNullable(operation.getPage()).map(QueryPage::getOffset).orElse(0),
                results.getResults()
                        .stream()
                        .map(Document::getId)
                        .collect(Collectors.toList())
        );
    }

    private QueryOptions queryOptions(QuerySort sort, QueryPage page) {
        QueryOptions.Builder builder = QueryOptions.newBuilder().setLimit(600);

        if (sort != null) {
            SortExpression sortExpression = SortExpression.newBuilder()
                    .setExpression(sort.getField().replaceAll("\\.", FieldMapper.NESTED_OBJECT_DELIMITER))
                    .setDirection(sort.isDescending() ? SortExpression.SortDirection.DESCENDING : SortExpression.SortDirection.ASCENDING)
                    .build();
            builder.setSortOptions(SortOptions.newBuilder().addSortExpression(sortExpression).build());
        }

        if (page != null) {
            LOG.info(String.format("Setting limit to %d and offset to %d", page.getLimit(), page.getOffset()));
            builder.setLimit(page.getLimit());
            builder.setOffset(page.getOffset());
        }

        return builder.build();
    }

    private String toQuery(List<Predicate> predicates) {
        return predicates
                .stream()
                .map(this::getQueryFragment)
                .collect(Collectors.joining(" "));
    }

    private String getQueryFragment(Predicate predicate) {
        String prefix = predicate.getOp().equals("!=") ? "NOT " : "";
        String operator = predicate.getOp() != null && !predicate.getOp().equals("!=") ? predicate.getOp() : "=";

        String fragment = String.format("%s%s %s ", prefix, predicate.getField().replaceAll("\\.", FieldMapper.NESTED_OBJECT_DELIMITER), operator);
        if (predicate.getValue() instanceof List) {
            List<String> values = (List<String>) predicate.getValue();
            String valueFragment = values.isEmpty() ? "__EMPTY_LIST_MASSIVE_HACK__" : String.join(" OR ", values);
            return String.format("%s(%s)", fragment, valueFragment);
        }

        return fragment + getPredicateValue(predicate.getValue());
    }

    private Object getPredicateValue(Object value) {
        if (isDate(value)) {
            return formatForSearch(value);
        }

        return String.format("\"%s\"", StringEscapeUtils.escapeJava((value.toString())));
    }

    private Index getIndex(String name) {
        return searchService.getIndex(IndexSpec.newBuilder().setName(name).build());
    }

}

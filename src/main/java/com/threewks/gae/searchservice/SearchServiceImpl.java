package com.threewks.gae.searchservice;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchServiceImpl {

	private final SearchService searchService;
	private final FieldMapper fieldMapper;

	public SearchServiceImpl() {
		this.fieldMapper = new FieldMapper();
		this.searchService = SearchServiceFactory.getSearchService();
	}

	public void index(IndexOperation operation) {
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

	public List<String> query(QueryOperation operation) {
		String queryStr = toQuery(operation.getFields());

		Index index = getIndex(operation.getEntityName());
		Query query = Query.newBuilder()
				.setOptions(queryOptions(operation.getSort()))
				.build(queryStr);

		Results<ScoredDocument> results = index.search(query);
		return results.getResults()
				.stream()
				.map(Document::getId)
				.collect(Collectors.toList());
	}

	private QueryOptions queryOptions(QuerySort sort) {
		QueryOptions.Builder builder = QueryOptions.newBuilder();
		if (sort != null) {
			SortExpression sortExpression = SortExpression.newBuilder()
				.setExpression(sort.getField().replaceAll("\\.", FieldMapper.NESTED_OBJECT_DELIMITER))
				.setDirection(sort.isDescending() ? SortExpression.SortDirection.DESCENDING : SortExpression.SortDirection.ASCENDING)
				.build();
			builder.setSortOptions(SortOptions.newBuilder().addSortExpression(sortExpression).build());
		}
		return builder.build();
	}

	private String toQuery(Map<String, Predicate> fields) {
		return fields.keySet()
				.stream()
				.map(fieldName -> getQueryFragment(fieldName, fields.get(fieldName)))
				.collect(Collectors.joining(" "));
	}

	private String getQueryFragment(String fieldName, Predicate predicate) {
		String prefix = predicate.getOp().equals("!=") ? "NOT " : "";
		String fragment = String.format("%s%s = ", prefix, fieldName.replaceAll("\\.", FieldMapper.NESTED_OBJECT_DELIMITER));
		if (predicate.getValue() instanceof List) {
			List<String> values = (List<String>) predicate.getValue();
			return String.format("%s(%s)", fragment, String.join(" OR ", values));
		}

		return fragment + predicate.getValue();
	}

	private Index getIndex(String name) {
		return searchService.getIndex(IndexSpec.newBuilder().setName(name).build());
	}

}

package com.threewks.gae.searchservice;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.threewks.gae.searchservice.DateFieldFormatter.getDate;
import static com.threewks.gae.searchservice.DateFieldFormatter.isDate;

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

	public int deleteAll(DeleteAllOperation operation) {
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

	public List<String> query(QueryOperation operation) {
		String queryStr = toQuery(operation.getFields());
		System.out.println("queryString " + queryStr);

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
			builder.setSortOptions(SortOptions.newBuilder().addSortExpression(sortExpression).setLimit(200).build());
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
		String operator = predicate.getOp() != null && !predicate.getOp().equals("!=") ? predicate.getOp() : "=";


		String fragment = String.format("%s%s %s ", prefix, fieldName.replaceAll("\\.", FieldMapper.NESTED_OBJECT_DELIMITER), operator);
		if (predicate.getValue() instanceof List) {
			List<String> values = (List<String>) predicate.getValue();
			String valueFragment = values.isEmpty() ? "__EMPTY_LIST_MASSIVE_HACK__" : String.join(" OR ", values);
			return String.format("%s(%s)", fragment, valueFragment);
		}

		return fragment + getPredicateValue(predicate.getValue());
	}

	private Object getPredicateValue(Object value) {
		if (isDate(value)){
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			return simpleDateFormat.format(getDate(value));
		}
		return value;
	}

	private Index getIndex(String name) {
		return searchService.getIndex(IndexSpec.newBuilder().setName(name).build());
	}

}

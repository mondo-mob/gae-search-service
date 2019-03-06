package com.example.appengine.java8;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchService;
import com.google.appengine.api.search.SearchServiceFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchServiceImpl {

	private final SearchService searchService;

	public SearchServiceImpl() {
		this.searchService = SearchServiceFactory.getSearchService();
	}

	public void index(com.example.appengine.java8.IndexOperation operation) {
		List<Document> documents = operation.getEntries()
				.stream()
				.map(indexEntry -> {
					Document.Builder docBuilder = Document.newBuilder();
					docBuilder.setId(indexEntry.getId());

					List<Field> fields = toFields(indexEntry.getFields());
					fields.forEach(docBuilder::addField);

					return docBuilder.build();
				})
				.collect(Collectors.toList());

		Index index = getIndex(operation.getEntityName());
		index.put(documents);
	}

	private List<Field> toFields(Map<String, String> fields) {
		return fields.keySet()
				.stream()
				.map(fieldName -> {
					Field.Builder fieldBuilder = Field.newBuilder();
					fieldBuilder.setName(fieldName);
					fieldBuilder.setText(fields.get(fieldName));
					return fieldBuilder.build();
				})
				.collect(Collectors.toList());
	}

	public List<String> query(com.example.appengine.java8.QueryOperation operation) {
		String query = toQuery(operation.getFields());

		Index index = getIndex(operation.getEntityName());
		Results<ScoredDocument> results = index.search(query);
		return results.getResults()
				.stream()
				.map(Document::getId)
				.collect(Collectors.toList());
	}

	private String toQuery(Map<String, Object> fields) {
		return fields.keySet()
				.stream()
				.map(fieldName -> getQueryFragment(fieldName, fields.get(fieldName)))
				.collect(Collectors.joining(" "));
	}

	private String getQueryFragment(String fieldName, Object targetValue) {
		if (targetValue instanceof List) {
			List<String> values = (List<String>) targetValue;
			return fieldName + " = " + StringUtils.join(values, " OR ");
		}

		return fieldName + " = " + targetValue;
	}

	private Index getIndex(String name) {
		return searchService.getIndex(IndexSpec.newBuilder().setName(name).build());
	}

}

package com.mondomob.gae.searchservice;

import com.google.appengine.api.search.Field;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FieldMapperTest {

	private FieldMapper mapper = new FieldMapper();

	private String payload = "{\n" +
			"        \"orgId\": \"MYORG\",\n" +
			"        \"status\": \"Processed\",\n" +
			"        \"accountId\": null,\n" +
			"        \"approvals\": [\n" +
			"          {\n" +
			"            \"approver\": \"approver1@example.com\",\n" +
			"            \"time\": \"2019-04-01T07:06:16.654Z\",\n" +
			"            \"outcome\": \"Approved\"\n" +
			"          },\n" +
			"          {\n" +
			"            \"approver\": \"approver2@example.com\",\n" +
			"            \"time\": \"2019-04-01T07:06:04.896Z\",\n" +
			"            \"outcome\": \"Approved\"\n" +
			"          }\n" +
			"        ],\n" +
			"        \"audit\": {\n" +
			"          \"created\": {\n" +
			"            \"time\": \"2019-04-01T07:05:48.264Z\",\n" +
			"            \"user\": \"creator@example.com\"\n" +
			"          },\n" +
			"          \"modified\": {\n" +
			"            \"time\": \"2019-04-01T07:05:48.264Z\",\n" +
			"            \"user\": \"creator@example.com\"\n" +
			"          }\n" +
			"        },\n" +
			"        \"paymentDate\": \"2019-03-31T13:00:00.000Z\",\n" +
			"        \"account\": {\n" +
			"          \"active\": true,\n" +
			"          \"balance\": 1234.56,\n" +
			"          \"code\": \"NAB\",\n" +
			"          \"expiry\": null\n" +
			"        },\n" +
			"        \"members\": [\n" +
			"          \"user-1\",\n" +
			"          \"user-2\",\n" +
			"          null,\n" +
			"          \"user-3\"\n" +
			"        ]\n" +
			"      }";

	@Test
	public void shouldMap() {
		Gson gson = new GsonBuilder().create();
		Map<String, Object> fieldsInput = gson.fromJson(payload, Map.class);

		List<Field> fields = mapper.map(fieldsInput);
		System.out.println(fields);

		assertThat(fields.size(), is(19));
		assertField(fields.get(0), "orgId", "MYORG");
		assertField(fields.get(1), "status", "Processed");
		assertField(fields.get(2), "approvals__approver", "approver1@example.com");
		assertField(fields.get(3), "approvals__time", "2019-04-01T07:06:16.654Z");
		assertField(fields.get(4), "approvals__outcome", "Approved");
		assertField(fields.get(5), "approvals__approver", "approver2@example.com");
		assertField(fields.get(6), "approvals__time", "2019-04-01T07:06:04.896Z");
		assertField(fields.get(7), "approvals__outcome", "Approved");
		assertField(fields.get(8), "audit__created__time", "2019-04-01T07:05:48.264Z");
		assertField(fields.get(9), "audit__created__user", "creator@example.com");
		assertField(fields.get(10), "audit__modified__time", "2019-04-01T07:05:48.264Z");
		assertField(fields.get(11), "audit__modified__user", "creator@example.com");
		assertThat(fields.get(12).getName(), is("paymentDate"));
		assertThat(fields.get(12).getNumber(), is(1554037.2));
		assertField(fields.get(13), "account__active", "true");
		assertField(fields.get(14), "account__balance", "1234.56");
		assertField(fields.get(15), "account__code", "NAB");
		assertField(fields.get(16), "members", "user-1");
		assertField(fields.get(17), "members", "user-2");
		assertField(fields.get(18), "members", "user-3");
	}

	private void assertField(Field field, String expectedName, String expectedValue) {
		assertThat(field.getName(), is(expectedName));
		assertThat(field.getText(), is(expectedValue));
	}

}

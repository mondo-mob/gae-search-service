package threewks.gae.searchservice;

import com.google.appengine.api.search.Field;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.threewks.gae.searchservice.FieldMapper;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FieldMapperTest {

	private FieldMapper mapper = new FieldMapper();

	private String payload = "{"
			+ "  \"orgId\": \"TIDSWELL\","
			+ "  \"fundId\": \"fund123\","
			+ "  \"level2\": {"
			+ "    \"level2Key\": \"level2Value\""
			+ "  },"
			+ "  \"level3\": {"
			+ "    \"created\": {"
			+ "      \"user\": \"glenn\","
			+ "      \"time\": \"blahhh\""
			+ "    }"
			+ "  }"
			+ "}";

	@Test
	public void shouldMap() {
		Gson gson = new GsonBuilder().create();
		Map<String, Object> fieldsInput = gson.fromJson(payload, Map.class);

		List<Field> fields = mapper.map(fieldsInput);
		System.out.println(fields);

		assertThat(fields.size(), is(5));
		assertField(fields.get(0), "orgId", "TIDSWELL");
		assertField(fields.get(1), "fundId", "fund123");
		assertField(fields.get(2), "level2__level2Key", "level2Value");
		assertField(fields.get(3), "level3__created__user", "glenn");
		assertField(fields.get(4), "level3__created__time", "blahhh");
	}

	private void assertField(Field field, String expectedName, String expectedValue) {
		assertThat(field.getName(), is(expectedName));
		assertThat(field.getText(), is(expectedValue));
	}

}

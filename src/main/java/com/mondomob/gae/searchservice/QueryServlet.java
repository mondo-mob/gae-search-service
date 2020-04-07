package com.mondomob.gae.searchservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "Query", value = "/query")
public class QueryServlet extends HttpServlet {

	private final Gson gson;
	private final SearchServiceImpl searchService;

	public QueryServlet() {
		this.gson = new GsonBuilder().create();
		this.searchService = new SearchServiceImpl();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

		QueryOperation operation = gson.fromJson(body, QueryOperation.class);

		QueryResults results = searchService.query(operation);

		response.setContentType("application/json");
		response.setStatus(200);
		response.getWriter().write(gson.toJson(results));
	}

}

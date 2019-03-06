package com.example.appengine.java8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "Query", value = "/query")
public class QueryServlet extends HttpServlet {

	private final Gson gson;
	private final com.example.appengine.java8.SearchServiceImpl searchService;

	public QueryServlet() {
		this.gson = new GsonBuilder().create();
		this.searchService = new com.example.appengine.java8.SearchServiceImpl();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

		com.example.appengine.java8.QueryOperation operation = gson.fromJson(body, com.example.appengine.java8.QueryOperation.class);

		List<String> ids = searchService.query(operation);

		response.setContentType("application/json");
		response.setStatus(200);
		response.getWriter().write(gson.toJson(ids));
	}

}

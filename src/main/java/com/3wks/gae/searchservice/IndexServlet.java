
package com.example.appengine.java8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "Index", value = "/index")
public class IndexServlet extends HttpServlet {

	private final Gson gson;
	private final com.example.appengine.java8.SearchServiceImpl searchService;

	public IndexServlet() {
		this.gson = new GsonBuilder().create();
		this.searchService = new com.example.appengine.java8.SearchServiceImpl();
	}

	// TODO all this should be done on a queue (and sequentially - max workers 1 maybe?)

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

		com.example.appengine.java8.IndexOperation operation = gson.fromJson(body, com.example.appengine.java8.IndexOperation.class);

		searchService.index(operation);

		response.setContentType("text/plain");
		response.setStatus(204);
	}

}

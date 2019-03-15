package com.threewks.gae.searchservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "Delete", value = "/delete")
public class DeleteServlet extends HttpServlet {

	private final Gson gson;
	private final SearchServiceImpl searchService;

	public DeleteServlet() {
		this.gson = new GsonBuilder().create();
		this.searchService = new SearchServiceImpl();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

		DeleteOperation operation = gson.fromJson(body, DeleteOperation.class);

		searchService.delete(operation);

		response.setContentType("text/plain");
		response.setStatus(204);
	}

}

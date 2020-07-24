// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;

/** Servlet that returns some comments.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment").addSort("dateTime", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();

    for (Entity entity : results.asIterable()) { 
        String userFirstName = (String) entity.getProperty("firstName");
        String userLastName = (String) entity.getProperty("lastName");
        String dateTime = (String) entity.getProperty("dateTime");
        String userMessage = (String) entity.getProperty("message");

        Comment userComment = new Comment(userFirstName, userLastName, dateTime, userMessage);
        comments.add(userComment);
    }
    
    response.setContentType("application/json;");
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Entity commentEntity = new Entity("Comment");
    String userFirstName = getParameter(request, "userFirstName", /* defaultValue= */ "");
    String userLastName = getParameter(request, "userLastName", /* defaultValue= */ "");
    String userMessage = getParameter(request, "userMessage", /* defaultValue= */ "");

    //Get current date and time and convert to String

    //TO DO Add an hour for BST onto comment times to make them correct
    LocalDateTime dateTime = LocalDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    String dateTimeFormatted = dateTime.format(format);
    
    commentEntity.setProperty("firstName", userFirstName);
    commentEntity.setProperty("lastName", userLastName);
    commentEntity.setProperty("dateTime", dateTimeFormatted);
    commentEntity.setProperty("message", userMessage);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    response.sendRedirect("/index.html");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}

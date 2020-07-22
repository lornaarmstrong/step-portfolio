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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> favQuotes;

  @Override
  public void init() {
    favQuotes = new ArrayList<>();
    favQuotes.add(
      " \"And little by little, she found the courage for it all.\" -- JH Hard");
    favQuotes.add(
      "\"Be loud about the things that are important to you.\" -- Karen Walrond");
    favQuotes.add(
      "\"Le monde est un livre dont chaque pas nous ouvre une page.\""
      + " -- Alphonse de Lamartine"
      + "\n *** Translation: The world is a book - with every step, we open a page. ***");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //return the ArrayList as JSON string
    String json = new Gson().toJson(favQuotes);
    response.setContentType("text/html;");
    response.getWriter().println(json);
  }
}

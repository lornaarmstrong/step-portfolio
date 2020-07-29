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

window.onload = loadPage;

function loadPage() {
  getComments();
}

function submitUserForm() {
    var response = grecaptcha.getResponse();
    if(response.length == 0) {
        document.getElementById("g-recaptcha-error").innerHTML = "This field is required";
        return false;
    }
    return true;
}

function verifyCaptcha() {
    document.getElementById("g-recaptcha-error").innerHTML = "";
}

function getComments(){
  var quantity = document.getElementById('commentCount');
  const request = new Request('/data?quantity=' + quantity.value, {method:'GET'});
  fetch(request).then(response => response.json()).then((messages) => {
    const commentContainer = document.getElementById("comment-container");
    var commentHTML = "";
    if (messages.length == 0){
       commentHTML = commentHTML.concat("<p>There are no comments yet. Why not post one yourself?</p>");
    } else {
      for (var i = 0; i < messages.length; i++){
        commentHTML = commentHTML.concat("<div id = \"user-comment\">");
        commentHTML = commentHTML.concat("<h4 id = \"commentor-name\">" + messages[i]["firstName"] + " " + messages[i]["lastName"] + "</h4>");
        commentHTML = commentHTML.concat("<h5 id = \"comment-date\">" + messages[i]["dateTime"] + "</h5>");
        commentHTML = commentHTML.concat("<p id = \"comment-text\">" + messages[i]["message"] + "</p>");
        commentHTML = commentHTML.concat("</div>");
      }
    }
    commentContainer.innerHTML = commentHTML;
  });
}

function deleteComments() {
  const request = new Request('/delete-all-data', {method: 'POST'}); 
  fetch(request).then(getComments());
}

//CHARTS
google.charts.load('current', {'packages':['geochart'],
'mapsApiKey': 'AIzaSyCP-FXbugyYntkSsiE4hQqIAEKh4Li8_ow'
});
  google.charts.setOnLoadCallback(drawRegionsMap);

  function drawRegionsMap() {

    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Country'); 
    data.addColumn('number', 'Value');
    data.addColumn('number', 'Size');
    data.addColumn({type:'string', role:'tooltip'});

    data.addRows([
      ["Edinburgh", 1, 70, "University of Edinburgh"],
      ["Darlington", 2, 70, "Queen Elizabeth Sixth Form College"],
      ["Staindrop", 3, 70, "Staindrop Academy"]
    ]);

    var options = {
        region: 'GB',
        displayMode: 'markers',
        colorAxis: {colors: ['#b85357', '#8774ab', '#327fc7']},
        legend: 'none'
    };

    var chart = new google.visualization.GeoChart(document.getElementById('regions_div'));
    chart.draw(data, options);
}

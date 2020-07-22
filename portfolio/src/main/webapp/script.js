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

/**
 * Fetches greeting from the server and adds it to the DOM.
 */
function fetchAndShowFavQuote() {
  console.log('Fetching a favourite quote.');
  const responsePromise = fetch('/data');
  responsePromise.then(handleResponse);
}

/**
 * Handles response by converting it to text and passing the result to
 * addGreetingToDom().
 */
function handleResponse(response) {
  console.log('Handling the response.');
  const textPromise = response.text();
  textPromise.then(addFavQuoteToDom);
}

/** Adds the quote to the DOM. */
function addFavQuoteToDom(quote) {
  console.log('Adding quote: ' + quote);
  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;
}

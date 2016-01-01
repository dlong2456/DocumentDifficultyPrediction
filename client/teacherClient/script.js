//Web socket functionality
var name = "Nikki Raske";
var status = "Insurmountable Difficulty";
var ws = new WebSocket("ws://127.0.0.1:8080/");

ws.onopen = function() {
	ws.send("Message Received");
};

ws.onmessage = function (evt) {
	alert("Message Received");
};

ws.onclose = function() {
	alert("closed");
};

ws.onerror = function(err) {
};

// Your Client ID can be retrieved from your project in the Google
// Developer Console, https://console.developers.google.com
var CLIENT_ID = '684709411950-0vsqji3pcrjtmnbeg9cl95hnqje0otg4.apps.googleusercontent.com';

var SCOPES = ['https://www.googleapis.com/auth/drive'];

/**
* Check if current user has authorized this application.
*/
function checkAuth() {
gapi.auth.authorize(
  {
    'client_id': CLIENT_ID,
    'scope': SCOPES.join(' '),
    'immediate': true
  }, handleAuthResult);
}

/**
* Handle response from authorization server.
*
* @param {Object} authResult Authorization result.
*/
function handleAuthResult(authResult) {
var authorizeDiv = document.getElementById('authorize-div');
if (authResult && !authResult.error) {
  // Hide auth UI, then load client library.
  authorizeDiv.style.display = 'none';
  loadDriveApi();
} else {
  // Show auth UI, allowing the user to initiate authorization by
  // clicking authorize button.
  authorizeDiv.style.display = 'inline';
}
}

/**
* Initiate auth flow in response to user clicking authorize button.
*
* @param {Event} event Button click event.
*/
function handleAuthClick(event) {
gapi.auth.authorize(
  {client_id: CLIENT_ID, scope: SCOPES, immediate: false},
  handleAuthResult);
return false;
}

/**
* Load Drive API client library.
*/
function loadDriveApi() {
gapi.client.load('drive', 'v2', listFiles);
document.getElementById('comment-button').onclick = function() {
  var content = document.getElementById('comment-content').value;
  insertComment('1HZYIkP_iUM4UyvjYxMaddiRFbBR17xwIeXmHO4RMw40', content);
};
}

/**
* Print files.
*/
function listFiles() {
var request = gapi.client.drive.files.list({
    'maxResults': 10
  });

  request.execute(function(resp) {
    appendPre('Files:');
    var files = resp.items;
    if (files && files.length > 0) {
      for (var i = 0; i < files.length; i++) {
        var file = files[i];
        appendPre(file.title + ' (' + file.id + ')');
      }
    } else {
      appendPre('No files found.');
    }
  });
}

/**
* Insert a new document-level comment.
*
* @param {String} fileId ID of the file to insert comment for.
* @param {String} content Text content of the comment.
*/
function insertComment(fileId, content) {
var body = {'content': content};
var request = gapi.client.drive.comments.insert({
  'fileId': fileId,
  'resource': body
});
request.execute(function(resp) { });
}
/**
* Append a pre element to the body containing the given message
* as its text node.
*
* @param {string} message Text to be placed in pre element.
*/
function appendPre(message) {
var pre = document.getElementById('output');
var textContent = document.createTextNode(message + '\n');
pre.appendChild(textContent);
}
var insertCommands = [];
var insertTimestamps = [];
var deleteCommands = [];
var deleteTimestamps = [];
var styleCommands = [];
var styleTimestamps = [];

chrome.webRequest.onBeforeRequest.addListener(
  function(request) {
      if (request.url.indexOf('/save?') != -1) {
        var requestBody = request.requestBody;
        var docId = request.url.match("docs\.google\.com\/document\/d\/(.*?)\/save")[1];

        var data = {
          "bundles": requestBody.formData.bundles,
          "timeStamp" : parseInt(request.timeStamp, 10)
        };
        parseData(data);
      }
    },
    { urls: ["*://docs.google.com/*"] },
    ['requestBody']
);

function parseData(data) {
  var jsonData = JSON.parse(data.bundles[0]);
  var commands = jsonData[0].commands;
  var timeStamp = data.timeStamp;
  console.log(commands);
  //it would be better to parse ei/si/ty and then parse within those categories
  for (var key in commands[0]) {
    if (commands[0].hasOwnProperty(key)) {
      var val = commands[0][key];
      console.log(val);
      if (val === 'is') {
        //insert

        insertTimestamps.push(timeStamp);
      } else if (val === 'ds') {
        //delete
        deleteTimestamps.push(timeStamp);
      } else if (val === 'as') {
        //style
        styleTimestamps.push(timeStamp);
      } else if (val == 'ei') {
        //end index for delete
      } else if (val === 'si') {
        //start index for delete
      } else if (val === 'ibi') {
        //index of insertion
      } else if (val === 's') {
        //what was inserted
        insertCommands.push(val);
      }
    }
  }
}
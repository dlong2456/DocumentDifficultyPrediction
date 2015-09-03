var insertCommands = [];
var deleteCommands = [];
var styleCommands = [];
var navigationCommands = [];

//Defining command objects
//should I make properties private and make getters/setters like in Java? 
var DeleteCommand = function (timestamp, startIndex, endIndex) {
  this.timeStamp = timeStamp;
  this.startIndex = startIndex;
  this.endIndex = endIndex;
};

var InsertCommand = function (timestamp, index, content) {
  this.timeStamp = timestamp;
  this.index = index;
  this.content = content;
};

var StyleCommand = function (timestamp, startIndex, endIndex, type) {
  this.timeStamp = timeStamp;
  this.startIndex = startIndex;
  this.endIndex = endIndex;
  this.type = type;
};

var NavigationCommand = function(timestamp) {
  this.timeStamp = timestamp;
};

//Listen for insert, style, and delete commands
chrome.webRequest.onBeforeRequest.addListener(
  function(request) {
      if (request.url.indexOf('/save?') != -1) {
        var requestBody = request.requestBody;
        var docId = request.url.match("docs\.google\.com\/document\/d\/(.*?)\/save");
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
  var command;
  console.log(commands);
  //TODO: need to handle 'mlti' commands
  for (var i = 0; i<commands.length; i++) {
    command = commands[i];
    //INSERT          
    if (command.ty === 'is') {
      var insertCommand = new InsertCommand(timeStamp, command.ibi, command.s);
      insertCommands.push(insertCommand);
    //DELETE
    } else if (command.ty === 'ds') {
      console.log('timestamp: ' + timeStamp);
      // var deleteCommand = new DeleteCommand(timeStamp, command.si, command.ei);
      // deleteCommands.push(deleteCommand);
    //STYLE
    } else if (command.ty === 'as') {
      var styleCommand;
      if(command.sm.hasOwnProperty('ts_bd')) {
        styleCommand = new StyleCommand(timestamp, command.si, command.ei, 'bold');
      } else if (command.sm.hasOwnProperty('ts_it')) {
        styleCommand = new StyleCommand(timestamp, command.si, command.ei, 'italics');
      } else if (command.hasOwnProperty('ts_un')) {
        styleCommand = new StyleCommand(timestamp, command.si, command.ei, 'underline');
      } else if (command.hasOwnProperty('ts_bgc')) {
        styleCommand = new StyleCommand(timestamp, command.si, command.ei, 'highlight');
        styleCommand.highlightColor = command.ts_bgc;
      } else if (command.hasOwnProperty('ts_fgc')) {
        styleCommand = new StyleCommand(timestamp, command.si, command.ei, 'font color change');
        styleCommand.fontColor = command.ts_fgc;
      }
      //etc.
      styleCommands.push(styleCommand);
    }
  }
}

//TODO: listen to mouse commands (navigation or focus?) 
//should I listen to all mouse movements? Just scrolling? Just clicking?

//TODO: listen to 'debug mode' -- spellcheck? comments/revisions?  

//Listen for navigation commands (Or is it focus???)
chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
  var navigationCommand = new NavigationCommand(Date.now());
  navigationCommands.push(navigationCommand);
});

chrome.tabs.onActivated.addListener(function(tabId, changeInfo, tab) {
  var navigationCommand = new NavigationCommand(Date.now());
  navigationCommands.push(navigationCommand);
  console.log(navigationCommands);
});

chrome.tabs.onCreated.addListener(function(tabId, changeInfo, tab) {
  var navigationCommand = new NavigationCommand(Date.now());
  navigationCommands.push(navigationCommand);
});

//Listening for message from content.js
// chrome.runtime.onMessage.addListener(function(request) {
//   console.log('hello');
// });
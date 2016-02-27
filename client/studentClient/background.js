var insertCommands = [];
var deleteCommands = [];
var boldCommands = [];
var italicizeCommands = [];
var highlightCommands = [];
var underlineCommands = [];
var scrollCommands = [];
var createNewTabCommands = [];
var switchTabCommands = [];
var updateURLCommands  = [];
var spellcheckCommands = [];
var collaborationCommands = [];
var numberOfCommands = 0;

//MESSAGE PASSING

// Sending message to content.js
sendToContent = function(message) {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
    chrome.tabs.sendMessage(tabs[0].id, {message: message}, function(response) {
    });
  });
};

// Listening for message from content.js
chrome.runtime.onMessage.addListener(function(request) {
  //Spellcheck commands
  if(request.type === "spellcheck_change" || request.type === "spellcheck_ignore" || request.type === "spellcheck_dictionary") {
    var spellcheckCommand = new SpellcheckCommand(request.timestamp, request.type);
    spellcheckCommands.push(spellcheckCommand);
    newCommand();
  //Status updates
  } else if (request.type === "statusUpdate") {
      var statusUpdateObject = {
        type: "statusUpdate",
        makingProgress: request.makingProgress,
        difficultyType: request.difficultyType,
        details: request.details
      };
      ws.send(JSON.stringify(statusUpdateObject));
  //Scroll commands
  } else if (request.type === "scroll") {
      var scrollCommand = new ScrollCommand(request.timestamp);
      scrollCommands.push(scrollCommand);
      newCommand();
  } 
});

//Web socket functionality
var ws = new WebSocket("ws://127.0.0.1:8080/");

ws.onopen = function() {
};

ws.onmessage = function (evt) {
    var data = evt.data;
    var json = JSON.stringify(eval("(" + data + ")"));
    var jsonData = JSON.parse(json);
    if (jsonData.hasOwnProperty("status")) {
      sendToContent(jsonData.status);
    }
};

ws.onclose = function() {
};

ws.onerror = function(err) {
};


function newCommand() {
  numberOfCommands++;
  if (numberOfCommands >= 10) {
    var commandObject = {
      type: "command",
      insertCommands : insertCommands,
      deleteCommands : deleteCommands,
      boldCommands : boldCommands,
      scrollCommands : scrollCommands,
      spellcheckCommands: spellcheckCommands,
      collaborationCommands: collaborationCommands, 
      italicizeCommands : italicizeCommands, 
      highlightCommands : highlightCommands, 
      underlineCommands : underlineCommands, 
      updateURLCommands : updateURLCommands, 
      createNewTabCommands : createNewTabCommands,
      switchTabCommands : switchTabCommands
    };
    ws.send(JSON.stringify(commandObject));
    numberOfCommands = 0;
    insertCommands = [];
    deleteCommands = [];
    styleCommands = [];
    navigationCommands = [];
    collaborationCommands = [];
    spellcheckCommands = [];
  }
}

//Listen for insert, style, and delete commands
chrome.webRequest.onBeforeRequest.addListener(
  function(request) {
      if (request.url.indexOf('/save?') != -1) {
        var requestBody = request.requestBody;
        // var docId = request.url.match("docs\.google\.com\/document\/d\/(.*?)\/save")[1];
        var data = {
          "bundles": requestBody.formData.bundles,
          "timeStamp" : parseInt(request.timeStamp, 10)
        };
        parseData(data);
      } else if (request.url.indexOf('/sync?') != -1) {
        //this is a suggested revision or a comment
        var collaborationCommand = new CollaborationCommand(Date.now());
        collaborationCommands.push(collaborationCommand);
        newCommand();
      }
    },
    { urls: ["*://*.google.com/*"] },
    ['requestBody']
);

//Defining command objects
var CollaborationCommand = function(timeStamp) {
  this.timeStamp = timeStamp;
};

var SpellcheckCommand = function(timeStamp, type) {
  this.timeStamp = timeStamp;
  this.type = type;
};

var DeleteCommand = function (timestamp, startIndex, endIndex) {
  this.timeStamp = timestamp;
  this.startIndex = startIndex;
  this.endIndex = endIndex;
};

var InsertCommand = function (timestamp, index, content) {
  this.timeStamp = timestamp;
  this.index = index;
  this.content = content;
};

var StyleCommand = function (timestamp, startIndex, endIndex, type) {
  this.timeStamp = timestamp;
  this.startIndex = startIndex;
  this.endIndex = endIndex;
  this.type = type;
};

var ScrollCommand = function(timeStamp) {
  this.timeStamp = timeStamp;
};

var CreateNewTabCommand = function(timeStamp) {
  this.timeStamp = timeStamp;
};

var UpdateURLCommand = function(timeStamp) {
  this.timeStamp = timeStamp;
};

var SwitchTabCommand = function(timeStamp) {
  this.timeStamp = timeStamp;
};

function parseData(data) {
  var jsonData = JSON.parse(data.bundles[0]);
  var commands = jsonData[0].commands;
  var timeStamp = data.timeStamp;
  var command;

  for (var i = 0; i<commands.length; i++) {
    command = commands[i];
    //INSERT          
    processCommands(command, timeStamp);
    if (command.ty === 'mlti') {
      var mltiCommands = command.mts;
      for(i = 0; i < mltiCommands.length; i++) {
        processCommands(mltiCommands[i], timeStamp);
      }
    }
  }
}

function processCommands(command, timeStamp) {
  //INSERT          
  if (command.ty === 'is') {
    var insertCommand = new InsertCommand(timeStamp, command.ibi, command.s);
    insertCommands.push(insertCommand);
    newCommand();
  //DELETE
  } else if (command.ty === 'ds') {
    var deleteCommand = new DeleteCommand(timeStamp, command.si, command.ei);
    deleteCommands.push(deleteCommand);
    newCommand();
  //STYLE
  } else if (command.ty === 'as') {
    //TODO: distinguish between bold and unbold, etc.
    var styleCommand;
    if(command.sm.hasOwnProperty('ts_bd_i')) {
      styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'bold');
      boldCommands.push(styleCommand);
    } else if (command.sm.hasOwnProperty('ts_it_i')) {
      styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'italics');
      boldCommands.push(styleCommand);
    } else if (command.sm.hasOwnProperty('ts_un_i')) {
      styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'underline');
      underlineCommands.push(styleCommand);
    } else if (command.sm.hasOwnProperty('ts_bgc_i')) {
      styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'highlight');
      styleCommand.highlightColor = command.ts_bgc;
      highlightCommands.push(styleCommand);
    } else if (command.sm.hasOwnProperty('ts_fgc_i')) {
      //TODO: handle this and send to server
      // styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'font color change');
      // styleCommand.fontColor = command.ts_fgc;
    }
    //etc.
    if (styleCommand) {
      newCommand();
    }
  }
}

//on typing new URL in a tab or reloading page
chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
  var url = tab.url;
  if (url !== undefined && changeInfo.status == "complete") {
    var updateURLCommand = new UpdateURLCommand(Date.now());
    updateURLCommands.push(updateURLCommand);
    newCommand();
  }
});

//on switching tabs
chrome.tabs.onActivated.addListener(function(tabId, changeInfo, tab) {
  var switchTabCommand = new SwitchTabCommand(Date.now());
  switchTabCommands.push(switchTabCommand);
  newCommand();
});

//on creating a tab (calls update and activate also)
chrome.tabs.onCreated.addListener(function(tabId, changeInfo, tab) {
  var createNewTabCommand = new CreateNewTabCommand(Date.now());
  createNewTabCommands.push(createNewTabCommand);
  newCommand();
});
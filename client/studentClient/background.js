var insertCommands = [];
var deleteCommands = [];
var styleCommands = [];
var navigationCommands = [];
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
  }
  if (request.type === "statusUpdate") {
    var statusUpdateObject = {
      type: "statusUpdate",
      makingProgress: request.makingProgress,
      difficultyType: request.difficultyType,
      details: request.details
    }
    ws.send(JSON.stringify(statusUpdateObject));
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
    var commandPercentageObject = {
      type: "commandPercentage",
      insertionPercentage : computeInsertPercentage(),
      deletionPercentage : computeDeletePercentage(),
      navigationPercentage : computeNavigationPercentage(),
      stylePercentage : computeStylePercentage(),
      debugPercentage : computeDebugPercentage()
    };
    var commandObject = {
      type: "command",
      insertCommands : insertCommands,
      deleteCommands : deleteCommands,
      styleCommands : styleCommands,
      navigationCommands : navigationCommands,
      spellcheckCommands: spellcheckCommands,
      collaborationCommands: collaborationCommands
    };
    ws.send(JSON.stringify(commandObject));
    ws.send(JSON.stringify(commandPercentageObject));
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

var NavigationCommand = function(timestamp) {
  this.timeStamp = timestamp;
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
    } else if (command.sm.hasOwnProperty('ts_it_i')) {
      styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'italics');
    } else if (command.sm.hasOwnProperty('ts_un_i')) {
      styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'underline');
    } else if (command.sm.hasOwnProperty('ts_bgc_i')) {
      styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'highlight');
      styleCommand.highlightColor = command.ts_bgc;
    } else if (command.sm.hasOwnProperty('ts_fgc_i')) {
      styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'font color change');
      styleCommand.fontColor = command.ts_fgc;
    }
    //etc.
    if (styleCommand) {
      styleCommands.push(styleCommand);
      newCommand();
    }
  }
}


//on typing new URL in a tab or reloading page
chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
  var url = tab.url;
  if (url !== undefined && changeInfo.status == "complete") {
    var navigationCommand = new NavigationCommand(Date.now());
    navigationCommands.push(navigationCommand);
    newCommand();
  }
});

//on switching tabs
chrome.tabs.onActivated.addListener(function(tabId, changeInfo, tab) {
  var navigationCommand = new NavigationCommand(Date.now());
  navigationCommands.push(navigationCommand);
  newCommand();
});

//on creating a tab (calls update and activate also)
chrome.tabs.onCreated.addListener(function(tabId, changeInfo, tab) {
  var navigationCommand = new NavigationCommand(Date.now());
  navigationCommands.push(navigationCommand);
  newCommand();
});

function computeInsertPercentage() {
  var numberOfInsertEvents = insertCommands.length;
  var numberOfNavigationEvents = navigationCommands.length;
  var numberOfDeleteEvents = deleteCommands.length;
  var numberOfStyleEvents = styleCommands.length;
  var numberOfDebugEvents = spellcheckCommands.length + collaborationCommands.length;
  var insertPercentage = 0;
  if (numberOfInsertEvents > 0) {
    insertPercentage = (numberOfInsertEvents/(numberOfNavigationEvents + numberOfDeleteEvents + numberOfInsertEvents + numberOfStyleEvents + numberOfDebugEvents)) * 100;
  }
  return insertPercentage;
}

function computeDeletePercentage() {
  var numberOfInsertEvents = insertCommands.length;
  var numberOfNavigationEvents = navigationCommands.length;
  var numberOfDeleteEvents = deleteCommands.length;
  var numberOfStyleEvents = styleCommands.length;
  var numberOfDebugEvents = spellcheckCommands.length + collaborationCommands.length;
  var deletePercentage = 0;
  if (numberOfDeleteEvents > 0) {
    deletePercentage = (numberOfDeleteEvents/(numberOfNavigationEvents + numberOfDeleteEvents + numberOfInsertEvents + numberOfStyleEvents + numberOfDebugEvents)) * 100;
  }
  return deletePercentage;
}

function computeNavigationPercentage() {
  var numberOfInsertEvents = insertCommands.length;
  var numberOfNavigationEvents = navigationCommands.length;
  var numberOfDeleteEvents = deleteCommands.length;
  var numberOfStyleEvents = styleCommands.length;
  var numberOfDebugEvents = spellcheckCommands.length + collaborationCommands.length;
  var navigationPercentage = 0;
  if (numberOfNavigationEvents > 0) {
    navigationPercentage = (numberOfNavigationEvents/(numberOfNavigationEvents + numberOfDeleteEvents + numberOfInsertEvents + numberOfStyleEvents + numberOfDebugEvents)) * 100;
  }
  return navigationPercentage;
}

function computeStylePercentage() {
  var numberOfInsertEvents = insertCommands.length;
  var numberOfNavigationEvents = navigationCommands.length;
  var numberOfDeleteEvents = deleteCommands.length;
  var numberOfStyleEvents = styleCommands.length;
  var numberOfDebugEvents = spellcheckCommands.length + collaborationCommands.length;
  var stylePercentage = 0;
  if (numberOfStyleEvents > 0) {
    stylePercentage = (numberOfStyleEvents/(numberOfNavigationEvents + numberOfDeleteEvents + numberOfInsertEvents + numberOfStyleEvents + numberOfDebugEvents)) * 100;
  }
  return stylePercentage;
}

function computeDebugPercentage() {
  var numberOfInsertEvents = insertCommands.length;
  var numberOfNavigationEvents = navigationCommands.length;
  var numberOfDeleteEvents = deleteCommands.length;
  var numberOfStyleEvents = styleCommands.length;
  var numberOfDebugEvents = spellcheckCommands.length + collaborationCommands.length;
  var debugPercentage = 0;
  if (numberOfDebugEvents > 0) {
    debugPercentage = (numberOfDebugEvents/(numberOfNavigationEvents + numberOfDeleteEvents + numberOfInsertEvents + numberOfStyleEvents + numberOfDebugEvents)) * 100;
  }
  return debugPercentage;
}
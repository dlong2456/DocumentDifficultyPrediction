//INITIALIZE VARIABLES 

var insertCommands = [];
var deleteCommands = [];
var boldCommands = [];
var italicizeCommands = [];
var highlightCommands = [];
var underlineCommands = [];
var scrollCommands = [];
var createNewTabCommands = [];
var switchTabCommands = [];
var windowFocusCommands = [];
var updateURLCommands  = [];
var spellcheckCommands = [];
var collaborationCommands = [];
var cursorCommands = [];
var numberOfCommands = 0;
var chromeInFocus = 1;
var documentId;
//This variable is updated when the user first visits a Google Doc after connecting
var documentIdFound = 0;
//This variable is updated when the server is connected and has all messaging systems running
var connected = 0;
//Initialize status to pending so it will show up on Google Doc 
var difficultyStatus = 'pending';
//This is the number of commands we will send in each package to the server
var commandPackageSize = 10;

//List of the docIDs we are using for the study
//This method of recording docIDs will need to be modified if this project is expanded
//Might look into Google Docs API but this was the quickest fix
var docIDList = [
"10eVBvPyYNHGE_xOOoIGfKYkIIrDpg9tUfn8FuSyVKIA",
"1bYdF4RyvYVso2DG8MUMJI_DS-SdCJf3SBE5kbuNdtsM",
"1iCHQpzQQJmhA67N8kKT4ry4m4wpyUShfD98h_LcxjjM"
];

//MESSAGE PASSING

// Sending message to content.js
sendToContent = function(message) {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
      if (typeof tabs !== 'undefined' && tabs.length > 0) {
        chrome.tabs.sendMessage(tabs[0].id, {message: message}, function(response) {
        });
      }
  });
};

// Listening for message from content.js
chrome.runtime.onMessage.addListener(function(request) {
  //Spellcheck commands
  if(request.type === "spellcheck_change" || request.type === "spellcheck_ignore" || request.type === "spellcheck_dictionary") {
    if (documentIdFound === 1 && connected === 1) {
        checkURL(function(docId) {
          if(docId === documentId) {
            var spellcheckCommand = new SpellcheckCommand(request.timestamp, request.type);
            spellcheckCommands.push(spellcheckCommand);
            newCommand();
          }
        });
      }
  //Status updates
  } else if (request.type === "statusUpdate") {
      var statusUpdateObject = {
        type: "statusUpdate",
        facingDifficulty: request.facingDifficulty,
        difficultyType: request.difficultyType,
        details: request.details
      };
      ws.send(JSON.stringify(statusUpdateObject));
  //Scroll commands
  } else if (request.type === "scroll") {
      if (documentIdFound === 1 && connected === 1) {
        checkURL(function(docId) {
          if(docId === documentId) {
            var scrollCommand = new ScrollCommand(request.timestamp);
            scrollCommands.push(scrollCommand);
            newCommand();
          }
        });
      }
  } else if (request.type === "cursor") {
      if (documentIdFound === 1 && connected === 1) {
        checkURL(function(docId) {
          if(docId === documentId) {
            var cursorCommand = new CursorCommand(request.timestamp, request.left, request.top);
            cursorCommands.push(cursorCommand);
            newCommand();
          }
        });
      }
  }
});

//Web socket functionality 
// start("ws://classroom1.cs.unc.edu:5050");
start("ws://127.0.0.1:8080/");

function start(websocketServerLocation) {
  ws = new WebSocket(websocketServerLocation);
  ws.onopen = function() {
  };

  ws.onmessage = function (evt) {
      var data = evt.data;
      if (data === "Connected") {
        connected = 1;
        //Once connected, send a docID. This covers the scenario when the user is already on the doc page when they connect.
        //The checkURL funciton will determine if there is a docID to send. 
        checkURL(sendDocId);
      } else if (data === "handshake") {
        ws.send("student");
      } else {
        var json = JSON.stringify(eval("(" + data + ")"));
        var jsonData = JSON.parse(json);
        if (jsonData.hasOwnProperty("status")) {
          difficultyStatus = jsonData.status;
          sendToContent(jsonData.status);
        }
      }
  };

  ws.onclose = function() {
    //Change status back to pending since socket is closing
    difficultyStatus = 'pending';
    //Notify content.js that the socket has closed
    sendToContent("close");
    //set documentIdFound to false so you can find the next one
    documentIdFound = 0;
    documentId = null;
    //try to reconnect in 5 seconds
    setTimeout(function() {
      start(websocketServerLocation);
    }, 5000);
  };

  ws.onerror = function(err) {
    console.log(err);
  };
}

//This prepares commands and sends them in bundles to the server
function newCommand() {
  numberOfCommands++;
  if (numberOfCommands >= commandPackageSize) {
    //Prepare a JSON object to send to server
    var commandObject = {
      type: "command",
      documentId : documentId,
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
      switchTabCommands : switchTabCommands,
      windowFocusCommands: windowFocusCommands,
      cursorCommands: cursorCommands
    };
    console.log(deleteCommands);
    //Send the object
    ws.send(JSON.stringify(commandObject));
    //Reset all command arrays for the next bundle
    numberOfCommands = 0;
    insertCommands = [];
    deleteCommands = [];
    boldCommands = [];
    scrollCommands = [];
    spellcheckCommands = [];
    collaborationCommands = [];
    italicizeCommands = [];
    highlightCommands = [];
    underlineCommands = [];
    updateURLCommands = [];
    createNewTabCommands = [];
    switchTabCommands = [];
    windowFocusCommands = [];
    cursorCommands = [];
  }
}

//DEFINE COMMAND OBJECTS
var CursorCommand = function(timeStamp, left, top) {
  this.timeStamp = timeStamp;
  this.left = left;
  this.top = top;
};

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

var WindowFocusCommand = function(timeStamp) {
  this.timeStamp = timeStamp;
};

//DETECT AND PARSE GOOGLE WEB REQUESTS

//Listen for insert, style, and delete commands (sent by Google as web requests)
chrome.webRequest.onBeforeRequest.addListener(
  function(request) {
      //All of the Google Docs edit-related web requests start with /save?
      if (request.url.indexOf('/save?') != -1) {
        var requestBody = request.requestBody;
        var data = {
          "bundles": requestBody.formData.bundles,
          "timeStamp" : parseInt(request.timeStamp, 10) //TODO: Maybe I should make my own timestamp here?
        };
        //This is a hack so that we only listen to edit commands on the Google Doc we are interested in
        //I think there is a better way to do this but this was the quickest fix
        if (documentIdFound === 1 && connected === 1) {
          checkURL(function(docId) {
            if(docId === documentId && data != null && data != undefined) {
              parseData(data);
            }
          });
        }
      } else if (request.url.indexOf('/sync?') != -1) {
        //this is a suggested revision or a comment
        if (documentIdFound === 1 && connected === 1) {
          checkURL(function(docId) {
            if(docId === documentId) {
              var collaborationCommand = new CollaborationCommand(Date.now());
              collaborationCommands.push(collaborationCommand);
              newCommand();
            }
          });
        }
      }
    },
    { urls: ["*://*.google.com/*"] },
    ['requestBody']
);

function parseData(data) {
  var jsonData = JSON.parse(data.bundles[0]);
  var commands = jsonData[0].commands;
  var timeStamp = data.timeStamp;
  var command;
  for (var i = 0; i<commands.length; i++) {
    command = commands[i];
    processCommands(command, timeStamp);
    //Large edits consisting of multiple commands come packaged labeled with the type "mlti"
    //Here I break down the bundle and parse each command individually
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
    //NOTE: These are all disabled right now because they fire unpredictably during paste commands and it throws off the ratios
    //TODO: distinguish between bold and unbold, etc.
    var styleCommand;
    //Note: these all need to be of the format ts_.. not ts_.._i because otherwise they show
    //up in paste/large insert mlti commands as well and it throws off the ratios
    if(command.sm.hasOwnProperty('ts_bd')) {
      // styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'bold');
      // boldCommands.push(styleCommand);
    } else if (command.sm.hasOwnProperty('ts_it')) {
      // styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'italics');
      // italicizeCommands.push(styleCommand);
    } else if (command.sm.hasOwnProperty('ts_un')) {
      // styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'underline');
      // underlineCommands.push(styleCommand);
    } else if (command.sm.hasOwnProperty('ts_bgc')) {
      //Don't count white highlights because these show up in paste/large insert mlti commands as well
      // if (command.sm.ts_bgc !== "#ffffff") {
      //   styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'highlight');
      //   styleCommand.highlightColor = command.sm.ts_bgc;
      //   highlightCommands.push(styleCommand);
      // }
    } else if (command.sm.hasOwnProperty('ts_fgc')) {
      //TODO: handle this and send to server
      // styleCommand = new StyleCommand(timeStamp, command.si, command.ei, 'font color change');
      // styleCommand.fontColor = command.sm.ts_fgc;
    }
    //etc.
    if (styleCommand) {
      newCommand();
    }
  }
}

//PROCESS URL AND PAGE UPDATES

//Function called on typing new URL in a tab 
chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
  //If statement prevents commands from firing on refresh or iframe load
  if (changeInfo.url !== undefined) {
    if (documentIdFound === 1 && connected === 1) {
      var updateURLCommand = new UpdateURLCommand(Date.now());
      updateURLCommands.push(updateURLCommand);
      newCommand();
    }
  } else {
    //On refresh, make sure to resend the status to content.js
    sendToContent(difficultyStatus);
    //Check for the document ID if it hasn't already been found
    if(documentIdFound === 0 && connected === 1) {
      checkURL(sendDocId);
    }
  }
});

//Function called on switching tabs
chrome.tabs.onActivated.addListener(function(tabId, changeInfo, tab) {
  //Check for the document ID if it hasn't already been found
  if(documentIdFound === 0 && connected === 1) {
    checkURL(sendDocId);
  }
  if (documentIdFound === 1 && connected === 1) {
    var switchTabCommand = new SwitchTabCommand(Date.now());
    switchTabCommands.push(switchTabCommand);
    newCommand();
  }
});

//Function called on creating a tab (calls update and activate also)
chrome.tabs.onCreated.addListener(function(tabId, changeInfo, tab) {
  //Check for the document ID if it hasn't already been found
  if(documentIdFound === 0 && connected === 1) {
    checkURL(sendDocId);
  }
  if (documentIdFound === 1 && connected === 1) {
    var createNewTabCommand = new CreateNewTabCommand(Date.now());
    createNewTabCommands.push(createNewTabCommand);
    newCommand();
  }
});

//Function polls every 1000 ms (?) to detect if the Chrome browser is currently in focus
window.setInterval(checkBrowserFocus, 1000);
function checkBrowserFocus() {
  chrome.windows.getCurrent(function(browser) {
    //Only log an event when a change in focus occurs
    if (browser.focused != chromeInFocus) {
      chromeInFocus = browser.focused;
      if (documentIdFound === 1 && connected === 1) {
        var windowFocusCommand = new WindowFocusCommand(Date.now());
        windowFocusCommands.push(windowFocusCommand);
        newCommand();
      }
    }
  });
}

//DOCUMENT ID PROCESSING FUNCTIONALITY

//Detects when the user is on a Google Docs tab.
function checkURL(cb) {
  chrome.tabs.getSelected(null, function(tab) {
    if(/^(https:\/\/docs\.google\.com)/.test(tab.url)) {
      var match = tab.url.match(/^(https:\/\/docs\.google\.com\/document\/d\/(.*?)\/edit)/);
      documentId = match[2];
      //This is a hack in case the first document the user visits is not the one we are interested in tracking
      //There is probably a better way to do this but this was the quickest fix for now
      for (var i = 0; i < docIDList.length; i++) {
        if (documentId === docIDList[i]) {
          //Set this so we don't have to keep polling now that we have found the ID
          documentIdFound = 1;
          cb(documentId);
        }
      }
    }
  });
}

//Sends the document ID to the server
function sendDocId(docId) {
  if (docId !== null && docId !== undefined) {
    var numericDocId = getNumericValFromDocId(docId);
    var docIdObject = {
      type: "documentId",
      documentId: numericDocId,
      documentIdString: docId
    };
    //Send Document ID to server
    ws.send(JSON.stringify(docIdObject));
  }
}

//Maps document ID strings to simple ID numbers
//Aside from readability, this is also so the ID can be logged 
//as a timestamp (type long) on the server end
function getNumericValFromDocId(docId) {
  switch(docId) {
    case  "10eVBvPyYNHGE_xOOoIGfKYkIIrDpg9tUfn8FuSyVKIA":
      return 1;
    case "1bYdF4RyvYVso2DG8MUMJI_DS-SdCJf3SBE5kbuNdtsM":
      return 2;
    case "1iCHQpzQQJmhA67N8kKT4ry4m4wpyUShfD98h_LcxjjM":
      return 3;
    default:
        //This should never be called
        console.log("Default case called");
        return 0;
  }
}
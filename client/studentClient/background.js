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
var documentIdFound = 0;
//Initialize status to pending so it will show up on Google Doc 
var difficultyStatus = 'pending';
//This is the number of commands we will send in each package to the server
var commandPackageSize = 10;

//List of the docIDs we are using for the study
//This method of recording docIDs will need to be modified if this project is expanded
//Might look into Google Docs API but this was the quickest fix
var docIDList = [
"1utnT3bYBZJf_M3NFb7CBVqpop4pIdR_AnZBo6pcjr8s",
"1cEe3hOc6hOQZbYrL1ojlIIPWqChNTIaP-rxDOhqVY4o",
"1znDjpT1DmRL5d55UK9GXhoicymICt7CE7lBmpyeEE-Y",
"16_j0WNLvvVlSUG0LcSCc_PRbmN0X8SpNUAI5gQ6DyiI",
"1PsNChRRfgorPLDNCls8n3IYGu0bq0aCQ2vK7hCOqmNA",
"1ZtHqc9NGtQ55E38xCzZBBCvNgAz2nWwrhvdf6-2eqPg",
"1R_42ciqJGjKPYLK8NX8VA2qnbw-LUSqjEnWjv-KtjG4",
"1YSSmozS3A-RBxzR-dQBgiz5dq4B0a1f9d8Kgunt0dmw",
"1_DqsfW4_v_SeNZpNRYRDnYnEDVwnRLPmbw9MIWvDkIk",
"1Zb55vT7OU27w9vPZdYNIbyBVbrsLuCSnnLsAG9nAsmA",
"1F7Oz6IVRmHgPKV1PvWkHlChtcaYS4J_XDT-CfzbBicw",
"173XESj7nTT3AtWOzR93BC-md5kDWalNED7hGC6MSrkg",
"1sqsPUagt8R_luTV6BXGRu51_d08T3Oh-dipgsVNE9dc",
"1jWTEgxOhUBkQx4qBd6Z91i6yib1R6mzfT3a_cOroUo4",
"10a6Kh-E5F2FnKew-I47bHTvIKdvC0EiCCyrcd2AUuEk",
"11vxDHg7KKZYjIi69jdlgEFXrcdNOUqmAoyoxIJCaDJY",
"1eCScFpbcEeqtJUyyy63boQ2AOIzfSP9XmXVkfMFiu7g",
"1ZXOHpZ8ez0m6efKIZ5M7li2mieQUrs4jsnx9JQm_AxY",
"1Obo7N7IPu2-muLJ43TjvAPy7v23W5LvFdMG1N-E-tzE",
"1FHM0P74zOzqiMSgghiMeb5mIDhjWj3z9qlK_K2k8C98",
"1aR6K2ld_Hn3fGq6wOaOQTp_vmvPlekhisGSxLOu9DkE",
"1t68zyyf6VWgkzWo50TO_yEzN_FUE56EO_3JlT7zz3Q0",
"1DSlcBm_NYHSZmmGWqk43xwex_Yo8M72M1VZ9ejMDCY0",
"1fc4WLpuRFP4fP6uwr0JmIYat9eFfR6lZJ3lHz-0steA",
"15z4a9HvKUYps56MARXffWTlVMFzcaVlP5vwLRvapEWM",
"1CEE4iEuw-6liGOrCOrv9cT68LigNoJDeIsqDYYVtjU0",
"1QykAhwwRunhp6QPEhaHSfxtqSAq0J7sdwiaf5JH5its",
"1kWQCeaWIRRsqbApb3zLrZm0qCRWqfDk48ewaIVUVnmw",
"1bQKmqyxqHweOanrgABlNUzUcVvyTMrNsaKrrC5g4YaU",
"1GWPnhLkICgXJYpFxyOFtxFquJgFLJ1WhnqKtSMUJ9Hs",
"1_0wSnzPhcZ6Pwd-elcnu-3i2c8VAPdNizSZdKX6yirE",
"1rxLuEdpFa1oIcvK3_YEiRfmC-AwwDsFXlrbf_s00O5Q",
"1ZMe6OhGZfsJWhJ3bHucyMICjBy9icvCaCzPJs3ofcYk",
"1xZONI7xDlb9qdltAdah6p9Ig3PLhOrOX9GtDUU_Mbu4",
"1hVrQjxkcoOkEnDGoclnXymjKizjiWyR6E_P5Py_H7R0",
"1XEyyogKV7lcNUuckPXnP6zKhhmg2zbA30suP7tb4WRk",
"1FSk_Coa86sSxN9LT4rpq4pCUtUjZKbv4_og6rLTWVMg",
"1AM11GlDR899B_1Skmz0s0D7sak-X_b7FVnu1l2PiEHk",
"1nMgO_pEiwpiqPoAOoendaqzu-cwmYW3QOQ9rpKFCQeM",
"15ZFb-5JC97-svNGSN9XhYnxoIrbFBIT0ntmMKCFfkgw"
];

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
        facingDifficulty: request.facingDifficulty,
        difficultyType: request.difficultyType,
        details: request.details
      };
      ws.send(JSON.stringify(statusUpdateObject));
  //Scroll commands
  } else if (request.type === "scroll") {
      var scrollCommand = new ScrollCommand(request.timestamp);
      scrollCommands.push(scrollCommand);
      newCommand();
  } else if (request.type === "cursor") {
    var cursorCommand = new CursorCommand(request.timestamp, request.left, request.top);
    cursorCommands.push(cursorCommand);
    newCommand();
  }
});

//Web socket functionality
var ws = new WebSocket("ws://127.0.0.1:8080/");
// var ws = new WebSocket("ws://classroom1.cs.unc.edu:5050/");

ws.onopen = function() {
};

ws.onmessage = function (evt) {
    var data = evt.data;
    var json = JSON.stringify(eval("(" + data + ")"));
    var jsonData = JSON.parse(json);
    if (jsonData.hasOwnProperty("status")) {
      difficultyStatus = jsonData.status;
      sendToContent(jsonData.status);
    }
};

ws.onclose = function() {
  //Change status back to pending since socket is closing
  difficultyStatus = 'pending';
  //Notify content.js that the socket has closed
  sendToContent("close");
};

ws.onerror = function(err) {
  console.log(err);
};

//This prepares commands and sends them in bundles to the server
function newCommand() {
  numberOfCommands++;
  if (numberOfCommands >= commandPackageSize) {
    //Prepare a JSON object to send to server
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
      switchTabCommands : switchTabCommands,
      windowFocusCommands: windowFocusCommands,
      cursorCommands: cursorCommands
    };
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
        if (documentIdFound === 1) {
          checkURL(function(docId) {
            if(docId === documentId) {
              parseData(data);
            }
          });
        }
      } else if (request.url.indexOf('/sync?') != -1) {
        //this is a suggested revision or a comment
        console.log("CollaborationCommand");
        var collaborationCommand = new CollaborationCommand(Date.now());
        collaborationCommands.push(collaborationCommand);
        newCommand();
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
    //Check for the document ID if it hasn't already been found
    if(documentIdFound === 0) {
      checkURL(sendDocId);
    }
    var updateURLCommand = new UpdateURLCommand(Date.now());
    updateURLCommands.push(updateURLCommand);
    newCommand();
  } else {
    //On refresh, make sure to resend the status to content.js
    sendToContent(difficultyStatus);
  }
});

//Function called on switching tabs
chrome.tabs.onActivated.addListener(function(tabId, changeInfo, tab) {
  //Check for the document ID if it hasn't already been found
  if(documentIdFound === 0) {
    checkURL(sendDocId);
  }
  var switchTabCommand = new SwitchTabCommand(Date.now());
  switchTabCommands.push(switchTabCommand);
  newCommand();
});

//Function called on creating a tab (calls update and activate also)
chrome.tabs.onCreated.addListener(function(tabId, changeInfo, tab) {
  //Check for the document ID if it hasn't already been found
  if(documentIdFound === 0) {
    checkURL(sendDocId);
  }
  var createNewTabCommand = new CreateNewTabCommand(Date.now());
  createNewTabCommands.push(createNewTabCommand);
  newCommand();
});

//Function polls every 1000 ms (?) to detect if the Chrome browser is currently in focus
window.setInterval(checkBrowserFocus, 1000);
function checkBrowserFocus() {
  chrome.windows.getCurrent(function(browser) {
    //Only log an event when a change in focus occurs
    if (browser.focused != chromeInFocus) {
      chromeInFocus = browser.focused;
      var windowFocusCommand = new WindowFocusCommand(Date.now());
      windowFocusCommands.push(windowFocusCommand);
      newCommand();
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
    case  "1utnT3bYBZJf_M3NFb7CBVqpop4pIdR_AnZBo6pcjr8s":
      return 1;
    case "1cEe3hOc6hOQZbYrL1ojlIIPWqChNTIaP-rxDOhqVY4o":
      return 2;
    case "1znDjpT1DmRL5d55UK9GXhoicymICt7CE7lBmpyeEE-Y":
      return 3;
    case "16_j0WNLvvVlSUG0LcSCc_PRbmN0X8SpNUAI5gQ6DyiI":
      return 4;
    case "1PsNChRRfgorPLDNCls8n3IYGu0bq0aCQ2vK7hCOqmNA":
      return 5;
    case "1ZtHqc9NGtQ55E38xCzZBBCvNgAz2nWwrhvdf6-2eqPg":
      return 6;
    case "1R_42ciqJGjKPYLK8NX8VA2qnbw-LUSqjEnWjv-KtjG4":
      return 7;
    case "1YSSmozS3A-RBxzR-dQBgiz5dq4B0a1f9d8Kgunt0dmw":
      return 8;
    case "1_DqsfW4_v_SeNZpNRYRDnYnEDVwnRLPmbw9MIWvDkIk":
      return 9;
    case "1Zb55vT7OU27w9vPZdYNIbyBVbrsLuCSnnLsAG9nAsmA":
      return 10;
    case "1F7Oz6IVRmHgPKV1PvWkHlChtcaYS4J_XDT-CfzbBicw":
      return 11;
    case "173XESj7nTT3AtWOzR93BC-md5kDWalNED7hGC6MSrkg":
      return 12;
    case "1sqsPUagt8R_luTV6BXGRu51_d08T3Oh-dipgsVNE9dc":
      return 13;
    case "1jWTEgxOhUBkQx4qBd6Z91i6yib1R6mzfT3a_cOroUo4":
      return 14;
    case "10a6Kh-E5F2FnKew-I47bHTvIKdvC0EiCCyrcd2AUuEk":
      return 15;
    case "11vxDHg7KKZYjIi69jdlgEFXrcdNOUqmAoyoxIJCaDJY":
      return 16;
    case "1eCScFpbcEeqtJUyyy63boQ2AOIzfSP9XmXVkfMFiu7g":
      return 17;
    case "1ZXOHpZ8ez0m6efKIZ5M7li2mieQUrs4jsnx9JQm_AxY":
      return 18;
    case "1Obo7N7IPu2-muLJ43TjvAPy7v23W5LvFdMG1N-E-tzE":
      return 19;
    case "1FHM0P74zOzqiMSgghiMeb5mIDhjWj3z9qlK_K2k8C98":
      return 20;
    case "1aR6K2ld_Hn3fGq6wOaOQTp_vmvPlekhisGSxLOu9DkE":
      return 21;
    case "1t68zyyf6VWgkzWo50TO_yEzN_FUE56EO_3JlT7zz3Q0":
      return 22;
    case "1DSlcBm_NYHSZmmGWqk43xwex_Yo8M72M1VZ9ejMDCY0":
      return 23;
    case "1fc4WLpuRFP4fP6uwr0JmIYat9eFfR6lZJ3lHz-0steA":
      return 24;
    case "15z4a9HvKUYps56MARXffWTlVMFzcaVlP5vwLRvapEWM":
      return 25;
    case "1CEE4iEuw-6liGOrCOrv9cT68LigNoJDeIsqDYYVtjU0":
      return 26;
    case "1QykAhwwRunhp6QPEhaHSfxtqSAq0J7sdwiaf5JH5its":
      return 27;
    case "1kWQCeaWIRRsqbApb3zLrZm0qCRWqfDk48ewaIVUVnmw":
      return 28;
    case "1bQKmqyxqHweOanrgABlNUzUcVvyTMrNsaKrrC5g4YaU":
      return 29;
    case "1GWPnhLkICgXJYpFxyOFtxFquJgFLJ1WhnqKtSMUJ9Hs":
      return 30;
    case "1_0wSnzPhcZ6Pwd-elcnu-3i2c8VAPdNizSZdKX6yirE":
      return 31;
    case "1rxLuEdpFa1oIcvK3_YEiRfmC-AwwDsFXlrbf_s00O5Q":
      return 32;
    case "1ZMe6OhGZfsJWhJ3bHucyMICjBy9icvCaCzPJs3ofcYk":
      return 33;
    case "1xZONI7xDlb9qdltAdah6p9Ig3PLhOrOX9GtDUU_Mbu4":
      return 34;
    case "1hVrQjxkcoOkEnDGoclnXymjKizjiWyR6E_P5Py_H7R0":
      return 35;
    case "1XEyyogKV7lcNUuckPXnP6zKhhmg2zbA30suP7tb4WRk":
      return 36;
    case "1FSk_Coa86sSxN9LT4rpq4pCUtUjZKbv4_og6rLTWVMg":
      return 37;
    case "1AM11GlDR899B_1Skmz0s0D7sak-X_b7FVnu1l2PiEHk":
      return 38;
    case "1nMgO_pEiwpiqPoAOoendaqzu-cwmYW3QOQ9rpKFCQeM":
      return 39;
    case "15ZFb-5JC97-svNGSN9XhYnxoIrbFBIT0ntmMKCFfkgw":
      return 40;
    default:
        return 0;
  }
}
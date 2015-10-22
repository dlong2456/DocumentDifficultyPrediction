//****UI BUTTON CODE****//
//TODO: Make a "Ask for Help" button
//TODO; Make a "Comments" text field
//TODO: Figure out how to pull HTML strings from documents so they are more readable/editable

//Create 'Facing difficulty' button
var $difficulty_button = $('<div role="button" id="facingDifficulty" class="goog-inline-block jfk-button jfk-button-standard docs-titlebar-button" aria-disabled="false" aria-pressed="false" tabindex="0" aria-label="I am facing difficulty" value="undefined" tabindex="0" style="-webkit-user-select: none;">Facing Difficulty</div>');
$difficulty_button.prependTo($('.docs-titlebar-buttons'));

//Create 'Making progress' button
var $progress_button = $('<div role="button" id="makingProgress" class="goog-inline-block jfk-button jfk-button-standard docs-titlebar-button" aria-disabled="false" aria-pressed="false" tabindex="0" aria-label="I am making progress" value="undefined" style="-webkit-user-select: none;">Making Progress</div>');
$progress_button.prependTo($('.docs-titlebar-buttons'));
//formatting
$progress_button.addClass('jfk-button-clear-outline');

//Set difficulty status and create status display
//TODO: Set this variable using data from backend
var statusText = 'Progress'; //either 'Progress' or 'Slow progress'
var $status = $('<div id="status" class="goog-inline-block" aria-label="Difficulty status" style="-webkit-user-select: none;" tabindex="0"><span id="status-text" style = "color: #458B00">' + statusText + '</span>&nbsp;&nbsp;&nbsp;&nbsp;</div>');
$status.prependTo($('.docs-titlebar-buttons'));

//Create dialog box that will open when user clicks "Facing Difficulty"
var $difficulty_box = $('<div class="docs-docos-activitybox docos-enable-new-header" id="difficulty-box" aria-label="Difficulty box" tabindex="0" style="display: none;"> <div class="docs-docos-activitybox-inner" dir="ltr" style="text-align: left;"> <div class="docos docos-streampane-container docos-enable-docs-header" tabindex="0"> <div class="docos-streampane-content"> <div class="docos-streampane-header"><div id = "typeButton" class = "goog-inline-block goog-flat-menu-button" aria-haspopup="true" aria-expanded="false" style="-webkit-user-select: none;"><div class="goog-inline-block goog-flat-menu-button-caption"><div class="docos-ns-caption"><div class="docos-ns-caption-text" data-tooltip="Select the type of difficulty you are facing" aria-label: "Select the type of difficulty you are facing"><div class="docos-ns-caption-text-value">Type</div></div></div></div><div class="goog-inline-block goog-flat-menu-button-dropdown" aria-hidden="true">&nbsp</div></div><div id = "difficultyTypeDropdown" class="goog-menu goog-menu-vertical" role = "menu" aria-haspopup= "true" style="-webkit-user-select: none; visibility: visible; display: none;"><div class="goog-menuitem goog-option" role = "menuitemcheckbox" aria-checked = "true" aria-label: "Select if you are facing spelling difficulty" id="spelling" style: "-webkit-user-select: none;"><div class="goog-menuitem-content" style: "-webkit-user-select: none;"><div class="goog-menuitem-checkbox" id = "spelling-check" style: "-webkit-user-select: none;"></div>Spelling</div></div><div class="goog-menuitem goog-option" role = "menuitemcheckbox" aria-checked = "true" aria-label: "Select if you are facing grammar difficulty" id="grammar" style: "-webkit-user-select: none;"><div class="goog-menuitem-content" style: "-webkit-user-select: none;"><div class="goog-menuitem-checkbox" id = "grammar-check" style: "-webkit-user-select: none;"></div>Grammar</div></div><div class="goog-menuitem goog-option" role = "menuitemcheckbox" aria-checked = "true" aria-label: "Select if you are facing content-related difficulty" id="content" style: "-webkit-user-select: none;"><div class="goog-menuitem-content" style: "-webkit-user-select: none;"><div class="goog-menuitem-checkbox" id = "content-check" style: "-webkit-user-select: none;"></div>Content</div></div></div></div></div></div></div></div>');
$difficulty_box.appendTo($('body'));
//Add custom classes for correct positioning (see CSS file)
$('#difficulty-box').addClass('custom');
$('#difficultyTypeDropdown').addClass('custom');

//Function that changes the color of the difficulty status display according to the text being displayed
function changeColor() {
	if (statusText == 'Progress') {
		//Green
		document.getElementById('status-text').style.color = "#32CD32";
	} else {
		//Red
		document.getElementById('status-text').style.color = "#FF0000";
	}
}

//****BUTTON EVENT LISTENERS****//

//Listen for clicks on "Facing Difficulty" button
$('#facingDifficulty').click(function() {
    //If "facing difficulty" is already selected, then unselect it and hide dialog box
	if ($('#difficulty-box:visible').length) {
		$('#difficulty-box').hide();
		$('#docs-docos-caret').hide().removeClass('custom');
		$('#facingDifficulty').removeClass('jfk-button-checked');
		//close dropdown menu if it is open
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		$('#difficultyTypeDropdown').hide();
	//Otherwise, select it and show dialog box
    } else {
		//formatting
		$('#facingDifficulty').addClass('jfk-button-checked');
		$('#facingDifficulty').addClass('jfk-button-clear-outline');
		//open dialog box
		$('#docs-docos-caret').show().addClass("custom");
		$('#difficulty-box').show();
	}
});

//Listen for clicks on "Making Progress" button
$progress_button.click(function() {
	//Remove 'selected' styling on Facing Difficulty button
	if ($('#difficulty-box:visible').length) {
		$('#difficulty-box').hide();
		$('#docs-docos-caret').hide().removeClass('custom');
		$('#facingDifficulty').removeClass('jfk-button-checked');
    }
    //If Making Progress is already the status
	if (statusText == 'Progress') {
		// Don't do anything
	//Otherwise, select it and notify backend
    } else {
		//Tell background.js that the user made a status correction
		chrome.runtime.sendMessage({timestamp: Date.now(), facingDifficulty: false}, function(response) {
			//handle response
		});
		//change status text and color
		statusText = 'Progress';
		$('#status-text').text(statusText);
		changeColor();
	}
});

//Change button formatting on mouseover and mouseout
$('#facingDifficulty').mouseover(function() {
	$(this).addClass('jfk-button-hover');
});
  
$('#facingDifficulty').mouseout(function() {
	$(this).removeClass('jfk-button-hover');
});

$('#makingProgress').mouseover(function() {
	$(this).addClass('jfk-button-hover');
});
  
$('#makingProgress').mouseout(function() {
	$(this).removeClass('jfk-button-hover');
});

//****DIFFICULTY DIALOG BOX CODE****//

//****EVENT HANDLERS FOR BUTTONS IN BOX****//

//Type button
//TODO: Icon for Type button
$('#typeButton').mouseenter(function() {
	//formatting
	$('#typeButton').addClass('goog-flat-menu-button-hover');
	$('#typeButton').addClass('goog-flat-menu-button-focused');
});

$('#typeButton').mouseleave(function() {
	//formatting
	$('#typeButton').removeClass('goog-flat-menu-button-hover');
	$('#typeButton').removeClass('goog-flat-menu-button-focused');
});

$('#typeButton').click(function() {
	//If dropdown is already open, then shut it
	if($('#typeButton').hasClass('goog-flat-menu-button-open')) {
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		//close dropdown
		$('#difficultyTypeDropdown').hide();
	//otherwise open the dropdown
	} else {
		//formatting
		$('#typeButton').addClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'true');
		//open dropdown
		$('#difficultyTypeDropdown').show();
	}
});

//*****MENU ITEM EVENT LISTENERS*****//

//TODO: Make a function for these that would make this more compartmentalized

//Click events
$('#spelling').click(function() {
	if($('#spelling').hasClass('goog-option-selected')) {
		//do nothing
	} else {
		//TODO: edit aria-activedescendent and aria-owns 
		//Select spelling and unselect other menu options
		$('#grammar').removeClass('goog-option-selected');
		$('#content').removeClass('goog-option-selected');
		$('#spelling').addClass('goog-option-selected');
		//change status text and color
		statusText = 'Slow progress';
		$('#status-text').text(statusText);
		changeColor();
		//tell background.js that the user made a status correction
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "spelling", facingDifficulty: true}, function(response) {
		});
		//close dropdown
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		$('#difficultyTypeDropdown').hide();
	}
});

$('#grammar').click(function() {
	if($('#grammar').hasClass('goog-option-selected')) {
		//do nothing
	} else {
		//Select grammar and unselect other menu options
		$('#spelling').removeClass('goog-option-selected');
		$('#content').removeClass('goog-option-selected');
		$('#grammar').addClass('goog-option-selected');
		//change status text and color
		statusText = 'Slow progress';
		$('#status-text').text(statusText);
		changeColor();
		//tell background.js that the user made a status correction
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "grammar", facingDifficulty: true}, function(response) {
		});
		//close dropdown
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		$('#difficultyTypeDropdown').hide();
	}
});

$('#content').click(function() {
	if($('#content').hasClass('goog-option-selected')) {
		//do nothing
	} else {
		//Select content and unselect other menu options
		$('#grammar').removeClass('goog-option-selected');
		$('#spelling').removeClass('goog-option-selected');
		$('#content').addClass('goog-option-selected');
		//change status text and color
		statusText = 'Slow progress';
		$('#status-text').text(statusText);
		changeColor();
		//tell background.js that the user made a status correction
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "content", facingDifficulty: true}, function(response) {
		});
		//close dropdown
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		$('#difficultyTypeDropdown').hide();
	}
});

//Hover events
$('#spelling').mouseenter(function() {
	$('#spelling').addClass('goog-menuitem-highlight');
});

$('#grammar').mouseenter(function() {
	$('#grammar').addClass('goog-menuitem-highlight');
});

$('#content').mouseenter(function() {
	$('#content').addClass('goog-menuitem-highlight');
});

$('#spelling').mouseleave(function() {
	$('#spelling').removeClass('goog-menuitem-highlight');
});

$('#grammar').mouseleave(function() {
	$('#grammar').removeClass('goog-menuitem-highlight');
});

$('#content').mouseleave(function() {
	$('#content').removeClass('goog-menuitem-highlight');
});

//****SPELLCHECK EVENT LISTENER****//

//Listen for spellcheck events by listening to clicks on spellcheck buttons
document.addEventListener("click",
  function(event) {
	if (event.target.getAttribute('id') === "docs-spellcheckslidingdialog-button-change") {
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "spellcheck_change"}, function(response) {
		});
	} else if (event.target.getAttribute('id') === "docs-spellcheckslidingdialog-button-dictionary") {
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "spellcheck_dictionary"}, function(response) {
		});
	} else if (event.target.getAttribute('id') === "docs-spellcheckslidingdialog-button-ignore") {
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "spellcheck_ignore"}, function(response) {
		});
	}
  }
);

//****PAGE SCROLL AND MOUSEOVER LISTENERS****//

//this only works for page scrolls right now (not scrolling in iframe)
document.addEventListener("scroll",
	function(event) {
		//log scroll events
	}
);

//This works -- how many mouse moves do I actually want to listen to?  
document.addEventListener("mousemove", function(event) {
	//log mouse move events
});

//class name of specific element being scrolled - this doesn't work yet
$(".kix-appview-editor").scroll(function() {
	//log scroll events
});

//***MESSAGE HANDLING CODE***//

chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
  //receive messages from backend.js
  }
);
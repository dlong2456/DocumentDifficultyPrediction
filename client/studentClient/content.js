//****UI BUTTON CODE****//

//TODO: Figure out how to pull HTML strings in code below from files so they are more readable/editable

//Create 'Facing difficulty' button
var $difficulty_button = $('<div role="button" id="facingDifficulty" class="goog-inline-block jfk-button jfk-button-standard docs-titlebar-button" aria-disabled="false" aria-pressed="false" tabindex="0" aria-label="I am facing difficulty" value="undefined" tabindex="0" style="-webkit-user-select: none;">Facing Difficulty</div>');
$difficulty_button.prependTo($('.docs-titlebar-buttons'));

//Create 'Making progress' button
var $progress_button = $('<div role="button" id="makingProgress" class="goog-inline-block jfk-button jfk-button-standard docs-titlebar-button" aria-disabled="false" aria-pressed="false" tabindex="0" aria-label="I am making progress" value="undefined" style="-webkit-user-select: none;">Making Progress</div>');
$progress_button.prependTo($('.docs-titlebar-buttons'));
//Formatting
$progress_button.addClass('jfk-button-clear-outline');

//Set difficulty status and create status display
var statusText = 'Pending'; //either 'No status yet', Progress' or 'Slow progress'. Backend will change it based on status.
var $status = $('<div id="status" class="goog-inline-block" aria-label="Difficulty status" style="-webkit-user-select: none;" tabindex="0"><span id="status-text" style = "color: #458B00">' + statusText + '</span>&nbsp;&nbsp;&nbsp;&nbsp;</div>');
$status.prependTo($('.docs-titlebar-buttons'));

//Create dialog box that will open when user clicks "Facing Difficulty"
var $difficulty_box = $('<div class="docs-docos-activitybox docos-enable-new-header" id="difficulty-box" aria-label="Difficulty box" tabindex="0" style="display: none;"> <div class="docs-docos-activitybox-inner" dir="ltr" style="text-align: left;"> <div class="docos docos-streampane-container docos-enable-docs-header" tabindex="0"> <div class="docos-streampane-content"> <div class="docos-streampane-header"><div id = "typeButton" class = "goog-inline-block goog-flat-menu-button" aria-haspopup="true" aria-expanded="false" style="-webkit-user-select: none;"><div class="goog-inline-block goog-flat-menu-button-caption"><div class="docos-ns-caption"><div class="docos-ns-caption-text" data-tooltip="Select the type of difficulty you are facing" aria-label: "Select the type of difficulty you are facing"><div class="docos-ns-caption-text-value">Type</div></div></div></div><div class="goog-inline-block goog-flat-menu-button-dropdown" aria-hidden="true">&nbsp</div></div><div id = "difficultyTypeDropdown" class="goog-menu goog-menu-vertical" role = "menu" aria-haspopup= "true" style="-webkit-user-select: none; visibility: visible; display: none;"><div class="goog-menuitem goog-option" role = "menuitemcheckbox" aria-checked = "true" aria-label: "Select if you are facing spelling difficulty" id="spelling" style: "-webkit-user-select: none;"><div class="goog-menuitem-content" style: "-webkit-user-select: none;"><div class="goog-menuitem-checkbox" id = "spelling-check" style: "-webkit-user-select: none;"></div>Spelling</div></div><div class="goog-menuitem goog-option" role = "menuitemcheckbox" aria-checked = "true" aria-label: "Select if you are facing grammar difficulty" id="grammar" style: "-webkit-user-select: none;"><div class="goog-menuitem-content" style: "-webkit-user-select: none;"><div class="goog-menuitem-checkbox" id = "grammar-check" style: "-webkit-user-select: none;"></div>Grammar</div></div><div class="goog-menuitem goog-option" role = "menuitemcheckbox" aria-checked = "true" aria-label: "Select if you are facing content-related difficulty" id="content" style: "-webkit-user-select: none;"><div class="goog-menuitem-content" style: "-webkit-user-select: none;"><div class="goog-menuitem-checkbox" id = "content-check" style: "-webkit-user-select: none;"></div>Content</div></div></div><label for="status-content">Details:</label><input id="status-content" class = "modal-dialog-userInput jfk-textinput" style="overflow: auto;" type = "text"></input><div role="button" id="submit" class="goog-inline-block jfk-button jfk-button-standard" aria-disabled="false" aria-pressed="false" tabindex="0" aria-label="Submit" value="undefined" style="-webkit-user-select: none;">Submit</div></div></div></div></div></div>');
$difficulty_box.appendTo($('body'));
//Add custom classes for correct positioning (see CSS file)
$('#difficulty-box').addClass('custom');
$('#difficultyTypeDropdown').addClass('custom');

//Function that changes the color of the difficulty status display according to the text being displayed
function changeColor() {
	if (statusText === 'Progress') {
		//Green
		document.getElementById('status-text').style.color = "#32CD32";
	} else if (statusText === 'Slow progress') {
		//Red
		document.getElementById('status-text').style.color = "#FF0000";
	} else {
		//Black (for Pending)
		document.getElementById('status-text').style.color = "#000000";
	}
}

//Refresh the color on page load
changeColor();

//****BUTTON EVENT LISTENERS****//

//Listen for clicks on "Facing Difficulty" button
$('#facingDifficulty').click(function() {
    //If "facing difficulty" is already selected, then unselect it and hide dialog box
	if ($('#difficulty-box:visible').length) {
		$('#difficulty-box').hide();
		$('#docs-docos-caret').hide().removeClass('custom');
		$('#facingDifficulty').removeClass('jfk-button-checked');
		//Close dropdown menu if it is open
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		$('#difficultyTypeDropdown').hide();
	//Otherwise, select it and show dialog box
    } else {
		//Formatting
		$('#facingDifficulty').addClass('jfk-button-checked');
		$('#facingDifficulty').addClass('jfk-button-clear-outline');
		//Open dialog box
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
	if (statusText === 'Progress') {
		// Don't do anything
	//Otherwise, select it and notify backend
    } else {
		//Tell background.js that the user made a status correction
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "statusUpdate", facingDifficulty: 0}, function(response) {
		});
		//Change status text and color
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
//TODO: Make an icon for the Type button
$('#typeButton').mouseenter(function() {
	//Formatting
	$('#typeButton').addClass('goog-flat-menu-button-hover');
	$('#typeButton').addClass('goog-flat-menu-button-focused');
});

$('#typeButton').mouseleave(function() {
	//Formatting
	$('#typeButton').removeClass('goog-flat-menu-button-hover');
	$('#typeButton').removeClass('goog-flat-menu-button-focused');
});

$('#typeButton').click(function() {
	//If dropdown is already open, then shut it
	if($('#typeButton').hasClass('goog-flat-menu-button-open')) {
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		//Close dropdown
		$('#difficultyTypeDropdown').hide();
	//Otherwise open the dropdown
	} else {
		//Formatting
		$('#typeButton').addClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'true');
		//Open dropdown
		$('#difficultyTypeDropdown').show();
	}
});

//*****MENU ITEM EVENT LISTENERS*****//

//TODO: Make a function for these that would make this more compartmentalized

//Click events
$('#spelling').click(function() {
	if($('#spelling').hasClass('goog-option-selected')) {
		//Do nothing
	} else {
		//TODO: edit aria-activedescendent and aria-owns 
		//Select spelling and unselect other menu options
		$('#grammar').removeClass('goog-option-selected');
		$('#content').removeClass('goog-option-selected');
		$('#spelling').addClass('goog-option-selected');
		//Close dropdown
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		$('#difficultyTypeDropdown').hide();
	}
});

$('#grammar').click(function() {
	if($('#grammar').hasClass('goog-option-selected')) {
		//Do nothing
	} else {
		//Select grammar and unselect other menu options
		$('#spelling').removeClass('goog-option-selected');
		$('#content').removeClass('goog-option-selected');
		$('#grammar').addClass('goog-option-selected');
		//Close dropdown
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		$('#difficultyTypeDropdown').hide();
	}
});

$('#content').click(function() {
	if($('#content').hasClass('goog-option-selected')) {
		//Do nothing
	} else {
		//Select content and unselect other menu options
		$('#grammar').removeClass('goog-option-selected');
		$('#spelling').removeClass('goog-option-selected');
		$('#content').addClass('goog-option-selected');
		//Close dropdown
		$('#typeButton').removeClass('goog-flat-menu-button-open');
		document.getElementById('typeButton').setAttribute('aria-expanded', 'false');
		$('#difficultyTypeDropdown').hide();
	}
});

$('#submit').click(function() {
	//Change status text and color
	statusText = 'Slow progress';
	$('#status-text').text(statusText);
	changeColor();
	//Get info about difficulty type and details
	var type;
	if ($('#grammar').hasClass('goog-option-selected')) {
		type = 'grammar';
	} else if ($('#spelling').hasClass('goog-option-selected')) {
		type = 'spelling';
	} else if ($("#content").hasClass('goog-option-selected')) {
		type = 'content';
	}
	//Tell background.js that the user made a status correction
	chrome.runtime.sendMessage({timestamp: Date.now(), type: "statusUpdate", difficultyType: type, details: $('#status-content').val(), facingDifficulty: 1}, function(response) {
	});
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
		//Send message to background.js
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "spellcheck_change"}, function(response) {
		});
	} else if (event.target.getAttribute('id') === "docs-spellcheckslidingdialog-button-dictionary") {
		//Send message to background.js
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "spellcheck_dictionary"}, function(response) {
		});
	} else if (event.target.getAttribute('id') === "docs-spellcheckslidingdialog-button-ignore") {
		//Send message to background.js
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "spellcheck_ignore"}, function(response) {
		});
	}
  }
);

//****PAGE SCROLL AND OTHER LISTENERS****//

//Function that is called on scroll events
function onScroll(event) {
	if (event.srcElement.attributes[0].nodeValue === "kix-appview-editor") {
		//Send message to background.js
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "scroll"}, function(response) {
		});
	}
}

//Generate a new version of onScroll using underscore.js's throttle function
//Throttle ensures that onScroll is only called once every 2000 milliseconds (2s)
//Ordinarily, scroll events fire really rapidly in succession and we don't want them to skew the command aggregation
//For more documentation, see underscorejs.org
var throttled = _.throttle(onScroll, 2000, {trailing: false});

//Attach the new "throttled" function to the scroll event listener 
window.addEventListener('scroll', throttled, true);

//---Code below can listen to mouse moves. Disabled for now.--- 

// document.addEventListener("mousemove", function(event) {
// 	//log mouse move events
// });

//----Code below can listen to keypresses. Disabled for now.---

// window.addEventListener("keypress", function(event) {
// 	var charCode = (typeof event.which == "number") ? event.which : event.keyCode;
//     if (charCode > 0) {
//     	console.log("KEYPRESS: " + String.fromCharCode(charCode));
//     }
// });

var cursorLeft;
var cursorTop;
//Function that is called on cursor move events. Every time a user clicks, this function determines whether the cursor moved.
//TODO: A limitation is that this does not incorporate cursor movement via arrow keys - this functionality could probably be added with a key listener
document.addEventListener('click', function() {
	var element = document.getElementsByClassName('kix-cursor docs-ui-unprintable')[0];
    var style = window.getComputedStyle(element);
    newTop = style.getPropertyValue('top');
    newLeft = style.getPropertyValue('left');
    if (newTop !== cursorTop || newLeft !== cursorLeft) {
		//Tell background JS that the cursor moved
		chrome.runtime.sendMessage({timestamp: Date.now(), type: "cursor", left: newLeft, top: newTop}, function(response) {
		});
		cursorLeft = newLeft;
		cursorTop = newTop;
    }
});

//***MESSAGE HANDLING CODE***//

//Receive messages from background.js
chrome.runtime.onMessage.addListener(
	function(request, sender, sendResponse) {
		if (request.message === '1') {
			statusText = 'Slow progress';
			$('#status-text').text(statusText);
			changeColor();
		} else if (request.message === '0') {
			statusText = 'Progress';
			$('#status-text').text(statusText);
			changeColor();
		} else if (request.message === 'close') {
			statusText = 'Pending';
			$('#status-text').text(statusText);
			changeColor();
		} else if (request.message === 'pending') {
			statusText = 'Pending';
			$('#status-text').text(statusText);
			changeColor();
		}
	}
);
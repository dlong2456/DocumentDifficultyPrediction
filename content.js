$("#dialog").dialog( {
	autoOpen: true,
	dialogClass: "no-close",
	buttons: [
	{
		text: "Yes",
		click: function() {
			//ask what kind of difficulty?
			//then send message with difficulty
			// chrome.runtime.sendMessage({type:'difficulty_type'});
			$(this).dialog("close");
		}
	},
	{
		text: "No",
		click: function() {
			$(this).dialog("close");
		}
	}
	]
});

//this isn't working
$("#dialog").dialog("open");
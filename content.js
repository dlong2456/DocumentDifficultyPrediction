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

// Could alternatively listen to when this box is open? But that doesn't signify action: docs-spellcheckslidingdialog-id

//this only works for page scrolls right now (not scrolling in iframe)
document.addEventListener("scroll",
	function(event) {
		console.log('scroll');
	}
);

//This works -- how many mouse moves do I actually want to listen to?  
// document.addEventListener("mousemove", function(event) {
//	console.log('mouse move');
// });


//class name of specific element being scrolled
// $(".kix-appview-editor").scroll(function() {
// 	console.log('scroll 2');
// });

chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
    if( request.message === "clicked_browser_action" ) {
      var firstHref = $("a[href^='http']").eq(0).attr("href");
      alert('click');
      console.log(firstHref);
    }
  }
);

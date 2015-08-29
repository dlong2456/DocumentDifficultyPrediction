chrome.webRequest.onBeforeRequest.addListener(
function(request) {
    if (request.url.indexOf('/save?') != -1) {
      var requestBody = request.requestBody;
      var docId = request.url.match("docs\.google\.com\/document\/d\/(.*?)\/save")[1];

      var data = {
        "bundles": requestBody.formData.bundles,
        "revNo": requestBody.formData.rev,
        "docId": docId,
        "timeStamp" : parseInt(request.timeStamp, 10)
      };
      console.log(data);
    }
  },
  { urls: ["*://docs.google.com/*"] },
  ['requestBody']
);
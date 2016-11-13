// This file is required by the index.html file and will
// be executed in the renderer process for that window.
// All of the Node.js APIs are available in this process.
const remote = require('remote');
var dialog = remote.require('dialog');
var fs = require('fs');

dialog.showOpenDialog({ properties: [ 'openFile', 'multiSelections',function(fileNames){
  console.log(fileNames);
}]});
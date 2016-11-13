const electron = require('electron')
// Module to control application life.
const app = electron.app
// Module to create native browser window.
const BrowserWindow = electron.BrowserWindow

const path = require('path')
const url = require('url')
var express = require('express');
var bodyParser = require('body-parser');
// var run = require('express')();
// var http = require('http').Server(run);
// var io = require('socket.io')(http);
var $ = require('jquery');
var net = require('net');
var exp = express();
var JsonSocket = require('json-socket');
var clients = [];
  exp.use(bodyParser.json());
  exp.use(bodyParser.json({type: 'application:vnd.api+json' }));
  exp.use(bodyParser.urlencoded({extended: true}));
  //var router = express.Router();


// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWindow

function createWindow () {
  // Create the browser window.
  mainWindow = new BrowserWindow({width: 800, height: 600})

  // and load the index.html of the app.
  mainWindow.loadURL(url.format({
    pathname: path.join(__dirname, 'public/transmit.html'),
    protocol: 'file:',
    slashes: true
  }))

  // Open the DevTools.
  mainWindow.webContents.openDevTools()

  // Emitted when the window is closed.
  mainWindow.on('closed', function () {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    mainWindow = null
  })
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
exp.use(express.static(__dirname + '/public'))

app.on('ready', createWindow)

// Quit when all windows are closed.
app.on('window-all-closed', function () {
  // On OS X it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', function () {
  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (mainWindow === null) {
    createWindow()
  }
})

// exp.get('/', function(req, res){
//   res.sendFile(__dirname +'/index.html');
// });

// io.on('connection', function(socket){
//   console.log('a user connected');
// });

// io.on('connection', function(socket){
//   socket.on('chat message', function(msg){
//     io.emit('chat message', msg);
//   });
// });

exp.listen(3000);
// http.listen(3000, function(){
//   console.log('listening on *:3000');
// }); 

exp.get('/t', function(req, res){
  res.sendFile(__dirname +'/public/receivetext.html');
});

exp.get('/s', function(req, res){
  res.sendFile(__dirname +'/public/sendtext.html');
});
exp.post('/connect/phone', function(req, res) {
  var client = new net.Socket();
  console.log(req.body);
  client.connect(req.body.port, req.body.ip, function() {
    console.log("Connected to Phone");
  });

  client.on('close', function() {
    console.log("Connection Closed");
    createServer();
  })
  res.send("connected");

});

exp.post('/connect/server', function(req, res) {
  var client = new net.Socket();
  console.log(req.body);
  client.connnect(9990, req.body.ip, function() {
    console.log("Connected to Server");
  });
    res.send("connected to server");
});


function createServer() {
  var server = net.createServer(function(socket) {
  });
  server.listen(9990);
  server.on('connection', function(socket) {
    console.log("Client Connected");
    socket.on('message', function(message) {
      //client sent message to server (file)
    });
  });
  console.log("Server Created");
}
// run.use(express.static('public'))


// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.

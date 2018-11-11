var PRINTING_SPEED = 100;
var KEY_NEXT = 122;
var LINE_LEN = 24;

var newMsgReady = true;
var newLineReady = true;
var newMessageInterrupt = false;
var currentLine = "";
var text = [];
var i = 0;

function showMsg(msg, guider,type){
    // var msgContainer = document.getElementById('msgContainer');
    // var guider = $("<div/>").addClass(guider);
    $("#msgContainer").appendChild($("<div/>").addClass(guider));
    $("#msgContainer").style.display="block";

    if(newMsgReady){
        newMsgReady = false;
        text = msg;
        document.getElementById('nextMsg').style.display = "block";
        printMsg();
    }
}

function printMsg() {
    if (text.length > 0) {
        if (newLineReady) {
            newLineReady = false;
            currentLine = text[0].split('');
            sendToTextBox();
        } else {
            newMessageInterrupt = true;
        }
    } else {
        newMsgReady = true;
        document.getElementById('nextMsg').style.display = "none";
    }
}

function sendToTextBox() {
    var container = document.getElementById("speechText");
    container.innerHTML = "";
    i = 0;
    var interval = setInterval(function () {
        if (NewMessageInterrupt) {
            container.innerHTML = text[0];
            text.shift();
            newLineReady = true;
            newMessageInterrupt = false;
            clearInterval(interval);
        }
        if (i < currentLine.length) {
            container.innerHTML += currentLine[i++];
        }
        else {
            text.shift();
            newLineReady = true;
            newMessageInterrupt = false;
            clearInterval(interval);
        }
    }, PRINTING_SPEED);
}

function nextString(event) {
    if (event.keyCode === KEY_NEXT) {
        if (newMsgReady) {
            document.getElementById('msgContainer').style.display = "none";
        } else {
            printMsg();
        }
    }
}

function resetGlobals(){
    newMsgReady = true;
    newLineReady = true;
    newMessageInterrupt = false;
    currentLine = "";
    text = [];
    i = 0;
}

export {showMsg,nextString};
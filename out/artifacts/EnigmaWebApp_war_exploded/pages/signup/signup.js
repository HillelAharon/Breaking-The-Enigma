//import {showMsg,nextString} from "../../common/messageUtil.js";
var SIGNUP_URL = "./login";
var HITLER_HEAD = "hitlerHead";
var CHURCHIL_HEAD = "churchilHead";
var PRINTING_SPEED = 100;
var KEY_NEXT = 122;

var newMsgReady = true;
var newLineReady = true;
var newMessageInterrupt = false;
var currentLine = "";
var text = [];
var i = 0;

var welcomeMsg = [
    "WELCOME, PRESS `Z` TO CONTINUE..",
    "NOW, INSERT YOUR NAME PLEASE"
];

var text = [];
var USER_EXIST = [
    "SKANDAL !! USERNAME ALREADY EXIST",
    "DAS IST YOUR NAME NOW"
];
var NAME_NO_VALID = [
    "USERNAME MUST CONTAIN CHARACTERS"
];

$(function () {
    $("#loginForm").submit(function() {
        if($("#inputText").val().replace(/ /g, "") !== ""){
            $.ajax({
                url: SIGNUP_URL,
                data: $(this).serialize(),
                dataType: 'json',
                success: function(message) {
                    if(message.status === false){
                        showMsg(USER_EXIST,HITLER_HEAD);
                        document.getElementById("inputText").value = message.payload.toString();
                    }
                    else
                        window.location.replace( message.payload.toString());

                },
                error: function() {
                    console.error("Failed to submit");

                }
            });

        }
        else{
            $("#inputText").val("");
            showMsg(NAME_NO_VALID,HITLER_HEAD);
        }
        return false;
    });
    showMsg(welcomeMsg,CHURCHIL_HEAD);
});

//
//
// function sendKey(event) {
//     nextString(event.keyCode);
//     console.log(event);
// }

function showMsg(msg, guider){
    var msgCont = document.getElementById('msgContainer');
    msgCont.style.display="block";
    var guiderHead = document.createElement('DIV');
    guiderHead.setAttribute("class",guider);
    msgCont.appendChild(guiderHead);
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
        if (newMessageInterrupt) {
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

function nextString(keyCode) {
    if (keyCode.keyCode === KEY_NEXT) {
        if (newMsgReady) {
            document.getElementById('msgContainer').style.display = "none";
        } else {
            printMsg();
        }
    }
}



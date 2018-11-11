var refreshRate = 1000;
var ALLIES_LOBBY_URL = "./allieslobby";
var battlefieldListInterval = null;
var selectedBattle = null;

/////////**********************************************/
/*          MESSAGES                 */
/**********************************************/
var CHURCHIL_HEAD = "churchilHead";
var PRINTING_SPEED = 100;
var KEY_NEXT = 122;

var newMsgReady = true;
var newLineReady = true;
var newMessageInterrupt = false;
var currentLine = "";
var text = [];
var i = 0;


function refreshAlliesLobby(battlefields) {

    $("#battlefieldsContainer").empty();

    $.each(battlefields || [], function( i , battlefield) {
        var className = "battleEntry";
        var newBattle = $("<div id='" + battlefield.name + "'>")//"<table>");
        var values = [
            {name: "U-boat", value: battlefield.uboat},
            {name: "Rounds",  value: battlefield.rounds},
            {name: "Level", value: battlefield.level},
            {name: "Applied",  value: battlefield.usersApplied},
            {name: "Status",  value: battlefield.status},
        ];
            newBattle.attr("class","battleEntry");
            var battle = $("<table>");
            for (var i = 1; i < values.length; ++i) {
                battle.append('<tr>' + '<td>' + values[i].value + '</td>' + '</tr>');
            }
        if(battlefield.status === "open") {
            newBattle.attr("onclick", "selectBattle(this)");
        }
        var touppercase = battlefield.name.toUpperCase();
        newBattle.append('<p>' + touppercase + '</p>');
        newBattle.append(battle);
        newBattle.appendTo($("#battlefieldsContainer"));
    });
}

function selectBattle(battle) {
        battle.classList.remove("battleEntry");
        battle.classList.add("SelectBattleEntry");
        selectedBattle = battle.id;
    setTimeout(function () {
            $.ajax({
                url: ALLIES_LOBBY_URL,
                data: "choice=" + battle.id,
                dataType: 'json',
                success(message){
                    window.location.replace(message);
                }
            });
        }
    ,1000);
    return false;
}

function ajaxBattlefieldList() {
    $.ajax({
        url: ALLIES_LOBBY_URL,
        data: "battlefieldList=true",
        dataType: 'json',
        success: function(battlefields) {
            refreshAlliesLobby(battlefields);
        }
    });
}

function ajaxGetInfo(){
    $.ajax({
        url: ALLIES_LOBBY_URL,
        data: "info=true",
        dataType: 'json',
        success: function(ally) {
            var text = [
                "HELLO SR. " + ally.name.toUpperCase(),
                "YOUR PORT IS: " +ally.port,
                "PLEASE CALL YOUR AGENTS"
            ];
            showMsg(text,CHURCHIL_HEAD);
        }
    });
}
//
// function sayHello(ally){
//     var text = [
//         "HELLO SR. " + ally.name.toUpperCase(),
//         "YOUR PORT IS: " +ally.port,
//         "JOIN YOUR AGENT TO THE ARMY"
//         ];
//     showMsg(text);
// }

// noinspection JSAnnotator
$(function() {
    ajaxGetInfo();
    ajaxBattlefieldList();
    battlefieldListInterval = setInterval(ajaxBattlefieldList, refreshRate);
});

///////////////////////*      MSG Functions      */////////////////////////////////

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

function nextString(event) {
    if (event.keyCode === KEY_NEXT) {
        if (newMsgReady) {
            document.getElementById('msgContainer').style.display = "none";
        } else {
            printMsg();
        }
    }
}





// function showMsg(msg){
//     document.getElementById('msgContainer').style.display="block";
//     if(newMsgReady){
//         newMsgReady = false;
//         text = msg;
//         document.getElementById('nextMsg').style.display = "block";
//         printMsg();
//     }
// }
// function nextString(event) {
//     if (event.keyCode === KEY_NEXT) {
//         if (newMsgReady) {
//             document.getElementById('msgContainer').style.display = "none";
//         }
//         else{
//             printMsg();
//         }
//     }
// }
// function printMsg() {
//     if (text.length > 0) {
//         if (newLineReady) {
//             newLineReady = false;
//             currentLine = text[0].split('');
//             sendToTextBox();
//         }else{
//             NewMessageInterrupt = true;
//         }
//     }
//     else{
//         newMsgReady = true;
//         document.getElementById('nextMsg').style.display = "none";
//     }
// }
//
// function sendToTextBox() {
//     var container = document.getElementById("speechText");
//     container.innerHTML = "";
//     i = 0;
//
//     var interval = setInterval(function () {
//         if(NewMessageInterrupt){
//             container.innerHTML = text[0];
//             text.shift();
//             newLineReady = true;
//             NewMessageInterrupt = false;
//             clearInterval(interval);
//         }
//         if (i < currentLine.length) {
//             container.innerHTML += currentLine[i++];
//         }
//         else{
//             text.shift();
//             newLineReady = true;
//             NewMessageInterrupt = false;
//             clearInterval(interval);
//         }
//     }, PRINTING_SPEED);
// }
//
//
//
//

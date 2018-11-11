//import AlliesUser from '../../common/AlliesUser'
//import {createP} from "../../common/utils";
var UBOAT_FILEUPLOAD_URL = "/Enigma/pages/loadXML/fileUpload.html";
var UBOAT_BATTLE_URL = "./uboatBattle";
var END_GAME_URL = "/Enigma/endgame";
var LOGOUT = "logout";
var CONTINUE = "continue";
var ROUND = "round";
var UBOAT = "uboat";
var refreshRate = 400;
var readyInterval = null;
var listAlliesInterval = null;
var progressInterval = null;
var isGameOn = false;
var checkEmptyInterval;
var messagesBuffer = [];
var gameOver = false;


/////////**********************************************/
/*          CONSTANT MASSAGES                 */
/**********************************************/
var WELCOME_MSG = [
    'MESSAGE RECEIVED',
    "Zerstöre die Juden!!"
];


/////////**********************************************/
/*          VARIABLES FOR MASSAGES            */
/**********************************************/
var HITLER_HEAD = "hitlerHead";
var PRINTING_SPEED = 100;
var KEY_NEXT = 122;

var newMsgReady = true;
var newLineReady = true;
var currentLine = "";
var text = [];
var i = 0;


//----------------------------------------------on load funcs-----------------------------------------------------------

$(function() {
    showMsg(WELCOME_MSG);
    $("#endGameContainer").hide();
    ajaxAlliesList();
    ajaxNotifyAliensReady();
    listAlliesInterval = setInterval(ajaxAlliesList,refreshRate);
    readyInterval = setInterval(ajaxNotifyAliensReady,refreshRate);
});


//---------------------------------------------------ajax funcs---------------------------------------------------------

function ajaxNotifyAliensReady(){
    $.ajax({
        url: UBOAT_BATTLE_URL,
        data: "ready=true",
        dataType: 'json',
        success: function(message) {
            if(message === "true"){
                // alert("Ready interval cleared");
                clearInterval(listAlliesInterval);
                clearInterval(readyInterval);
                ajaxAlliesList();
                progressInterval = setInterval(ajaxGameProgress,refreshRate);
            }

        }
    });
    return false;
}

function ajaxGameProgress(){
    $.ajax({
        url: UBOAT_BATTLE_URL,
        data: "roundProgress=true",
        dataType: 'json',
        success: function(roundProgress) {
            if(roundProgress.winner != null){
                endRound();
                //renderEndGamePopUp(roundProgress.winner,roundProgress.currentRound);
            } else if(roundProgress.candidateMSG != null){
                for(var i = 0; i < roundProgress.candidateMSG.length;i++) {
                    messagesBuffer.push(roundProgress.candidateMSG[i]);
                }
                printAllCandidate();
            }
        }
    });
    return false;
}

function ajaxRoundOver() {
    $.ajax({
        url: UBOAT_BATTLE_URL,
        data: "gameOver=true"
    });
}

function ajaxAlliesList() {
    $.ajax({
        url: UBOAT_BATTLE_URL,
        data: "alliesData=true",
        dataType: 'json',
        success: function(allies) {
            renderAlliesDisplay(allies);
        }
    });
    return false;
}


//------------------------------------------------render funcs----------------------------------------------------------


function renderNewMassages(massages){
    $.each(massages || [], function( i , massage) {
        messagesBuffer.add(massage);
    });
}

function renderAlliesDisplay(allies) {
    $("#alliesContainer").empty();
    $.each(allies || [], function (i, ally) {
        var id = ally.name;
        var newAllyEntry = $("<div class='allyEntry' id='" + id + "' >");
        var Username = document.createElement('P');
        var NumberAgents = document.createElement('P');
        var allyReady = document.createElement('P');
        var UserNameContainer = document.createElement('DIV');
        UserNameContainer.setAttribute("class","userNameContainer");
        UserNameContainer.setAttribute("id","userNameContainer");
        Username.setAttribute("class", "username");
        UserNameContainer.append(Username);
        Username.innerText = ally.name;
        NumberAgents.setAttribute("class", "numberAgents");
        NumberAgents.innerText = ally.agents;
        allyReady.setAttribute("class", "allyReady");
        allyReady.innerText = ((ally.ready == true) ? "Ready!" : "");
        newAllyEntry.append(UserNameContainer).append(NumberAgents).append(allyReady);
        newAllyEntry.appendTo($("#alliesContainer"));
    });
}



//-----------------------------------------------------other------------------------------------------------------------

function initRound() {
    ajaxAlliesList();
}

// function createInputBar(_action, _name){
//     var inputContainer = $("#inputContainer");
//     var form = $("<form mathod = GET>");
//     var input = $("<input type = text>");
//     var submit = $("<input type = submit>");
//
//     form.attr("action",_action);
//     input.attr("name",_name);
//     input.attr("class",); //check
//     submit.attr("value",_action);
//
//     submit.click()
//
//     form.append(input);
//     form.append(submit);
//     inputContainer.append(form);
// }
//
// function removeInputBar(_from){$( "#"+_from).empty(); }
//
// function ajaxAgentsProgress() {
//     $.ajax({
//         url: ALLIES_BATTLE_URL,
//         data: "agents",
//         dataType: 'json',
//         success: function(agentsProgress) {
//             refreshAgentsProgress(agentsProgress);
//         }
//     });
// }
function endRound(){
    isGameOn = false;
    clearInterval(progressInterval);
    $.ajax({
        url: END_GAME_URL,
        data: ROUND +"=true",
        dataType: 'json',
        success: function(message) {
            if(message.status === true){
                showMsg(["The winner in round " + message.payload.round + " is "+ message.payload.winner],HITLER_HEAD);
                var nextRoundButton = document.getElementById("next-round-button");
                nextRoundButton.style.display = "block";
            }
            else{
                renderEndGamePopUp(message.payload);
            }


        },
        error: function() {
            console.error("Failed to submit");
        }
    });
    return false;
}

function goToNextRound(){
    window.location.replace(UBOAT_FILEUPLOAD_URL);
}

function continueFunc() {
    $.ajax({
        url: END_GAME_URL,
        data: CONTINUE +"=true",
        dataType: 'json',
        success: function(message) {
            if(message.status === true){
                //Game Over
                window.location.replace(message.payload);
            }
        },
        error: function() {
            console.error("Failed to submit");
        }
    });
    return false;
}

function Logout() {
    $.ajax({
        url: END_GAME_URL,
        data: LOGOUT +"=true",
        dataType: 'json',
        success: function(message) {
            if(message.status === true){
                //Game Over
                window.location.replace(message.payload);
            }
        },
        error: function() {
            console.error("Failed to submit");
        }
    });
    return false;
}



var START_LEFT_POSITION_HEAD = 72;
var DEFAULT_HEAD_POSITION = 53;
var STEP_LEFT_POSITION = -1;
var STEP_PAGE_TOP = 9.32;
var STEP_PAGE_HEIGHT = 10;
var MAX_HEIGHT = 152;
var MAX_TOP = -59;
var MIN_TOP = 61;
var MIN_HEIGHT = 33;
var TYPEING_SPEED = 20;
var currentLine = "helloWorld";
var currentString;
var currentLeft = 12;
var currentTop = 50;
var currentHeight = 33;
var autoScroll = true;
var i = 0;
var  j = 0;
var printReady = true;
var bufferNotEmpty = false;


function splitMsg() {
    var userName = currentLine.substr(0,currentLine.indexOf(">"));
    var msg = currentLine.substr(currentLine.indexOf(">"),currentLine.length);
    console.log(userName + " " + msg);
    var userName = document.createElement("p");
    var msg = document.createElement("p");
    userName.setAttribute("class","msgUserName");
    msg.setAttribute("class","msgContent");
    var container = document.getElementById("pageText");
    container.append(userName).append(msg);
}

function printAllCandidate() {
    if(bufferNotEmpty) {
        return;
    }
    bufferNotEmpty = true;
    var printInterval = setInterval(function () {
        if (messagesBuffer.length > 0 && gameOver === false) {
            if (printReady) {
                printReady = false;
                currentLine = messagesBuffer[0];
                printCandidate();
            }
        } else {
            bufferNotEmpty = false;
            clearInterval(printInterval);
        }

    }, 500);
}
var type;
var name = true;

function printCandidate() {
    var head = document.getElementById("head");
    var page = document.getElementById('page');
    var container = document.getElementById("pageText");
    i = 0;
    head.style.left = START_LEFT_POSITION_HEAD + "%";
    var userNameIndex = currentLine.toString().indexOf(">");
    var span = document.createElement("span");
    span.setAttribute("style","color:red");
    container.appendChild(span);
    var printingInterval = setInterval(function () {
        if(gameOver){
            clearInterval(printingInterval);
            container.innerHTML = currentLine.toString();
            return;
        }
        else if(i === 0 && autoScroll){
            page.scrollTop = page.scrollHeight;
            autoScroll = false;
        }
        else if( i < currentLine.length) {
            if(i < userNameIndex) {
                container.lastElementChild.innerHTML += currentLine[i++];
            }else {
                container.innerHTML += currentLine[i++];
                currentLeft += STEP_LEFT_POSITION;
                head.style.left = currentLeft + "%";
            }
        }
        else{
            if(currentHeight < MAX_HEIGHT) {
                currentHeight += STEP_PAGE_HEIGHT;
                page.style.height = currentHeight + "%";
            }
            if(currentTop > MAX_TOP){
                currentTop -= STEP_PAGE_TOP;
                page.style.top = currentTop + "%";
            }else{
                page.style.overflow = "auto";
            }
            currentLeft = START_LEFT_POSITION_HEAD;
            page.scrollTop = page.scrollHeight;
            head.style.left = DEFAULT_HEAD_POSITION + "%";
            messagesBuffer.shift();
            clearInterval(printingInterval);
            document.getElementById('pageText').innerHTML += '<br />';
            printReady = true;
        }
    }, TYPEING_SPEED);
}
var sum = {
    "winner":"dfggf",
    "rank":[
        "ssdfg",
        "dasfgdas",
        "sdfgfds"
    ]

}

function renderEndGamePopUp(summery) {
    var container = document.getElementById("endGameContainer");

    container.style.display = "block";
    var rankList = document.createElement('DIV');
    var winner = document.createElement('DIV');
    winner.innerText = summery.status;
    if( summery.winner != null){
        winner.innerHTML +='<br />' + summery.winner;
    }

    for (var i = 0; i < summery.rank.length; ++i) {
        var ranki = document.createElement('P')
        ranki.innerHTML = summery.rank[i];
        rankList.appendChild(ranki);
    }
    var title = document.createElement('P')
    title.innerText = "Game Summery:";
    container.appendChild(title).appendChild(winner).appendChild(rankList);
}

function renderNextRoundPopUp(winner,round) {
    var container = document.getElementById("endGameContainer");
    var logout = document.getElementById("logout-button");
    var winner = document.createElement('P');
    var title = document.createElement('P')

    container.style.display = "block";
    logout.style.display = "none";
    title.innerText = "Round Summery";
    winner.innerText = ""


    container.appendChild(title).appendChild(winner).appendChild(rankList);
}



///////////////////////*      MSG Functions      */////////////////////////////////
function showMsg(msg){
    document.getElementById('msgContainer').style.display="block";
    if(newMsgReady){
        newMsgReady = false;
        text = msg;
        document.getElementById('nextMsg').style.display = "block";
        printMsg();
    }
}
function nextString(event) {
    if (event.keyCode === KEY_NEXT) {
        if (newMsgReady) {
            document.getElementById('msgContainer').style.display = "none";
        }
        else{
            printMsg();
        }
    }
}

function printMsg() {
    if (text.length > 0) {
        if (newLineReady) {
            newLineReady = false;
            currentLine = text[0].split('');
            sendToTextBox();
        }else{
            NewMessageInterrupt = true;
        }
    }
    else{
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
            NewMessageInterrupt = false;
            clearInterval(interval);
        }
        if (i < currentLine.length) {
            container.innerHTML += currentLine[i++];
        }
        else {
            text.shift();
            newLineReady = true;
            NewMessageInterrupt = false;
            clearInterval(interval);
        }
    }, PRINTING_SPEED);
}
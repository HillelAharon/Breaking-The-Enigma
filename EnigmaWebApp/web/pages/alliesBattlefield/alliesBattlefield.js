var ALLIES_BATTLE_URL = "./alliesBattle";
var END_GAME_URL = "/Enigma/endgame";
var LOGOUT = "logout";
var CONTINUE = "continue";
var ROUND = "round";
var refreshRate = 500;
var progressInterval = null;
var alliesDataInterval = null;
var isGameOn = false;
var endGamePopUp;
var PORT_MSG = "Your port for agent connection is: ";

/////////**********************************************/
/*                    MASSAGES                 */
/**********************************************/
var CHURCHILL_HEAD = "churchillHead";
var PRINTING_SPEED = 100;
var KEY_NEXT = 122;

var newMsgReady = true;
var newLineReady = true;
var newMessageInterrupt = false;
var currentLine = "";
var text = [];
var i = 0;


//-----------------------------------------------on load funcs----------------------------------------------------------


$(function() {
    $("#missionSizeForm").submit(function() {
        $.ajax({
            url: ALLIES_BATTLE_URL,
            data: $(this).serialize(),
            dataType: 'json',
            success: function(message) {
                renderReadyButton();
            },
            error: function() {
                console.error("Failed to submit");
            }
        });
        return false;
    });
    ajaxGetInfo();
    alliesDataInterval = setInterval(ajaxAlliesList,refreshRate);

});


//-------------------------------------------------ajax funcs-----------------------------------------------------------

function ajaxNotifyReady(){
    $.ajax({
        url: ALLIES_BATTLE_URL,
        data: "ready=true",
        dataType: 'json',
        success: function(message) {
            //showMsg([message],CHURCHILL_HEAD);
            if(message === "Confirm") {
                progressInterval = setInterval(ajaxGameProgress, refreshRate);
            }
        },
        error: function() {
            showMsg(["Failed to submit"],CHURCHILL_HEAD);
        }
    });
    return false;
}

function ajaxGameProgress(){
    $.ajax({
        url: ALLIES_BATTLE_URL,
        data: "roundProgress=true",
        dataType: 'json',
        success: function(roundProgress) {
            if(isGameOn === false && roundProgress.startGame === true){
                isGameOn = true;
                initRound();
            }
            else if(roundProgress.winner != null){
                endRound();
                //renderEndGamePopUp(roundProgress.winner,roundProgress.currentRound);
            }
            else if(roundProgress.agentsProgress != null){
                renderAgentsProgress(roundProgress.agentsProgress,roundProgress.remainingMissions);
            }
        },
        error: function() {
            console.error("Failed to submit");
        }
    });
}

function ajaxAlliesList() {
    $.ajax({
        url: ALLIES_BATTLE_URL,
        data: "alliesData=true",
        dataType: 'json',
        success: function(allies) {
            renderAlliesDisplay(allies);
        }
    });
    return false;
}

function ajaxGetInfo(){
    $.ajax({
        url: ALLIES_BATTLE_URL,
        data: "info=true",
        dataType: 'json',
        success: function(ally) {
            sayHello();
        }
    });
}
function sayHello(){
    var text = [
        "CLICK ON THE TYPEWRITER",
        "THAN ON NOTE TO SET MISSION SIZE",
        "AND PRESS ENTER TO CONFIRM",
        "FOR INFORMATION ABOUT AGENTS FLIP THE PAGE",
        "THE RED SQUARE ON THE DESK FOR READY"
    ];
    showMsg(text,CHURCHILL_HEAD);
}

//-----------------------------------------------render funcs-----------------------------------------------------------

function renderReadyButton(){
    document.getElementById("readyButton").style.display = "block";
}

function renderAgentsProgress(agentsCandidates,remainingMissions) {
    $("#agentsContainer").empty();

    document.getElementById("missionLeft").innerHTML = remainingMissions;
    for(var i = 0; i < agentsCandidates.length;i++){
        var id = "agent" + i;
        var newAgentsEntry = $("<div class='agentsEntry' id='"+ id + "'>");
        var agentName = document.createElement('P');
        var agentMissions = document.createElement('P');
        agentName.setAttribute("class","agent");
        agentName.innerText = i.toString();
        agentMissions.setAttribute("class","agentMissions");
        agentMissions.innerText = agentsCandidates[i];
        newAgentsEntry.append(agentName).append(agentMissions);
        newAgentsEntry.appendTo($("#agentsContainer"));
    }
}

function showBFName(ally) {
    var BF_Name = document.createElement("div");
    BF_Name.setAttribute("class","bf_name");
    BF_Name.innerHTML = ally.battlename.toUpperCase() + " YOUR PORT: " + ally.port;
    document.getElementById("background").appendChild(BF_Name);
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

//-----------------------------------------------------other------------------------------------------------------------
function createP(content){ return $('<p>' + content + '</p>'); }
function initRound() {
    ajaxAlliesList();
    clearInterval(alliesDataInterval);
}


function endRound(){
    isGameOn = false;
    clearInterval(progressInterval);
    $.ajax({
        url: END_GAME_URL,
        data: ROUND +"=true",
        dataType: 'json',
        success: function(message) {
            if(message.status === true){
                var msgArr = ["The winner in round " + message.payload.round + " is "+ message.payload.winner];
                showMsg(msgArr,CHURCHILL_HEAD);
                $("#agentsContainer").empty();
                document.getElementById("missionLeft").innerHTML = "";
                alliesDataInterval = setInterval(ajaxAlliesList,refreshRate);
                //window.location.replace(ALLIES_BATTLEFIELD_URL);
                //Go to next round
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

function goToLobby() {
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




//////////////////////
var openNote = document.getElementById('openNote');
var statusNote = document.getElementById('noteNextPage');
var noteContainer = document.getElementById('notepadContainer');
var missionsContainer = document.getElementById('missionsContainer');
var missionSize = document.getElementById('missionSize');
var inWindow = false;


function testingTarget(event) {
    var statusNote = document.getElementById('noteNextPage');
    var openNote = document.getElementById('openNote');

    if (event.target.className === "inputText") {
        $(missionSize).attr("value","");
        document.getElementById('missionSize').classList.add("selectedText");
    }
    if (event.target.className === "background-black") {
        if (openNote.style.display === "block") {
            openNote.style.display = "none";
        }
        else if (statusNote.style.display === "block") {
            openNote.style.display = "none";
        }
        else {
            missionsContainer.style.display = "none";
            document.getElementById('background').style.display = "none";
        }
    }
}

function onHover(button) {
    inWindow = true;
    document.getElementById(button).style.display = "block";
}

function onLeave(button) {
    inWindow = false;
    document.getElementById(button).style.display = "none";

}
function showTypeWriter(){
    document.getElementById('background').style.display = "block";
    document.getElementById('missionsContainer').style.display = 'block';
}

function openNotepad() {
    var openNote = document.getElementById('openNote');

    document.getElementById('notepadContainer').style.display = "block";
    openNote.classList.add('noteAnimation');
    openNote.style.display = "block";
    setTimeout(function(){
        openNote.classList.remove('noteAnimation');
    }, 2200);

}
function nextPageNote() {
    var statusNote = document.getElementById('noteNextPage');
    var openNote = document.getElementById('openNote');

    if(statusNote.style.display === 'none'){
        statusNote.style.display = "block";
        openNote.style.display = "none";
    }
    else{
        statusNote.style.display = "none";
        openNote.style.display = "block";
    }
}


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
            if(text.length > 0){
                //text.slice(1,text.length - 1);
                text.shift();
            }
            newLineReady = true;
            newMessageInterrupt = false;
            clearInterval(interval);
        }
        if (i < currentLine.length) {
            container.innerHTML += currentLine[i++];
        }
        else {
            if(text.length > 0){
                //text.slice(1,text.length - 1);
                text.shift();
            }
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

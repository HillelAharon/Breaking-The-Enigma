/////////**********************************************/
/*             CONSTANT URL'S                 */
/**********************************************/
var UPLOAD_AND_SETTING_URL = "./uploadAndSetting";

////////**********************************************/
/*            VARIABLES FOR SECRET            */
/**********************************************/
var Secret = {};
var Secret_Notches = [];
var Secret_NotchesCount = 0;
var Secret_rotorsCount = 0;
var Secret_Rotors = [];
var Secret_Reflector = "";
////////**********************************************/
/*       VARIABLES TO STORE INCOME DATA       */
/**********************************************/
var machine;


/////////**********************************************/
/*          CONSTANT MASSAGES                 */
/**********************************************/
var WELCOME_MSG = [
    'WELCOME ON BOARD!',
    'WE NEED YOUR HELP!',
    'WE LOST OUR RADIOMAN. PLEASE SET THE MACHINE',
    "TO START, OPEN THE MACHINE AND SELECT UPLOAD",
    "TO SELECT NOTCHES AND MESSAGE",
    "CLICK ON SET NOTCHES"
];


var REACH_MAX_ROTORS = [
    'YOU REACH TO THE LIMITS OF YOUR ROTOR',
    'IF YOU WISH TO REPLACE THE ROTORS YOU NEED ',
    'FIRST REMOVE OLD ROTORS THEN SELECT NEW'
];
var MACHINE_ALREADY_LOADED = [
    'MACHINE EXIST PLEASE SET SECRET AND MSG'  // CHANGE ME!
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


////////////////////////////////
var rotorCarouselIndex = 0;
var reflectorCarouselIndex = 0;

/////////**********************************************/
/*           SERVER COMMUNICATIONS            */
/**********************************************/

$(function() {
    $("#file").change(function() {
        var file = this.files[0];
        var formData = new FormData();
        formData.append("file", file);
        $.ajax({
            method: 'POST',
            data: formData,
            url: UPLOAD_AND_SETTING_URL,
            processData: false, // Don't process the files
            contentType: false, // Set content type to false as jQuery will tell the server its a query string request
            timeout: 4000,
            success: function(data) {
                if(data.status === true){
                    machine = data.payload
                    initialMachine();
                }
                else
                    showMsg(data.payload,HITLER_HEAD);
            },
            error: function (e) {
                console.log("Failed to submit");
                $("#result").text("Failed to get result from server " + e);
            }
        });

        // return value of the submit operation
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });

    $("#submitMachine").submit(function() {
        if(!buildMachine())
            return false;
        var data = JSON.stringify(Secret);
        $.ajax({
            type: 'POST',
            data:"data=" + data,
            url: this.action,
            dataType: 'json',
            success: function(message) {
                if(message.status === true) {
                    window.location.replace(message.payload);
                }
                else
                    showMsg(message.payload,HITLER_HEAD);
            },
            error: function() {
                showMsg(["Failed to submit"],HITLER_HEAD);
            }
        });
        return false;
    });

    $.ajax({
        type: 'POST',
        data:"check=true",
        dataType: 'json',
        url: UPLOAD_AND_SETTING_URL,
        success: function(data) {
            if(data.status === true){
                machine = data.payload
                initialMachine();
                loadSecret(data.payload.secretAndMsg);
                showMsg(MACHINE_ALREADY_LOADED,HITLER_HEAD);
            } else {
                showMsg(WELCOME_MSG,HITLER_HEAD);
            }
        },
        error: function (e) {
            $("#result").text("Failed to get result from server " + e);
        }

    });
});



/////////**********************************************/
/*           ANIMATIONS AND DESIGN            */
/**********************************************/
function setMachineCover(open) {
    var openMachineContainer = document.getElementById("openMachineContainer");
    var closeMachineContainer = document.getElementById("closeMachineContainer");
    var cover = document.getElementById("machineCover");
    var coverOpen = document.createElement('DIV');
    var coverClose = document.createElement('DIV');

    if(open) {
        openMachineContainer.style.display = "block";
        closeMachineContainer.style.display = "none";
        closeMachineContainer.removeChild(cover);
        coverOpen.setAttribute("class","coverOpen");
        coverOpen.setAttribute("id","machineCover");
        openMachineContainer.appendChild(coverOpen);

    }else{
        openMachineContainer.style.display = "none";
        closeMachineContainer.style.display = "grid";
        openMachineContainer.removeChild(cover);
        coverClose.setAttribute("id","machineCover");
        coverClose.setAttribute("class","coverClose");
        closeMachineContainer.insertAdjacentElement('afterbegin', coverClose)
    }
}
////////////////////*      Carousels Functions      *///////////////////////////

function notchCarousel(direction,notchItem) {
    var abcItem = $(notchItem).closest('div').attr('id');
    var itemId = abcItem.toString().replace("abcItem","");
    var abcId = "abcList" + itemId;
    var abcItem = Secret_Notches[itemId];
    var abcIndex = machine.ABC.indexOf(abcItem);
    abcIndex += direction;
    if(abcIndex < 0){
        abcIndex = machine.ABC.length - 1;
    }
    else if (abcIndex === machine.ABC.length) {
        abcIndex = 0;
    }
    Secret_Notches[itemId] = machine.ABC[abcIndex];
    document.getElementById(abcId).innerText = machine.ABC[abcIndex];
}

function rotorsCarousel(direction) {
    var rotors = document.getElementsByClassName("rotor");
    rotorCarouselIndex += direction;

    if (rotorCarouselIndex > rotors.length -1) {
        rotorCarouselIndex = 0
    }
    if (rotorCarouselIndex < 0) {
        rotorCarouselIndex = rotors.length - 1;
    }
    for (var i = 0; i < rotors.length; i++) {
        rotors[i].style.display = "none";
    }
    rotors[rotorCarouselIndex].style.display = "block";
}

function reflectorCarousel(direction) {
    reflectorCarouselIndex += direction;
    var reflectors = document.getElementsByClassName("reflector");
    if (reflectorCarouselIndex > reflectors.length -1) {
        reflectorCarouselIndex = 0;
    }
    if (reflectorCarouselIndex < 0) {
        reflectorCarouselIndex = reflectors.length - 1;
    }
    for (var i = 0; i < reflectors.length; i++) {
        reflectors[i].style.display = "none";
    }
    reflectors[reflectorCarouselIndex].style.display = "block";
}

/////////**********************************************/
/*      LOAD MACHINE AND SECRET               */
/**********************************************/

function initialMachine() {

    clearLastMachine();
    var rotors = machine.rotorIds;
    var reflectors = machine.reflectorIds;
    for (var i_rotors = 0; i_rotors < rotors.length; i_rotors++) {
        var rotor = document.createElement("INPUT");
        var currentRotor = rotors[i_rotors];
        rotor.setAttribute("type", "button");
        rotor.setAttribute("value", currentRotor);
        rotor.setAttribute("id", "rotor" + currentRotor);
        rotor.setAttribute("class", "rotor");
        rotor.setAttribute("onclick", "selectRotor(this)");
        document.getElementById("unselectedRotorsContainer").appendChild(rotor);
    }
    for(var i_reflector = 0; i_reflector < reflectors.length; i_reflector++){
        var reflector = document.createElement("INPUT");
        reflector.setAttribute("type", "button");
        reflector.setAttribute("value",reflectors[i_reflector]);
        reflector.setAttribute("id","reflector" +reflectors[i_reflector]);
        reflector.setAttribute("class","reflector");
        reflector.setAttribute("onclick","selectReflector(this)");
        document.getElementById("unselectedReflectorsContainer").appendChild(reflector);
    }
    //document.getElementById("clickedChoose").setAttribute("onclick","showMsg(MACHINE_ALREADY_LOADED)");
    updateRotorsCount();
}

function clearLastMachine(){
    $("#unselectedRotorsContainer").empty();
    $("#unselectedRotorsContainer")
        .append("<input type=\"button\" value=\"\"  id=\"leftRotor\" class=\"leftRotorButton\"  onclick=\"rotorsCarousel(-1);\"/>")
        .append(" <input type=\"button\" value=\"\" id=\"rightRotor\" class=\"rightRotorButton\"  onclick=\"rotorsCarousel(1);\"/>");

    $("#unselectedReflectorsContainer").empty();
    $("#unselectedReflectorsContainer")
        .append("<input type=\"button\" value=\"\" id=\"leftReflector\" class=\"leftReflectorButton\" onclick=\"reflectorCarousel(-1)\"/>")
        .append("<input type=\"button\" value=\"\" id=\"rightReflector\" class=\"rightReflectorButton\"  onclick=\"reflectorCarousel(1)\"/>");
    $("#selectedReflectorsContainer").empty();
    $("#selectedRotorsContainer").empty();
    $("#selectedRotorsContainer").append("<p class=\"rotorsLeft\" id=\"rotorsLeft\"></p>");
    $("#abcContainer").empty();

    Secret_NotchesCount = 0;
    Secret_rotorsCount = 0;
    Secret = {};
    Secret_Notches = [];
    Secret_Rotors = [];
    Secret_Reflector = "";
}

function buildMachine() {
    if(machine == null){
        showMsg(["Error: Please upload file first"],HITLER_HEAD);
        return false;
    }
        
    if(Secret_rotorsCount != machine.rotorInUse){
        var text = [ "Error: Need to select " + machine.rotorInUse,
            " rotor before send the secret"
        ]
        showMsg(text,HITLER_HEAD);
        return false;
    }
        
    if(Secret_Reflector == "" ){
        showMsg(["Error: reflector didn't selected"],HITLER_HEAD);
        return false;
    }
        

    var msg = document.getElementById("msg").value;
    if(msg == ""){
        showMsg(["Error: message cant be null"],HITLER_HEAD);
        return false;
    }
        
    Secret["rotorsIds"] = Secret_Rotors;
    Secret["reflector"] = Secret_Reflector.replace("reflector","");
    Secret["notchs"] = Secret_Notches;
    Secret["msg"] = msg;
    // console.log(Secret);
    return true;
}

function loadSecret(SecretAndMsg) {
    for(var i = 0;i < SecretAndMsg.rotorsIds.length; i++){
        var rotorId = "rotor" + SecretAndMsg.rotorsIds[i];
        selectRotor(document.getElementById(rotorId));
    }
    var reflectorId = "reflector" + SecretAndMsg.reflector;
    selectReflector(document.getElementById(reflectorId));

    for(var i = 0 ;i < SecretAndMsg.notchs.length ; ++i){
        var notchs = document.getElementById("abcList" + i).innerText = machine.ABC[SecretAndMsg.notchs[i]];
    }
    document.getElementById("clickedChoose").setAttribute("onclick","showMsg(MACHINE_ALREADY_LOADED,HITLER_HEAD)");
}


////////////////////////SELECT OBJECTS/////////////////////////////////////////
function selectRotor(r) {
    var id = $(r).attr('id');
    var currentRotor = document.getElementById(id);
    var parent = $(r).parent();
    if (parent.attr('class') === "unselectedRotorsContainer") {
        if (Secret_rotorsCount < machine.rotorInUse) {
            Secret_rotorsCount++;
            currentRotor.parentNode.removeChild(currentRotor);
            Secret_Rotors.push(id.replace("rotor",""));
            currentRotor.style.display = "block";
            rotorsCarousel(-1);

            document.getElementById("selectedRotorsContainer").insertAdjacentElement('afterbegin',currentRotor);
            $(r).toggleClass("selectedRotor");
            $(r).toggleClass("rotor");
            document.getElementById(id).style.animation = "selectRotor 1s";
            addNotch();
        }
        else {
            showMsg(REACH_MAX_ROTORS,HITLER_HEAD);
        }
    }
    else {
        Secret_rotorsCount--;
        document.getElementById(id).style.animation = "";
        removeNotch();
        currentRotor.parentNode.removeChild(currentRotor);
        Secret_Rotors.pop();
        document.getElementById("unselectedRotorsContainer").appendChild(r);
        $(r).toggleClass("rotor");
        $(r).toggleClass("selectedRotor");
    }
    updateRotorsCount();
}
function updateRotorsCount() {
    var left = machine.rotorInUse - Secret_rotorsCount;
    if(left === 0){left = "";}
    document.getElementById("rotorsLeft").innerText = left;
}

function selectReflector(r) {
    var id = $(r).attr('id');
    var currentReflector = document.getElementById(id);
    var parent = $(r).parent();
    if (parent.attr('class') === "unselectedReflectorsContainer")
    {
        if(Secret_Reflector !== ""){
            selectReflector(document.getElementById(Secret_Reflector));
        }
        currentReflector.parentNode.removeChild(currentReflector);
        Secret_Reflector = id;
        reflectorCarousel(-1);
        currentReflector.style.display = "block";
        document.getElementById("selectedReflectorsContainer").appendChild(r);

    }else{
        currentReflector.parentNode.removeChild(currentReflector);
        Secret_Reflector = "";
        document.getElementById("unselectedReflectorsContainer").appendChild(r);
    }
    $(r).toggleClass("selectedReflector");
    $(r).toggleClass("reflector");
}

function addNotch() {
    var preSetChar = machine.ABC[0];

    Secret_Notches.push(preSetChar.toString());
    var notchUp = document.createElement('INPUT');
    var notchDown = document.createElement('INPUT');
    var notchChar = document.createElement('DIV');
    var abcItem = document.createElement('DIV');
    notchUp.setAttribute("class","notchUp");
    notchUp.setAttribute("type", "button");
    notchUp.setAttribute("value","up");
    notchUp.setAttribute("onclick","notchCarousel(1,this)");

    notchDown.setAttribute("class","notchDown");
    notchDown.setAttribute("type", "button");
    notchDown.setAttribute("value","down");
    notchDown.setAttribute("onclick","notchCarousel(-1,this)");
    notchChar.setAttribute("id","abcList" + Secret_NotchesCount);
    notchChar.setAttribute("class","SelectedAbc");
    notchChar.innerText = preSetChar;
    abcItem.setAttribute("class","abcItem");
    abcItem.setAttribute("id","abcItem" + Secret_NotchesCount);
    abcItem.appendChild(notchUp);
    abcItem.appendChild(notchChar);
    abcItem.appendChild(notchDown);
    console.log(abcItem);
    document.getElementById("abcContainer").appendChild(abcItem);
    Secret_NotchesCount++;
}

function removeNotch() {
    Secret_Notches.pop();
    Secret_NotchesCount--;
    document.getElementById("abcContainer").removeChild(document.getElementById("abcItem" + Secret_NotchesCount));
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
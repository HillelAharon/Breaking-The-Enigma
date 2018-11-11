var ENLISMENT_URL = "./choice";


$(function(){
    $("left").click(function(){
        ajaxEnlismentUpdate("allies");
    });
    $("right").click(function(){
        ajaxEnlismentUpdate("uboat");
    });
});


function ajaxEnlismentUpdate(userChoice) {
    $.ajax({
        url: ENLISMENT_URL,
        data: "choice=" + userChoice,
        dataType: 'json',
        success(message){
            window.location.replace(message);
        }
    });
}
//
// $(function () {
//     $.ajax({
//         url: SIGNUP_URL,
//         dataType: 'json',
//         success: function(message) {
//             window.location.replace( message.payload);
//         },
//         error: function() {
//             console.error("Failed to submit");
//
//         }
//     });
//     return false;
// });

var SIGNUP_URL = "./pages/signup/login";
$(function () {
    $.ajax({
        url: SIGNUP_URL,
        dataType: 'json',
        success: function(message) {
                window.location.replace( message.payload);
        },
        error: function() {
            console.error("Failed to submit");

        }
    });
    return false;
});
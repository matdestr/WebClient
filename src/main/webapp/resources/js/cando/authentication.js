
function signInClick() {
    $("#form-sign-in").delay(100).fadeIn(100);
    $("#form-sign-up").fadeOut(100);
    $('#form-sign-up-link').removeClass('active');
    $('#form-sign-in-link').addClass('active');
    preventDefault();
}

function signUpClick(){
    $("#form-sign-up").delay(100).fadeIn(100);
    $("#form-sign-in").fadeOut(100);
    $('#form-sign-in-link').removeClass('active');
    $('#form-sign-up-link').addClass('active');
    preventDefault();
}


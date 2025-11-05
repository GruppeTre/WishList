function onChange() {

    let alertText;
    if(navigator.language.includes("da")) {
        alertText = "Kodeord matcher ikke"
    } else{
        alertText = "Passwords don't match"
    }

    const password = document.querySelector('input[id=password]');
    const confirm = document.querySelector('input[id=confirm-password]');
    if (confirm.value === password.value) {
        confirm.setCustomValidity('');
    } else {
        confirm.setCustomValidity(alertText);
    }
}
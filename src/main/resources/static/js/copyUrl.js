function copyUrl() {
    // Get the text field
    let copyText = document.getElementById("listLink");

    // Select the text field
    copyText.select();

    // Copy the text inside the text field
    navigator.clipboard.writeText(copyText.value);

    let alertText;
    if (navigator.language.includes("da")) {
        alertText = "Link kopieret: "
    } else {
        alertText = "Copied to clipboard: "
    }

    // Alert the copied text
    alert(alertText + copyText.value);
}
$(document).ready(function() {
    updateCounters();
    setTimeout('refreshPage();', 300000);
});

function refreshPage() {
    location.reload();
}

function fileUpload() {
    $('#info_span').text("Uploading file").show().fadeOut(5000);
    setTimeout('refreshPage();', 2000);
    //$tabs.tabs('select', '#' + ui.panel.id);
}

function updateCounters() {
    message("route=receive&attribute=ExchangesCompleted", "receivedCount");
    message("route=receipt&attribute=ExchangesCompleted", "receiptCount");
    message("route=balance&attribute=ExchangesCompleted", "balanceCount");
    message("route=receipt&attribute=ExchangesCompleted", "clearingCount");
    setTimeout("updateCounters()", 1000);
}

function message(requestString, id) {
    var http = new XMLHttpRequest();
    http.open("GET", "../service/jmxrest?" + requestString, true);
    http.onreadystatechange = function() {
        if (http.readyState == 4) {
            document.getElementById(id).innerHTML = http.responseText;
        }
    };
    http.send(null);
}
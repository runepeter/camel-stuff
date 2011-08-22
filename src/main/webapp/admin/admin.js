$(document).ready(function() {
    updateCounters();

    updateDatabaseNumbers();
});



function fileUpload() {
    $('#info_span').text("Uploading file").show().fadeOut(5000);
   // setTimeout('refreshPage();', 2000);
    //$tabs.tabs('select', '#' + ui.panel.id);
}

function loadOkFile() {
    
}

function updateCounters() {
    message("route=receive&attribute=ExchangesTotal", "receivedCount");
    message("route=receipt&attribute=ExchangesTotal", "receiptCount");
    message("route=balance&attribute=ExchangesTotal", "balanceCount");
    message("route=clearing&attribute=ExchangesTotal", "clearingCount");
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

function updateDatabaseNumbers() {
    /*
    $.get('../service/fil/antall/', function(data) {
        $('#numFilDb').html(data);
    });
    $.get('../service/transaction/antall/', function(data) {
        $('#numTraDb').html(data);
    });
    $.get('../service/balance/antall/', function(data) {
        $('#numBalanceDb').html(data);
    });
    */

    $.get('../service/balance/saldo/', function(data) {
        $('#saldo').html(data);
    });
    $.get('../service/balance/reserved/', function(data) {
        $('#reserved').html(data);
    });
    setTimeout('updateDatabaseNumbers()', 500);

}

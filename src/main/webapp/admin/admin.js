$(document).ready(function() {
    updateCounters();
    updateDatabaseNumbers();
    updateMessage();
});



function fileUpload() {
    $('#info_span').text("Uploading file");
   // setTimeout('refreshPage();', 2000);
    //$tabs.tabs('select', '#' + ui.panel.id);
}

function loadOkFile() {
    
}

function updateCounters() {
    message("route=receive&attribute=ExchangesTotal", "receivedCount");
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

    $.get('../service/balance/saldo/', function(data) {
        s = ""+data
        if (data>999999) {
            s = s.substring(0,s.length-6)+" "+s.substring(s.length-6, s.length)
        }

        if (data > 999) {
            s = s.substring(0, s.length-3)+" "+s.substring(s.length-3, s.length)
        }
        $('#saldo').html(s);
    });
    $.get('../service/balance/reserved/', function(data) {
        j = ""+data
        if (data>999999) {
            j = j.substring(0,j.length-6)+" "+j.substring(j.length-6, j.length)
        }

        if (data > 999) {
            j = j.substring(0, j.length-3)+" "+j.substring(j.length-3, j.length)
        }
        $('#reserved').html(j);
    });
    setTimeout('updateDatabaseNumbers()', 500);

}

function updateMessage() {
    $.get('../service/message/', function(data) {
        $('#info_span').html(data);
    });
    setTimeout('updateMessage()', 5000);
}
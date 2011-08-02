
$(document).ready(function() {
    $("#tabs").tabs();
    updateFilTable();
    updateCounters();
    updateCSMTable();
    setTimeout('refreshPage();', 300000);
});

function refreshPage() {
    location.reload();
}

function updateFilTable() {
    $("#interchangeTableContent").empty();

    jsonUrl = "/service/fil/";
    $.getJSON(jsonUrl, function(data) {
        $.each(data, function(i, item) {
            $('#interchangeTableContent').append($('<tr>')
                    .append($('<td>').text(item.id))
                    .append($('<td>').text(item.originalFilename!=null?item.originalFilename:""))
                    .append($('<td>').text(item.numberOfInstructions!=null?item.numberOfInstructions:""))
                    .append($('<td>').text(item.creationDateTime!=null?item.creationDateTime.$:"")))
        });
    });

    jsonUrl = null;
}


function updateCounters() {
    message("route=receive&attribute=ExchangesCompleted", "receivedCount");
    message("route=receipt&attribute=ExchangesCompleted", "receiptCount");
    message("route=balance&attribute=ExchangesCompleted", "balanceCount");
    message("route=receipt&attribute=ExchangesCompleted", "clearingCount");
    setTimeout("updateCounters()", 10000);
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


function updateCSMTable() {
    $("#paymentTableContent").empty();
    jsonUrl = "/service/transaction/";

    $.getJSON(jsonUrl, function(data) {
        $.each(data, function(i, item) {
            $('#paymentTableContent').append($('<tr>')
                    .append($('<td>').text(item.id))
                    .append($('<td>').text(item.debetAccount!=null?item.debetAccount:""))
                    .append($('<td>').text(item.creditAccount!=null?item.creditAccount:""))
                    .append($('<td>').text(item.amount!=null?item.amount:""))
                    .append($('<td>').text(item.currency!=null?item.currency:"")))
        });
    });
    jsonUrl = null;
}


function fileUpload() {
    $('#info_span').text("Uploading file").show().fadeOut(5000);
    setTimeout('refreshPage();', 2000);
}



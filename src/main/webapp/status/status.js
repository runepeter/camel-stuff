
$(document).ready(function() {
    $("#tabs").tabs();
    updateFilTable();
    updateCounters();
    //updateCSMTable();
    setTimeout('refreshPage();', 300000);
});

function refreshPage() {
    location.reload();
}

function updateFilTable() {

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





function updateCounters() {
    message("route=receipt&attribute=ExchangesCompleted", "receiptCount");
    message("route=balance&attribute=ExchangesCompleted", "balanceCount");
    message("route=receipt&attribute=ExchangesCompleted", "clearingCount");
    setTimeout("updateCounters()", 10000);
}




function updateCSMTable() {
    $('#instructionsdiv').show();

    jsonUrl = "/service/transactions/";

    $.getJSON(jsonUrl, function(data) {
        $.each(data.list, function(i, item) {
            $('#paymentTableContent').append($('<tr>')
                    .click(function() {
                listTransactionItems(item, this)
            })
                    .append($('<td>').text(item.id))
                    .append($('<td>').text(item.paymentId!=null?item.paymentId:""))
                    .append($('<td>').text(item.paymentPosition!=null?item.paymentPosition:""))
                    .append($('<td>').text(item.paymentType!=null?item.paymentType:""))
                    .append($('<td>').text(item.initiatorService!=null?item.initiatorService:""))
                    .append($('<td>').text(item.paymentDate!=null?item.paymentDate.$:""))
                    .append($('<td>').text(item.debetAccount!=null?item.debetAccount.accountNumber:""))
                    .append($('<td>').text(item.creditAccount!=null?item.creditAccount.accountNumber:""))
                    .append($('<td>').text(item.amount!=null?item.amount:""))
                    .append($('<td>').text(item.numberOfTransactions!=null?item.numberOfTransactions:""))
                    .append($('<td>').text(item.currency!=null?item.currency:""))
                    .append($('<td>').text(item.paymentFlow!=null?item.paymentFlow:""))
                    .append($('<td>').text(item.clearing!=null?item.clearing:""))
                    .append($('<td>').text(" Se > ")
                    .click(
                    function() {
                        window.open("/service/payment/"+item.id+"/raw");
                    }))
                     .append($('<td>').text(" Se > ")
                    .click(
                    function() {
                        window.open("/service/payment/"+item.id+"/xml");
                    })))
        });
    });
    jsonUrl = null;
}


function fileUpload() {
    $('#info_span').text("Uploading file").show().fadeOut(5000);
    setTimeout('refreshPage();', 2000);
}



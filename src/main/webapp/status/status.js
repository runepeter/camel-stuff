
$(document).ready(function() {
    $("#tabs").tabs();
    updateInterchangesTable();
    updateAll();
    setTimeout('refreshPage();', 300000);
});

function refreshPage() {
    location.reload();
}

function updateInterchangesTable() {
   /* $('#instructionsdiv').hide();
    $('#paymentTableContent').empty();
    $('#transactionsTableContent').empty();
    $('#transactionsdiv').hide();
    $('#statushistoryTableContent').empty();
    $('#statushistorydiv').hide();
    $('#paymentTableContent').empty();
    $('#interchangeTableContent').empty();

    jsonUrl = "/service/interchange/";
    $.getJSON(jsonUrl, function(data) {
        $.each(data, function(i, item) {
            $('#interchangeTableContent').append($('<tr>')
                    .click(function() {
                listPaymentItems(item, this)
            })
                    .append($('<td>').text(item.id))
                    .append($('<td>').text(item.originalFilename!=null?item.originalFilename:""))
                    .append($('<td>').text(item.numberOfInstructions!=null?item.numberOfInstructions:""))
                    .append($('<td>').text(item.creationDateTime!=null?item.creationDateTime.$:"")))
        });
    });
    */
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





function updateAll() {
    message("route=receipt&attribute=ExchangesCompleted", "receiptCount");
    message("route=balance&attribute=ExchangesCompleted", "balanceCount");
    message("route=receipt&attribute=ExchangesCompleted", "clearingCount");
    setTimeout("updateAll()", 10000);
}

function listPaymentItems(item, listWebElement) {
    $('#paymentTableContent').empty();
    $('#transactionsTableContent').empty();
    $('#statushistoryTableContent').empty();
    $('#instructionsdiv').hide();
    $('#transactionsdiv').hide();
    $('#statushistorydiv').hide();

    if ($(listWebElement).hasClass("highlight")) {
        $(listWebElement).removeClass("highlight");
    } else {
        $(listWebElement).siblings().removeClass("highlight");
        $(listWebElement).addClass("highlight");
        updatePaymentsTable(item);
    }
}


function updatePaymentsTable(item) {
    $('#instructionsdiv').show();
    $('#interchangename').text('Instructions in interchange with id: '+item.id);

    if (item == null) return;
    dato = $('#datepicker').val();
    jsonUrl = "/service/interchange/";
    /*
    if (dato != null) {
        jsonUrl += "?dato="+dato;
    }
    */
    if (item != null) {
        jsonUrl += item.id + "/instructions";
    }
    $.getJSON(jsonUrl, function(data) {
        $.each(data.list, function(i, item) {
            $('#paymentTableContent').append($('<tr>')
                    .click(function() {
                listTransactionItems(item, this)
            })
                    .append($('<td>').text(item.id))
                    .append($('<td>').text(item.paymentId!=null?item.paymentId:""))
                    .append($('<td>').text(item.paymentPosition!=null?item.paymentPosition:"")/*.click(function(){
             window.open("/service/payment/"+item.id+"/statushistory/", "Status history",
             "status=no, height = 300, width = 300, resizable=no, menubar=no, location=no, toolbar=no, scrollbars=no");
             return false;
             })*/)

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

    dato = null;
    jsonUrl = null;
}


function listTransactionItems(item, listWebElement) {
    listTransactions(item.id);
    listStatushistory(item.id);

    if ($(listWebElement).hasClass("highlight")) {
        $(listWebElement).removeClass("highlight");
        $('#transactionsTableContent').empty();
        $('#transactionsdiv').hide();
        $('#statushistoryTableContent').empty();
        $('#statushistorydiv').hide();
    } else {
        $(listWebElement).siblings().removeClass("highlight");
        $(listWebElement).addClass("highlight");
    }
}

function listTransactions(id) {
    $('#transactionsdiv').show();
    $('#instructionname').text('Transactions in instruction with id: '+id);
    $('#transactionsTableContent').empty();
    dato = $('#datepicker').val();
    jsonUrl = "/service/payment/"+id+"/transactions/";
    if (dato != null) {
        jsonUrl += "?dato="+dato;
    }
    $.getJSON(jsonUrl, function(data) {
        $.each(data.list, function(i, item) {
            $('#transactionsTableContent').append($('<tr>')
                    .append($('<td>').text(item.id))
                    .append($('<td>').text(item.paymentId!=null?item.paymentId:""))
                    .append($('<td>').text(item.debetAccount!=null?item.debetAccount.accountNumber:""))
                    .append($('<td>').text(item.creditAccount!=null?item.creditAccount.accountNumber:""))
                    .append($('<td>').text(item.amount!=null?item.amount.substring(0,item.amount.length-2)+"."+item.amount.substring(item.amount.length-2, item.amount.length):""))
                    .append($('<td>').text(item.currency!=null?item.currency:""))
                    .append($('<td>').text(" Se > ")
                    .click(
                    function() {
                        window.open("/service/payment/"+id+"/transactions/"+item.id+"/raw");
                    }))
                    .append($('<td>').text(" Se > ")
                    .click(
                    function() {
                        window.open("/service/payment/"+id+"/transactions/"+item.id+"/xml");
                    })))
        });
    });

    dato = null;
    jsonUrl = null;
}

function listStatushistory(id) {
    $('#statushistorydiv').show();
    $('#instruction2name').text('StatusHistory for instruction with id: '+id);
    $('#statushistoryTableContent').empty();
    dato = $('#datepicker').val();
    jsonUrl = "/service/payment/"+id+"/statushistory/";
    if (dato != null) {
        jsonUrl += "?dato="+dato;
    }
    $.getJSON(jsonUrl, function(data) {
        $.each(data.list, function(i, item) {
            $('#statushistoryTableContent').append($('<tr>')
                    .append($('<td>').text(item.id))
                    .append($('<td>').text(item.entityId))
                    .append($('<td>').text(item.entityType))
                    .append($('<td>').text(item.paymentPosition))
                    .append($('<td>').text(item.timeStamp.$)));
        });
    });

    dato = null;
    jsonUrl = null;
}



function fileUpload() {
    $('#info_span').text("Uploading file").show().fadeOut(5000);
    setTimeout('refreshPage();', 2000);
}
function runR2Kvittering() {
    $.ajax({
        url: "/service/r2kvittering/",
        type: "POST",
        success: function(html) {
            $('#info_span').text("Telepay R2 startet").show().fadeOut(5000);
        }
    });
    setTimeout('refreshPage();', 2000);
}

function runDKO() {
    $.ajax({
        url: "/service/dko/",
        type: "POST",
        success: function(html){
            $('#info_span').text("DKO startet").show().fadeOut(5000);
        }
    });
    setTimeout('refreshPage();', 2000);
}

function runNibe() {
    $.ajax({
        url: "/service/nibe/",
        type: "POST",
        success: function(html){
            $('#info_span').text("Nibesuttrekk startet").show().fadeOut(5000);
        }
    });
    setTimeout('refreshPage();', 2000);
}

function runtelepayOnUs() {
    $.ajax({
        url: "/service/telepayonus/",
        type: "POST",
        success: function(html){
            $('#info_span').text("Telepay On Us Flow startet").show().fadeOut(5000);
        }
    });
    setTimeout('refreshPage();', 2000);
}

function uploadfile() {
    var file = document.getElementById("fileToUpload").files[0];
    var contents = file.getAsText("");
    $.post('/service/upload/',
            function(data) {
                alert("Data Loaded: " + data)
            }
            );

//    $.post("test.php", { name: "John", time: "2pm" },
//    		   function(data) {
//    		     alert("Data Loaded: " + data);
//    		   });



//    $.ajax({
//        url: "/service/upload/",
//        type: "POST",
//        data: contents,
//        success: function(html){
//            $('#info_span').text("File Upload Started").show().fadeOut(5000);
//        }
//    });
    setTimeout('refreshPage();', 2000);
}

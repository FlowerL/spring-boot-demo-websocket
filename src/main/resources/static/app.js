var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect(url) {
    // 如果开了nginx代理走https和wss，那么url前面要加上https://
    url = url || '127.0.0.1:8080/websocket';
    var socket = new SockJS(url);
    stompClient = Stomp.over(socket);
    var headers = {
        login: 'mylogin',
        passcode: 'mypasscode',
        // additional header
        'client-id': 'my-client-id'
    }
    stompClient.connect(headers, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        // 订阅用户独有的topic通道
        stompClient.subscribe('/user/topic/greetings', function (greeting) {
            var content = JSON.parse(greeting.body).content;
            if (content == null) {
                content = JSON.parse(greeting.body).error;
            }
            showGreeting(content);
        }, function (error) {
            showGreeting(error);
        });
        // 订阅用户广播的topic通道
        stompClient.subscribe('/topic/greetings', function (greeting) {
            var content = JSON.parse(greeting.body).content;
            if (content == null) {
                content = JSON.parse(greeting.body).error;
            }
            showGreeting(content);
        }, function (error) {
            showGreeting(error);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName(url) {
    stompClient.send(url, {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect($("#connect_url").val()); });
    $( "#disconnect" ).click(function() { disconnect(); });

    $( "#annotation_broadcast" ).click(function() { sendName('/app/annotation/broadcast'); });
    $( "#annotation_user" ).click(function() { sendName('/app/annotation/user'); });
    $( "#annotation_session" ).click(function() { sendName('/app/annotation/session'); });

    $( "#template_broadcast" ).click(function() { sendName('/app/template/broadcast'); });
    $( "#template_user" ).click(function() { sendName('/app/template/user'); });
    $( "#template_session" ).click(function() { sendName('/app/template/session'); });

    $( "#exception_broadcast" ).click(function() { sendName('/app/exception/broadcast'); });
    $( "#exception_user" ).click(function() { sendName('/app/exception/user'); });
    $( "#exception_session" ).click(function() { sendName('/app/exception/session'); });
});
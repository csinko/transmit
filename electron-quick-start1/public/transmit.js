var TextReceiver = (function() {
    Quiet.setProfilesPrefix("");
    Quiet.setMemoryInitializerPrefix("");
    Quiet.setLibfecPrefix("");
    var target;
    var content;
    var warningbox;


    function connectToPhone(address) {
        data = address.split(':');
        msg = {
            ip: data[0],
            port: data[1]
        }
        console.log(data);
        $.post("http://localhost:3000/connect/phone", msg, function(data, status) {
            console.log(data);
            console.log(status);
        });

        }
    

    function routeMessage(message) {
        if(message.match(/:/g)) {
            alert("Phone Found! " + message);
            connectToPhone(message);
        }
        else {
            alert("Found not phone!");
        }
    }

    function onReceive(recvPayload) {
        content = new ArrayBuffer(0);
        content = Quiet.mergeab(content, recvPayload);
        messagereceived = Quiet.ab2str(content);
        routeMessage(messagereceived);

    };

    function onReceiverCreateFail(reason) {
        console.log("failed to create quiet receiver: " + reason);
        warningbox.classList.remove("hidden");
        warningbox.textContent = "Sorry, it looks like this example is not supported by your browser. Please give permission to use the microphone or try again in Google Chrome or Microsoft Edge."
    };

    function onReceiveFail(num_fails) {
        warningbox.classList.remove("hidden");
        warningbox.textContent = "We didn't quite get that. It looks like you tried to transmit something. You may need to move the transmitter closer to the receiver and set the volume to 50%."
    };

    function onQuietReady() {
        var profilename = document.querySelector('[data-quiet-profile-name]').getAttribute('data-quiet-profile-name');
        Quiet.receiver(profilename, onReceive, onReceiverCreateFail, onReceiveFail);
    };

    function onQuietFail(reason) {
        console.log("quiet failed to initialize: " + reason);
        warningbox.classList.remove("hidden");
        warningbox.textContent = "Sorry, it looks like there was a problem with this example (" + reason + ")";
    };

    function onDOMLoad() {
        target = document.querySelector('[data-quiet-receive-text-target]');
        warningbox = document.querySelector('[data-quiet-warning]');
        Quiet.addReadyCallback(onQuietReady, onQuietFail);
    };

    document.addEventListener("DOMContentLoaded", onDOMLoad);
})();
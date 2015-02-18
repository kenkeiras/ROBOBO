(function($){
    var IMAGE_REFRESH_TIME = 2000; // Miliseconds
    var cameraFrame = undefined;
    var imageSet = false;
    var STATS_REFRESH_TIME = 2000; // Miliseconds

    function updateBackground(){
        var i = new Image();
        i.onload = (function(){
            if ((!imageSet) && (this.naturalWidth > 0)){
                cameraFrame.style.width = this.naturalWidth + "px";
                cameraFrame.style.height = this.naturalHeight + "px";
                imageSet = true;
            }
            cameraFrame.style.backgroundImage = "url(" + this.src + ")";
        });

        // Add a miliseconds parameter to avoid caching
        i.src = "sensors/compressedImage?nocache=" + new Date().getTime();
        setTimeout(updateBackground, IMAGE_REFRESH_TIME);
    };

    function updateStats(){
        // Board
        // Wheels
        $.get("actuators/wheels", function(data){
            $('#wheels>.value').html("left " + data.leftWheel
                              + ", right: " + data.rightWheel);
        }, "json");

        // IR Sensors
        $.get("sensors/ir", function(data){
            $('#irsensors>.value').html(data.irSensor0 + ", "
                                         + data.irSensor1 + ", "
                                         + data.irSensor2 + ", "
                                         + data.irSensor3 + ", "
                                         + data.irSensor4 + ", "
                                         + data.irSensor5 + ", "
                                         + data.irSensor6 + ", "
                                         + data.irSensor7 + ", "
                                         + data.irSensor8);
        }, "json");

        // Android sensors
        $.get("sensors/accelerometer", function(data){
            $('#accelerometer>.value').html(
                                       data.accelerationX + ", "
                                     + data.accelerationY + ", "
                                     + data.accelerationZ);
        }, "json");

        $.get("sensors/battery", function(data){
            $('#battery>.value').html(data.batteryLevel * 100 + "%");
        }, "json");

        $.get("sensors/gravity", function(data){
            $('#gravity>.value').html(data.gravityX + ", "
                                       + data.gravityY + ", "
                                       + data.gravityZ);
        }, "json");

        $.get("sensors/gyroscope", function(data){
            $('#gyroscope>.value').html(data.gyroscopeX + ", "
                                         + data.gyroscopeY + ", "
                                         + data.gyroscopeZ);
        }, "json");

        $.get("sensors/light", function(data){
            $('#light>.value').html(data.light);
        }, "json");

        $.get("sensors/odometry", function(data){
            $('#odometry>.value').html(data.odometryX + ", "
                                       + data.odometryY);
        }, "json");

        $.get("sensors/magneticField", function(data){
            $('#magneticField>.value').html(data.magneticFieldX + ", "
                                             + data.magneticFieldY + ", "
                                             + data.magneticFieldZ);
        }, "json");

        $.get("sensors/pressure", function(data){
            $('#pressure>.value').html(data.pressure);
        }, "json");

        $.get("sensors/proximity", function(data){
            $('#proximity>.value').html(data.proximity);
        }, "json");

        $.get("sensors/temperature", function(data){
            $('#temperature>.value').html(data.temperature);
        }, "json");

        setTimeout(updateStats, STATS_REFRESH_TIME);
    }


    function sendNewSpeed(){
        var value = "leftWheel=" + $('#leftWheel').val() + "&"
                + "rightWheel=" + $('#rightWheel').val();
        $.post("actuators/wheels", value.replace(",", "."));
    }



    function onload(){
        cameraFrame = document.getElementById("cameraFrame");
        updateBackground();
        updateStats();
        $("#newSpeedForm").submit(sendNewSpeed);
    };
    window.onload = onload;
})(jQuery);

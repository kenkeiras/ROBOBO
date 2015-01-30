(function($){
    var IMAGE_REFRESH_TIME = 500;
    var cameraFrame = undefined;
    var imageSet = false;
    var STATS_REFRESH_TIME = 1000;

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
            $('#wheels').html("Wheels: left " + data.leftWheel
                              + ", right: " + data.rightWheel);
        }, "json");

        // IR Sensors
        $.get("sensors/ir", function(data){
            $('#irsensors').html("IrSensors: "
                                 + data.irSensor0 + ", "
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
            $('#accelerometer').html("Accelerometer: "
                                     + data.accelerationX + ", "
                                     + data.accelerationY + ", "
                                     + data.accelerationZ);
        }, "json");

        $.get("sensors/battery", function(data){
            $('#battery').html("Battery: " + data.batteryLevel * 100 + "%");
        }, "json");

        $.get("sensors/gravity", function(data){
            $('#gravity').html("Gravity: "
                               + data.gravityX + ", "
                               + data.gravityY + ", "
                               + data.gravityZ);
        }, "json");

        $.get("sensors/gyroscope", function(data){
            $('#gyroscope').html("Gyroscope: "
                                     + data.gyroscopeX + ", "
                                     + data.gyroscopeY + ", "
                                     + data.gyroscopeZ);
        }, "json");

        $.get("sensors/light", function(data){
            $('#light').html("Light: " + data.light);
        }, "json");

        $.get("sensors/magneticField", function(data){
            $('#magneticField').html("Magnetic Field: "
                                     + data.magneticFieldX + ", "
                                     + data.magneticFieldY + ", "
                                     + data.magneticFieldZ);
        }, "json");

        $.get("sensors/pressure", function(data){
            $('#pressure').html("Pressure: " + data.pressure);
        }, "json");

        $.get("sensors/proximity", function(data){
            $('#proximity').html("Proximity: " + data.proximity);
        }, "json");

        $.get("sensors/temperature", function(data){
            $('#temperature').html("Temperature: " + data.temperature);
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

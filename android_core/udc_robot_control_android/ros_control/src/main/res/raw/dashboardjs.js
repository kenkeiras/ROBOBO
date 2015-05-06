(function($){
    var IMAGE_REFRESH_TIME = 1000;
    var STATS_REFRESH_TIME = 1000;
    var SEND_SPEED_BATCH_TIMEOUT = 2000;
    var updatingSpeed = false;
    var updatingSpeedTimeout = undefined;

    function updateBackground(){
        var i = document.getElementById('cameraFrame');
        // Add a miliseconds parameter to avoid caching
        i.src = "sensors/compressedImage?nocache=" + new Date().getTime();
        setTimeout(updateBackground, IMAGE_REFRESH_TIME);
    };

    function updateStats(){
        // Board
        // Wheels
        if (!updatingSpeed){
            $.get("actuators/wheels", function(data){
                $('#leftEngine input')[0].value = data.leftWheel;
                $('#rightEngine input')[0].value = data.rightWheel;
            }, "json");
        }

        var irNums = [1, 2, 3, 4, 5, 6, 7, 8, 9];

        // IR Sensors
        $.get("sensors/ir", function(data){
            for (var i in irNums){
                document.getElementById('ir' + irNums[i]).textContent = data['irSensor' + irNums[i]];
            }
        }, "json");

        // Not actually a board one, but based on the engine
        $.get("sensors/odometry", function(data){
            $('#odometry>.value').html(data.odometryX + ", "
                                       + data.odometryY);
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
        var leftWheel = $('#leftEngine input')[0].value;
        var rightWheel = $('#rightEngine input')[0].value;
        var value = "leftWheel=" + leftWheel + "&"+ "rightWheel=" + rightWheel;
        $.post("actuators/wheels", value);
        updatingSpeed = false;
        updatingSpeedTimeout = undefined;
    }


    function onChangeSpeed(wheel, e){
        // Restart timer if it was going on
        if (updatingSpeed){
            clearTimeout(updatingSpeedTimeout);
        }

        // Give the user some time to make changes before the updates are made
        updatingSpeed = true;
        updatingSpeedTimeout = setTimeout(sendNewSpeed, SEND_SPEED_BATCH_TIMEOUT);
    }


    function onload(){
        updateBackground();
        updateStats();
        $("#rightEngine input").change(function (e) {onChangeSpeed("right", e); });
        $("#leftEngine input").change(function (e) {onChangeSpeed("left", e); });
        $("#newSpeedForm").submit(sendNewSpeed);
    };
    window.onload = onload;
})(jQuery);

UDC Robot Control
=================

The main objective of this project is to implement a controller for a research robot developed by the UDC GSA Lab.

This project uses an Android Phone to connect vía USB cable to the robot. The phone will apport to the robot his own hardware (WIFI, GPS, gyroscope...)

This project is in part derivative work from the Android Sensors Driver (see LICENSE.txt).

This projects uses a modified version of the org.ros.android.view.camera package, from the android_gingerbread_mr1, releases under the Apache 2.0 license by Google Inc (see LICENSE.txt)


Plugins
-------

Plugins are [rosjava](http://wiki.ros.org/rosjava) applications that may be
dinamically loaded, to be used they must be converted into dex's and placed on
**/sdcard/ros**, you may convert a `.jar` into a `.dex` like this

    $ANDROID_HOME/build-tools/19.1.0/dx --dex --output=plugin.dex plugin.jar


April Tag detection using the NDK
---------------------------------

April tag detection consumes a big amount of memory, and it increases as the 
pixel count (so pretty fast), a way to palliate this is to use the C 
implementation, this is not default (yet) because it's not well integrated
in the building process, less data is passed (just tag id and hamming distance)
and seems to yield more false positives.

To use the C version you have to download the [Android NDK](https://developer.android.com/tools/sdk/ndk/index.html), 
from a shell `ANDROID_NDK_PATH` to the uncompressed directory and call 
`build-ndk.sh` in the `android_core/udc_robot_control_android/ros_control`,
this will build the needed libraries, to use them set 
`es.udc.fic.android.robot_control.camera.AprilTagPublisher` 
(again from `ros_control`) field `USE_NDK` to `true`.

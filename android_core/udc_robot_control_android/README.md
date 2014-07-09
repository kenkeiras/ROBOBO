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

    $ANDROID_HOME/build-tools/<version>/dx --dex --output=plugin.dex plugin.jar

Regarding the version, any *should* work.

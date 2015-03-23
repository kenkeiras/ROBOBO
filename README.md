ROBOBO
======


Hay cuatro artefactos diferentes en este proyecto:

* **android_core/udc_robot_control_android** Aplicación Android para controlar el robot. Debe compilarse en el entorno proporcionado por [android_core](http://wiki.ros.org/android_core "Wiki Ros.org")
* **rosjava/src/ROBOBO/udc_robot_control_java** Librería java nexo de ROS con las aplicaciones que comunican con el robot. Debe compilarse en el entorno proporcionado por [Ros Java Core](http://wiki.ros.org/rosjava_core "Wiki Ros.org")
* **rosjava/src/ROBOBO/panel_control** Interfaz gráfico simple creado con Idea IntelliJ para demostrar el uso de **udc_robot_control_java**. Proporciona un panel de control simple para el robot.
* **prototipo** Aplicación Android para controlar el robot descargando instrucciones vía HTTP desde un servidor web.


Dependencias
------------

* [ros-indigo](http://wiki.ros.org/ROS/Installation), es suficiente con instalar los repositorios.
* [rosjava-indigo](http://wiki.ros.org/rosjava/Tutorials/indigo/Installation)
* [Android SDK](https://developer.android.com/sdk/index.html) para los apps de
Android, habiendo apuntado $ANDROID_HOME a la raíz del SDK, desde este se ha
de instalar los siguientes paquetes:
 + Android SDK Build-tools v21.1.0
 + Android 4.3 (API 18) - SDK Platform
 + Android Support Repository v11
 + Android Support Library


Installación
------------

Lance el script de instalación

    bash install.sh

Si todo va bien, se mostrarán instrucciones para instalar los apps de Android

    INFO: To install android core in a device go to android_core/udc_robot_control_android/
    INFO: and run bash ./gradlew installDebug
    INFO: or, to install prototype go to prototipo/
    INFO: and run bash ./gradlew installUsbHostDebug

ROBOBO
======


Hay cuatro artefactos diferentes en este proyecto:

* *android_core/udc_robot_control_android* Aplicación Android para controlar el robot. Debe compilarse en el entorno proporcionado por [android_core](http://wiki.ros.org/android_core "Wiki Ros.org")
* *rosjava/udc_robot_control_java* Librería java nexo de ROS con las aplicaciones que comunican con el robot. Debe compilarse en el entorno proporcionado por [Ros Java Core](http://wiki.ros.org/rosjava_core "Wiki Ros.org")
* *panel_control* Interfaz gráfico simple creado con Idea IntelliJ para demostrar el uso de *udc_robot_control_java*. Proporciona un panel de control simple para el robot.
* *prototipo* Aplicación Android para controlar el robot descargando instrucciones vía HTTP desde un servidor web.


Installation
============

Run the installation script.

    bash install.sh

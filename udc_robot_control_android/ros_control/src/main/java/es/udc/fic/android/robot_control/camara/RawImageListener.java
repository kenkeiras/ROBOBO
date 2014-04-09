package es.udc.fic.android.robot_control.camara;

import android.hardware.Camera.Size;

interface RawImageListener {

  void onNewRawImage(byte[] data, Size size);

}
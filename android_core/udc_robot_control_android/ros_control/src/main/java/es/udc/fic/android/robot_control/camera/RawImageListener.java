package es.udc.fic.android.robot_control.camera;

import android.hardware.Camera.Size;

interface RawImageListener {

  void onNewRawImage(byte[] data, Size size);

}

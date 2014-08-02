package es.udc.fic.android.robot_control.camera;

public class AprilTagNdkInterface {

  static {
    System.loadLibrary("apriltag");
  }

  /**
   * Performs April tag detection over an image.
   *
   * @NOTE: JNI makes it "difficult" pass non-base classes such as TagDetection.
   */
  public native String[] process(byte[] image, int width, int height);
}

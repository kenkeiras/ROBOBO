package es.udc.fic.android.robot_control.camera;

import android.graphics.YuvImage;
import android.hardware.Camera.Size;
import android.util.Log;

import april.image.FloatImage;
import april.tag.*;

import com.google.common.base.Preconditions;

import java.util.List;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;


class AprilTagPublisher implements RawImageListener {

    private final ConnectedNode connectedNode;
    // private byte[] rawImageBuffer;
    // private Size rawImageSize;
    // private final Publisher<sensor_msgs.CompressedImage> imagePublisher;
    // private final Publisher<sensor_msgs.CameraInfo> cameraInfoPublisher;

    // private byte[] rawImageBuffer;
    // private Size rawImageSize;
    // private YuvImage yuvImage;
    // private Rect rect;
    // private ChannelBufferOutputStream stream;



    public AprilTagPublisher(String robotName, ConnectedNode connectedNode) {
        this.connectedNode = connectedNode;
        // String aprilQueue = robotName + "/" + IMAGE_QUEUE_NAME + "/compressed";
        // String infoQueue = robotName + "/" + CAMERA_INFO_QUEUE_NAME;
        // imagePublisher = connectedNode.newPublisher(imageQueue, sensor_msgs.CompressedImage._TYPE);
        // cameraInfoPublisher = connectedNode.newPublisher(infoQueue, sensor_msgs.CameraInfo._TYPE);
        // stream = new ChannelBufferOutputStream(MessageBuffers.dynamicBuffer());
    }


    private float[] yuvToFloats(byte[] yuv){
        int origLen = yuv.length;
        float[] fimg = new float[origLen];

        // Check convertion quality
        for (int i = 0; i < origLen; i++) {
            int grey = yuv[i] & 0xff;
            fimg[i] = ((float)grey) / 255.0f;
        }
        return fimg;
    }


    @Override
    public void onNewRawImage(byte[] data, Size size) {

        Log.d("UDCApril", "More data");
        // if (data == rawImageBuffer && size.equals(rawImageSize)) {
        //     Log.d("UDCApril", "Data already checked");
        //     return;
        // }

        // Log.d("UDCApril", "New data!");
        /// @TODO don't destroy buffers after every call to improve performance
        /// @TODO make this configurable
        TagFamily tf = new Tag36h11();
        TagDetector td = new TagDetector(tf);
        int width = size.width;
        int height = size.height;

        FloatImage img = new FloatImage(width, height,
                                        yuvToFloats(data));

        List<TagDetection> detections = td.processFloat(img, new double[]{width / 2,
                                                                          height / 2});

        Log.d("UDCApril", detections.size() + " detections!");
        for (TagDetection detection : detections){
            Log.d("UDCApril", "Detected: " + detection);
        }
    }
}

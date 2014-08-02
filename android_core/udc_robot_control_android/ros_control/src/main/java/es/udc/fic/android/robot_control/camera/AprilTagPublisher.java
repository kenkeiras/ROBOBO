package es.udc.fic.android.robot_control.camera;


import android.graphics.YuvImage;
import android.hardware.Camera.Size;
import android.util.Log;

import april.image.FloatImage;
import april.tag.*;

import com.google.common.base.Preconditions;

import es.udc.robotcontrol.utils.Constants;
import java.util.List;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;


class AprilTagPublisher implements RawImageListener {

    private final ConnectedNode connectedNode;
    private final Publisher<udc_robot_control_msgs.AprilTag> aprilPublisher;



    public AprilTagPublisher(String robotName, ConnectedNode connectedNode) {
        this.connectedNode = connectedNode;
        String aprilQueue = robotName + "/" + Constants.TOPIC_APRIL_TAGS;
        aprilPublisher = connectedNode.newPublisher(aprilQueue, udc_robot_control_msgs.AprilTag._TYPE);
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

            udc_robot_control_msgs.AprilTag msg = aprilPublisher.newMessage();
            msg.setCode((int) detection.code);
            msg.setId(detection.id);
            msg.setHammingDistance(detection.hammingDistance);
            msg.setRotation(detection.rotation * 90);
            msg.setObservedPerimeter(detection.observedPerimeter);

            aprilPublisher.publish(msg);
        }
    }
}

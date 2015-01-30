package es.udc.fic.android.robot_control.camera;


import android.hardware.Camera.Size;
import android.util.Log;

import april.image.FloatImage;
import april.tag.*;

import es.udc.robotcontrol.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;


class AprilTagPublisher implements RawImageListener {

    private boolean working = false;
    public static final boolean USE_NDK = false;
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


    private byte[] yuvToBytes(byte[] yuv){
        int origLen = yuv.length;
        byte[] img = new byte[origLen];

        // Check convertion quality
        for (int i = 0; i < origLen; i++) {
            byte grey = (byte)(yuv[i] & 0xff);
            img[i] = grey;
        }
        return img;
    }


    private TagDetection stringToDetection (String str){
        TagDetection detection = new TagDetection();
        String []parts = str.split(", ");
        assert(parts.length == 5);

        detection.code              = Integer.parseInt(parts[0], 16);
        detection.id                = Integer.parseInt(parts[1], 16);
        detection.hammingDistance   = Integer.parseInt(parts[2], 16);
        detection.rotation          = Integer.parseInt(parts[3], 16);
        detection.observedPerimeter = Integer.parseInt(parts[4], 16);

        // Filler
        detection.obsCode = -1;
        detection.good = true; // Probably...
        detection.cxy = new double[]{(double) -1, (double) -1};

        return detection;
    }

    private List<TagDetection> stringArrayToDetections(String[] results){
        List<TagDetection> detections = new ArrayList<TagDetection>();
        for (String result : results){
            detections.add(stringToDetection(result));
        }

        return detections;
    }


    @Override
    public void onNewRawImage(byte[] data, Size size) {

        if (working) return;
        working = true;

        List<TagDetection> detections;
        int width = size.width;
        int height = size.height;

        if (USE_NDK){
            AprilTagNdkInterface april = new AprilTagNdkInterface();
            String[] results = april.process(yuvToBytes(data), width, height);

            detections = stringArrayToDetections(results);
        }
        else {
            TagFamily tf = new Tag36h11();
            TagDetector td = new TagDetector(tf);

            FloatImage img = new FloatImage(width, height,
                                            yuvToFloats(data));
            detections = td.processFloat(img, new double[]{width / 2,
                                                           height / 2});
            tf = null;
            td = null;
            img = null;
        }
        Log.d("UDCApril", detections.size() + " detections");
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

        working = false;
    }
}

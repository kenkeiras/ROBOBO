package es.udc.fic.android.robot_control.camera;


import android.hardware.Camera.Size;
import android.util.Log;

import april.image.FloatImage;
import april.tag.*;

import boofcv.alg.geo.pose.P3PGrunert;
import boofcv.alg.geo.pose.PointDistance3;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;
import georegression.struct.point.Point2D_F64;
import udc_robot_control_msgs.AprilTag;

import java.util.ArrayList;
import java.util.List;

import org.ddogleg.solver.PolynomialOps;
import org.ddogleg.solver.RootFinderType;
import org.ddogleg.struct.FastQueue;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;


class AprilTagPublisher implements RawImageListener {

    private boolean working = false;
    private final ConnectedNode connectedNode;
    private final Publisher<udc_robot_control_msgs.AprilTag> aprilPublisher;
    private final double TAG_SIZE = 0.04f; // Expected april tag size in meters


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


    private AprilTag buildMsg(TagDetection detection){
        Log.d(C.APRIL_TAG, "Detected: " + detection);

        P3PGrunert grunert = new P3PGrunert(PolynomialOps.createRootFinder(5, RootFinderType.STURM));
        Point2D_F64 obs1 = new Point2D_F64(detection.p[0][0], detection.p[0][1]);
        Point2D_F64 obs2 = new Point2D_F64(detection.p[1][0], detection.p[1][1]);
        Point2D_F64 obs3 = new Point2D_F64(detection.p[2][0], detection.p[2][1]);

        /* Tags are square, so the distances are either that of the square side or the one from one
           vertex to the oposite, this last distance is the same as the hipotenuse of a triangle
           with the same base and height as the square side. */
        double length23 = TAG_SIZE;
        double length13 = Math.sqrt(TAG_SIZE * TAG_SIZE + TAG_SIZE * TAG_SIZE);
        double length12 = TAG_SIZE;

        boolean solved = grunert.process(obs1, obs2, obs3, length23, length13, length12);
        PointDistance3 distance = null;
        Log.d(C.APRIL_TAG, "Solved: " + solved);

        if (solved){
            FastQueue<PointDistance3> points = grunert.getSolutions();
            Log.d(C.APRIL_TAG, points.size() + " solutions!");
            for (PointDistance3 p : points.toList()){
                if (distance == null){
                    distance = p;
                }
                Log.d(C.APRIL_TAG, "X: " + p.dist1 + " | Y: " + p.dist2 + " | Z: " + p.dist3);
            }
        }

        AprilTag msg = aprilPublisher.newMessage();
        msg.setCode((int) detection.code);
        msg.setId(detection.id);
        msg.setHammingDistance(detection.hammingDistance);
        msg.setRotation(detection.rotation * 90);
        msg.setObservedPerimeter(detection.observedPerimeter);

        if (distance != null) {
            msg.setDistanceX(distance.dist1);
            msg.setDistanceY(distance.dist2);
            msg.setDistanceZ(distance.dist3);
        }

        return msg;
    }


    @Override
    public void onNewRawImage(byte[] data, Size size) {

        if (working) return;
        working = true;

        List<TagDetection> detections;
        int width = size.width;
        int height = size.height;

        TagFamily tf = new Tag36h11();
        TagDetector td = new TagDetector(tf);

        FloatImage img = new FloatImage(width, height,
                                        yuvToFloats(data));
        data = null;

        detections = td.processFloat(img, new double[]{width / 2,
                                                       height / 2});
        tf = null;
        td = null;
        img = null;

        Log.d(C.APRIL_TAG, detections.size() + " detections");
        for (TagDetection detection : detections){
            udc_robot_control_msgs.AprilTag msg = buildMsg(detection);

            aprilPublisher.publish(msg);
        }

        working = false;
    }
}

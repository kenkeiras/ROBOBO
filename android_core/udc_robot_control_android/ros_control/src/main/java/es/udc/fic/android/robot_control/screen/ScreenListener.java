package es.udc.fic.android.robot_control.screen;

import android.util.Log;

import es.udc.fic.android.robot_control.robot.RobotCommController;
import es.udc.fic.android.robot_control.utils.C;
import es.udc.robotcontrol.utils.Constants;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Subscriber;
import org.ros.message.MessageListener;

public class ScreenListener implements NodeMain {

    private RobotCommController robot;
    private String robotName;
    private NodeMainExecutor nodeMainExecutor;

    private ConnectedNode cn;
    private Subscriber<std_msgs.String> subscriber;
    private ScreenMessageListener ml;

    public ScreenListener(RobotCommController robot, String robotName, NodeMainExecutor nodeMainExecutor) {
        super();
        Log.d(C.TAG, "Creating Information Listener");
        this.robot = robot;
        this.robotName = robotName;
        this.nodeMainExecutor = nodeMainExecutor;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(C.DefaultBaseNodeName + "/" + Constants.TOPIC_SCREEN);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        cn = connectedNode;
        ml = new ScreenMessageListener(robot, nodeMainExecutor);

        String topicName = robotName + "/" + Constants.TOPIC_SCREEN;
        subscriber = connectedNode.newSubscriber(topicName, std_msgs.String._TYPE);
        subscriber.addMessageListener(ml);
    }

    @Override
    public void onShutdown(Node node) {
        if (subscriber != null) {
            subscriber.shutdown();
            subscriber = null;
        }
    }

    @Override
    public void onShutdownComplete(Node node) {
    }


    @Override
    public void onError(Node node, Throwable throwable) {
        Log.w(C.CMD_TAG, "Error on Screen Listener [ " + node.getName() + " ] [ " + throwable.getMessage() + " ]", throwable);
    }

    private class ScreenMessageListener implements MessageListener<std_msgs.String> {

        private RobotCommController robot;
        private NodeMainExecutor nodeMainExecutor;


        public ScreenMessageListener(RobotCommController robot, NodeMainExecutor nodeMainExecutor) {
            this.robot = robot;
            this.nodeMainExecutor = nodeMainExecutor;

        }

        @Override
        public void onNewMessage(std_msgs.String msg) {
            robot.setLastInfo(msg.getData());
        }
    }
}

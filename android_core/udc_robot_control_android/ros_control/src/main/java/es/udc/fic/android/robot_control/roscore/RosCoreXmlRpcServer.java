package es.udc.fic.android.robot_control.roscore;

import com.nmote.nanohttp.NanoServer;
import com.nmote.xr.HTTPServerEndpoint;

import es.udc.fic.android.robot_control.roscore.roslaunch.RosLaunch;

import java.io.IOException;

/**
 * Implements a full XMLRPC driven ROS Master node.
 *
 */
public class RosCoreXmlRpcServer {

    /// @TODO Surely ROS defines this already
    public static final int ERROR_STATUS = -1;  // Error on clients part
    public static final int FAILURE_STATUS = 0; // Error completing request
    public static final int SUCCESS_STATUS = 1; // Request went OK


    private int port;
    private NanoServer server;
    private RosLaunch rosLaunch;

    RosCoreXmlRpcServer (int port) throws IOException {
       server = new NanoServer(port);
       server.add(new HTTPServerEndpoint(new EndpointSelectorEndpoint()));
       RosParamServer.paramHierarchy.set(new String[]{"", "use_sim_time"},
                                         new Boolean(false));

       rosLaunch = new RosLaunch();
    }


    public void start() throws IOException {
        server.start();
        rosLaunch.start();
    }


    public static void main(String[] args) throws Exception {
        RosCoreXmlRpcServer server = new RosCoreXmlRpcServer(11311);
        server.start();
    }
}

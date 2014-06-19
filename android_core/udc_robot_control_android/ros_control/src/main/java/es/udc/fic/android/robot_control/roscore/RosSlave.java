package es.udc.fic.android.robot_control.roscore;

import com.nmote.xr.XRMethod;

import java.io.IOException;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements ROS <a href="http://wiki.ros.org/ROS/Slave_API">slave API</a>.
 *
 */
public class RosSlave {
    /**
     * Retrieve process PID.
     *
     * @TODO put in a utils file.
     *
     */
    private static final Integer getPid(){
        // /proc/self links to /proc/<pid>, so on standard unix environments
        // it can be extracted "portably" from there
        try {
            String pidStr = new File("/proc/self").getCanonicalFile().getName();
            return Integer.parseInt(pidStr);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }

        return null;
    }


    // API
    /**
     * Retrieve transport/topic statistics.
     *
     */
    @XRMethod(value = "getBusStats", help = "Retrieve transport/topic statistics")
    public static Object[] getBusStats(String callerId){
        List<Object> stats = new ArrayList<Object>();

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] = stats;

        return response;
    }


    /**
     * Retrieve transport/topic connection information.
     *
     */
    @XRMethod(value = "getBusInfo", help = "Retrieve transport/topic connection information")
    public static Object[] getBusInfo(String callerId){
        List<Object> stats = new ArrayList<Object>();

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] = stats;

        return response;
    }


    /**
     * Get the URI of the master node.
     *
     */
    @XRMethod(value = "getMasterUri", help = "Get the URI of the master node")
    public static Object[] getMasterUri(String callerId){
        return RosMaster.getUri(callerId);
    }


    /**
     * Stop this server.
     *
     * @TODO optional message
     *
     */
    @XRMethod(value = "shutdown", help = "Stop this server")
    public static Object[] getBusInfo(String callerId, String msg){
        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] =  0; // Ignore

        return response;
    }


    /**
     * Get the PID of this server.
     *
     */
    @XRMethod(value = "getPid", help = "Exposes master PID (for some reason)")
    public static Object[] getPid(String callerId){
        Object[] response = new Object[3];
        Integer pid = getPid();

        if (pid != null){
            response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
            response[1] = pid + "";
            response[2] = getPid();
        }
        else { // If PID couldn't be retrieved, fail
            response[0] = RosCoreXmlRpcServer.FAILURE_STATUS;
            response[1] = "";
            response[2] = -1;
        }

        return response;
    }


    /**
     * Retrieve a list of topics that this node subscribes to.
     *
     */
    @XRMethod(value = "getSubscriptions",
              help = "Retrieve a list of topics that this node subscribes to")
    public static Object[] getSubscriptions(String callerId){
        List<Object[]> subscriptions = new ArrayList<Object[]>();

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] = subscriptions;

        return response;
    }


    /**
     * Retrieve a list of topics that this node publishes.
     *
     */
    @XRMethod(value = "getPublications",
              help = "Retrieve a list of topics that this node publishes")
    public static Object[] getPublications(String callerId){
        List<Object[]> publications = new ArrayList<Object[]>();

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] = publications;

        return response;
    }


    /**
     * Callback from master with updated value of subscribed parameter.
     *
     */
    @XRMethod(value = "paramUpdate",
              help = "Callback from master with updated value of subscribed parameter")
    public static Object[] paramUpdate(String callerId, String parameter_key,
                                       Object parameter_value){

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] =  0; // Ignore

        return response;
    }


    /**
     * Callback from master of current publisher list for specified topic.
     *
     */
    @XRMethod(value = "publisherUpdate",
              help = "Callback from master of current publisher list for specified topic")
    public static Object[] publisherUpdate(String callerId, String topic,
                                           String[] publishers){

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] =  0; // Ignore

        return response;
    }


    /**
     * Publisher node API method called by a subscriber node.
     * This requests that source allocate a channel for communication.
     *
     * Subscriber provides a list of desired protocols for communication.
     * Publisher returns the selected protocol along with any additional params
     * required for establishing connection.
     * For example, for a TCP/IP-based connection, the source node may return
     * a port number of TCP/IP server.
     *
     */
    @XRMethod(value = "requestTopic",
              help = "Publisher node API method called by a subscriber node")
    public static Object[] requestTopic(String callerId, String topic,
                                        Object[] protocols){

        List<Object[]> protocolParams = new ArrayList<Object[]>();

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] = protocolParams; // Ignore

        return response;
    }
}

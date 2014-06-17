package es.udc.fic.android.robot_control.roscore;

import com.nmote.xr.XRMethod;

/**
 * Implements ROS <a href="http://wiki.ros.org/ROS/Parameter%20Server%20API">parameter server API</a>.
 *
 */
public class RosParamServer {

    public final static String HIERARCHYCAL_SEPARATOR = "/";

    static Hierarchy<String, Object> paramHierarchy =
        new Hierarchy<String, Object>();


    /**
     * Set parameter.
     *
     */
    @XRMethod(value = "setParam", help = "Set parameter")
    public static Object[] setParam(String callerId, String key, String value){
        /// @TODO decode JSON
        System.err.println("Set parameter: \033[1;92m" + key + "\033[0m: \033[1;94m" + value + "\033[0m");
        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";
        response[2] = "";  // Ignore

        paramHierarchy.set(key.split(HIERARCHYCAL_SEPARATOR), value);

        return response;
    }


    /**
     * Retrieve parameter value from server.
     *
     */
    @XRMethod(value = "getParam", help = "Retrieve parameter value from server")
    public static Object[] getParam(String callerId, String key){
        System.err.println("Get parameter: \033[1;92m" + key + "\033[0m");

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";
        try {
            Tuple<Hierarchy<String, Object>, Object> param =
                paramHierarchy.get(key.split(HIERARCHYCAL_SEPARATOR));

            // If the requested key is not a "leaf", return it as a json dict
            if (param.y == null){
                response[2] = param.x.asMap();
            }
            else {
                response[2] = param.y;
            }

            System.out.println(response[2].toString());
        }
        catch (Hierarchy.KeyNotFoundException e){
            response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
            response[1] = e.getMessage();
            response[2] = "";
        }
        catch (IllegalArgumentException e){
            response[0] = RosCoreXmlRpcServer.FAILURE_STATUS;
            response[1] = e.getMessage();
            response[2] = "";
        }

        return response;
    }


    /**
     * Check if parameter is stored on server.
     *
     */
    @XRMethod(value = "hasParam", help = "Check if parameter is stored on server")
    public static Object[] hasParam(String callerId, String key){
        System.err.println("Has parameter: \033[1;92m" + key + "\033[0m");

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";
        response[2] = paramHierarchy.has(key.split(HIERARCHYCAL_SEPARATOR));

        return response;
    }
}

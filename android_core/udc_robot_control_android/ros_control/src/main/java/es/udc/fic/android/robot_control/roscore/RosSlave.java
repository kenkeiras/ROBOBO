package es.udc.fic.android.robot_control.roscore;

import com.nmote.xr.XRMethod;

import java.io.IOException;
import java.io.File;

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


}

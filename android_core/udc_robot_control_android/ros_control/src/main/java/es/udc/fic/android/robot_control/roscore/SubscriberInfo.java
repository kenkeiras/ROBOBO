package es.udc.fic.android.robot_control.roscore;

/**
 * Represents ROS Master node internal information about subscibers.
 *
 */
public class SubscriberInfo {

    private String callerId;
    private String callerApi;

    SubscriberInfo(String callerId, String callerApi){
        this.callerId = callerId;
        this.callerApi = callerApi;
    }


    public String getCallerId(){
        return callerId;
    }


    public String getCallerApi(){
        return callerApi;
    }

    @Override
    public int hashCode(){
        return this.callerId.hashCode() ^ callerApi.hashCode();
    }

    @Override
    public boolean equals(Object o){
        System.err.println(this + " \033[1;92mvs\033[0m " + o);

        if (!(o instanceof SubscriberInfo)){
            return false;
        }

        SubscriberInfo other = (SubscriberInfo) o;
        System.err.println(other.getCallerId() + " - " + callerId);
        System.err.println(other.getCallerApi() + " - " + callerApi);
        System.err.println(other.getCallerId().equals(callerId));

        return other.getCallerId().equals(callerId);
    }
}

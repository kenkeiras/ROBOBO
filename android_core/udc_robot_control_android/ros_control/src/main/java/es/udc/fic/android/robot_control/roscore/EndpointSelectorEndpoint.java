package es.udc.fic.android.robot_control.roscore;

import com.nmote.xr.Endpoint;
import com.nmote.xr.MethodCall;
import com.nmote.xr.MethodResponse;
import com.nmote.xr.XR;

import java.io.ObjectInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Hack for nmote-xr, handles calls to the appropriate endpoint.
 * It also manages queries to system.multicall
 *
 */
public class EndpointSelectorEndpoint implements Endpoint {

    Map<String, Endpoint> selector;

    EndpointSelectorEndpoint(){
        Endpoint master = XR.server(RosMaster.class);
        Endpoint slave = XR.server(RosSlave.class);
        Endpoint paramServer = XR.server(RosParamServer.class);

        // Leveraging reflection to replace this may be great
        selector = new HashMap<String, Endpoint>();

        // Master selectors
        selector.put("registerService",      master);
        selector.put("unregisterService",    master);
        selector.put("registerSubscriber",   master);
        selector.put("unregisterSubscriber", master);
        selector.put("registerPublisher",    master);
        selector.put("unregisterPublisher",  master);
        selector.put("lookupNode",           master);
        selector.put("getPublishedTopics",   master);
        selector.put("getTopicTypes",        master);
        selector.put("getSystemState",       master);
        selector.put("getUri",               master);
        selector.put("lookupService",        master);

        // Slave selectors
        selector.put("getBusStats",     slave);
        selector.put("getBusInfo",      slave);
        selector.put("getMasterUri",    slave);
        selector.put("shutdown",        slave);
        selector.put("getPid",          slave);
        selector.put("getSubscritions", slave);
        selector.put("getPublications", slave);
        selector.put("paramUpdate",     slave);
        selector.put("publisherUpdate", slave);
        selector.put("requestTopic",    slave);

        // Param server selectors
        selector.put("deleteParam",      paramServer);
        selector.put("setParam",         paramServer);
        selector.put("getParam",         paramServer);
        selector.put("searchParam",      paramServer);
        selector.put("subscribeParam",   paramServer);
        selector.put("unsubscribeParam", paramServer);
        selector.put("hasParam",         paramServer);
        selector.put("getParamNames",    paramServer);
    }


    private MethodResponse buildMultiResponse(List<MethodResponse> responses){
        List<Object> value = new ArrayList<Object>();

        for (MethodResponse r : responses){
            Object[] wrapper = new Object[1];
            wrapper[0] = r.getValue();

            System.out.println(r.getValue());
            value.add(wrapper);
        }

        return new MethodResponse(value);
    }


    private MethodResponse rebuildMulticall(MethodCall multicall){

        System.err.println(multicall);
        List<MethodResponse> responses = new ArrayList<MethodResponse>();

        try {
            for (Object params : multicall.getParams()){
                for (Object param : ((List) params)){

                    @SuppressWarnings({"unchecked", "rawtypes"})
                    HashMap<String, Object> properties = (HashMap<String, Object>) param;
                    String methodName = (String) properties.get("methodName");
                    List methodParams = (List) properties.get("params");
                    System.out.println(methodName + ": " + methodParams.size() + " params");

                    responses.add(call(new MethodCall(methodName, methodParams.toArray())));
                }
            }
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }

        return buildMultiResponse(responses);
    }


    /**
     * Redirect the call to the Endpoint which manages the method.
     *
     */
    public MethodResponse call(MethodCall call){
        String callName = call.getMethodName();

        // This one is akin to a indirect call
        if (callName.equals("system.multicall")) {
            return rebuildMulticall(call);
        }


        System.out.println(callName + " -> " + selector.get(callName));

        return selector.get(callName).call(call);
    }

}

package es.udc.fic.android.robot_control.roscore;

import com.nmote.xr.XRMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements ROS <a href="http://wiki.ros.org/ROS/Master_API">master API</a>.
 *
 */
public class RosMaster {


    // State
    /// @NOTE Set semantics may fit better than lists
    private static Map<String, Tuple<List<SubscriberInfo>, List<SubscriberInfo>>> topics =
        new HashMap<String, Tuple<List<SubscriberInfo>, List<SubscriberInfo>>>();

    private static Map<String, List<SubscriberInfo>> services =
        new HashMap<String, List<SubscriberInfo>>();

    private static Map<String, String> nodeUris = new HashMap<String, String>();

    // Auxiliary functions
    /**
     * Builds the list of publishers as a list of tuples (topic, publisher list)
     *
     *
     */
    private static List<Object[]> getPublisherList(){
        List<Object[]> publishers = new ArrayList<Object[]>();

        for (String topic : topics.keySet()){
            Tuple<List<SubscriberInfo>, List<SubscriberInfo>> topicInfo = topics.get(topic);
            Object[] topicData = new Object[2];
            topicData[0] = topic;
            topicData[1] = topicInfo.x;

            publishers.add(topicData);
        }

        return publishers;
    }


    /**
     * Builds the list of subscribers as a list of tuples (topic, subscribers).
     *
     *
     */
    private static List<Object[]> getSubscriberList(){
        List<Object[]> subscribers = new ArrayList<Object[]>();

        for (String topic : topics.keySet()){
            Tuple<List<SubscriberInfo>, List<SubscriberInfo>> topicInfo = topics.get(topic);
            Object[] topicData = new Object[2];
            topicData[0] = topic;
            List<String> topicSubscribers = new ArrayList<String>(topicInfo.y.size());

            topicData[1] = topicSubscribers;
            for (SubscriberInfo subscriberInfo : topicInfo.y){
                topicSubscribers.add(subscriberInfo.getCallerId());
            }

            subscribers.add(topicData);
        }

        return subscribers;
    }


    /**
     * Builds the list of services as a list of tuples (service, provider).
     *
     *
     */
    private static List<Object[]> getServiceList(){
        List<Object[]> serviceList = new ArrayList<Object[]>();

        for (String service : services.keySet()){
            List<SubscriberInfo> providers = services.get(service);
            Object[] serviceData = new Object[2];
            serviceData[0] = service;
            List<String> serviceProviders = new ArrayList<String>(providers.size());

            serviceData[1] = serviceProviders;
            for (SubscriberInfo providerInfo : providers){
                serviceProviders.add(providerInfo.getCallerId());
            }

            serviceList.add(serviceData);
        }

        return serviceList;
    }


    /**
     * Update the node callerApi.
     *
     */
    private static void setNodeCallerApi(String nodeName, String callerApi){
        nodeUris.put(nodeName, callerApi);
    }



    // XMLRPC API
    /**
     * Register the caller as a provider of the specified service.
     *
     */
    @XRMethod(value = "registerService",
              help = "Register the caller as a provider of the specified service.")
    public static Object[] registerService(String callerId, String service,
                                           String serviceApi, String callerApi) {

        setNodeCallerApi(callerId, callerApi);

        System.err.println("Registering service: " + service);
        if (!services.containsKey(service)){
            services.put(service, new ArrayList<SubscriberInfo>());
        }


        List<SubscriberInfo> providers = services.get(service);

        providers.add(new SubscriberInfo(callerId, callerApi));

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";
        response[2] = -1; // Ignore?
        return response;
    }


    /**
     * Unregister the caller as a provider of the specified service.
     *
     */
    @XRMethod(value = "unregisterService",
              help = "Unregister the caller as a provider of the specified service..")
    public static Object[] unregisterService(String callerId, String service,
                                             String serviceApi) {

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";
        response[2] = 0;

        System.out.println(service + " ?");
        for (String k : services.keySet()){
            System.out.println(k);
        }
        if (!services.containsKey(service)){
            response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
            return response;
        }

        List<SubscriberInfo> providers = services.get(service);
        if (providers.remove(new SubscriberInfo(callerId, serviceApi))){
            response[2] = 1;
        }
        else {
            System.out.println("Eeeck!");
        }

        return response;
    }


    /**
     * Subscribe the caller to the specified topic.
     * In addition to receiving a list of current publishers, the subscriber
     * will also receive notifications of new publishers via the
     * publisherUpdate API.
     *
     */
    @XRMethod(value = "registerSubscriber",
              help = "Subscribe the caller to the specified topic.")
    public static Object[] registerSubscriber(String callerId, String topic,
                                              String topicType, String callerApi){

        setNodeCallerApi(callerId, callerApi);

        if (!topics.containsKey(topic)){
            topics.put(topic, new Tuple<List<SubscriberInfo>, List<SubscriberInfo>>
                       (new ArrayList<SubscriberInfo>(),
                        new ArrayList<SubscriberInfo>()));
        }


        Tuple <List<SubscriberInfo>, List<SubscriberInfo>> topicInfo = topics.get(topic);
        List<SubscriberInfo> publishers = topicInfo.x;
        List<SubscriberInfo> subscribers = topicInfo.y;

        subscribers.add(new SubscriberInfo(callerId, callerApi));

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";
        response[2] = publishers;
        return response;
    }


    /**
     * Unregister the caller as a publisher of the topic.
     *
     */
    @XRMethod(value = "unregisterSubscriber",
              help = "Unregister the caller as a publisher of the topic")
    public static Object[] unregisterSubscriber(String callerId, String topic,
                                            String callerApi){

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] = 0;
        return response;
    }


    /**
     * Register the caller as a publisher the topic.
     *
     */
    @XRMethod(value = "registerPublisher",
              help = "Register the caller as a publisher the topic.")
    public static Object[] registerPublisher(String callerId, String topic,
                                             String topicType, String callerApi){

        setNodeCallerApi(callerId, callerApi);

        if (!topics.containsKey(topic)){
            topics.put(topic, new Tuple<List<SubscriberInfo>, List<SubscriberInfo>>
                       (new ArrayList<SubscriberInfo>(),
                        new ArrayList<SubscriberInfo>()));
        }


        Tuple <List<SubscriberInfo>, List<SubscriberInfo>> topicInfo = topics.get(topic);
        List<SubscriberInfo> publishers = topicInfo.x;
        List<SubscriberInfo> subscribers = topicInfo.y;

        publishers.add(new SubscriberInfo(callerId, callerApi));

        List<String> subscribersApi = new ArrayList<String>(subscribers.size());
        for (SubscriberInfo subscriber : subscribers){
            subscribersApi.add(subscriber.getCallerApi());
        }

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";
        response[2] = subscribersApi;
        return response;
    }


    /**
     * Unregister the caller as a publisher of the topic.
     *
     */
    @XRMethod(value = "unregisterPublisher",
              help = "Register the caller as a provider of the specified service.")
    public static Object[] unregisterPublisher(String callerId, String topic,
                                               String callerApi){

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";
        response[2] = 0;

        if (!topics.containsKey(topic)){
            response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
            return response;
        }

        Tuple <List<SubscriberInfo>, List<SubscriberInfo>> topicInfo = topics.get(topic);
        List<SubscriberInfo> publishers = topicInfo.x;
        if (publishers.remove(callerApi)){
            response[2] = 1;
        }
        else {
            System.out.println("\033[0;91mNo removal\033[0m");
        }

        return response;
    }



    /**
     * Get the XML-RPC URI of the node with the associated name/caller_id.
     * This API is for looking information about publishers and subscribers.
     * Use lookupService instead to lookup ROS-RPC URIs.
     *
     */
    @XRMethod(value = "lookupNode",
              help = "Get the XML-RPC URI of the node with the associated name/caller_id")
    public static Object[] lookupNode(String callerId, String nodeName){
        Object[] response = new Object[3];

        String uri = nodeUris.get(nodeName);
        if (uri == null){
            response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
            response[1] = "";
            response[2] = "";
        }
        else {
            response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
            response[1] = "";
            response[2] = uri;
        }

        return response;
    }


    /**
     * Get list of topics that can be subscribed to.
     * This does not return topics that have no publishers.
     * See getSystemState() to get more comprehensive list.
     *
     */
    @XRMethod(value = "getPublishedTopics",
              help = "Get list of topics that can be subscribed to")
    public static Object[] getPublishedTopics(String callerId, String subgraph){
        Object[] response = new Object[3];
        List<String> topicList = new ArrayList<String>();
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "";
        response[2] = topicList;

        for (String topic : topics.keySet()){
            if (topic.startsWith(subgraph)){
                Tuple<List<SubscriberInfo>, List<SubscriberInfo>> topicInfo = topics.get(topic);
                List<SubscriberInfo> publishers = topicInfo.x;

                if (publishers.size() > 0){
                    topicList.add(topic);
                }
            }
        }

        return response;
    }


    /**
     * Retrieve list topic names and their types.
     *
     */
    @XRMethod(value = "getTopicTypes",
              help = "Retrieve list topic names and their types")
    public static Object[] getTopicTypes(String callerId){

        List<Object[]> topics = new ArrayList<Object[]>();
        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] = topics;
        return response;
    }


    /**
     * Retrieve list representation of system state
     * (i.e. publishers, subscribers, and services).
     *
     */
    @XRMethod(value = "getSystemState",
              help = "Retrieve list representation of system state")
    public static Object[] getSystemState(String callerId){
        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
        response[1] = "";


        Object[] state = new Object[3];

        state[0] = getPublisherList();
        state[1] = getSubscriberList();
        state[2] = getServiceList();

        response[2] = state;

        return response;
    }


    /**
     * Get the URI of the the master.
     *
     */
    @XRMethod(value = "getUri",
              help = "Get the URI of the the master")
    public static Object[] getUri(String callerId){

        Object[] response = new Object[3];
        response[0] = RosCoreXmlRpcServer.ERROR_STATUS;
        response[1] = "Not implemented";  /// @TODO Implement
        response[2] = "";
        return response;
    }


    /**
     * Lookup all provider of a particular service.
     *
     */
    @XRMethod(value = "lookupService",
              help = "Lookup all provider of a particular service.")
    public static Object[] lookupService(String callerId, String service){
        Object[] response = new Object[3];

        List<SubscriberInfo> serviceInfo = services.get(service);
        if ((serviceInfo == null) || (serviceInfo.size() == 0)){
            response[0] = RosCoreXmlRpcServer.FAILURE_STATUS;
            response[1] = "";
            response[2] = "";
        }
        else {
            response[0] = RosCoreXmlRpcServer.SUCCESS_STATUS;
            response[1] = "";
            String uri = serviceInfo.get(0).getCallerApi();
            if (uri.startsWith("http:")){
                uri = "rosrpc:" + uri.substring(5);
            }

            response[2] = uri;
        }

        return response;
    }
}

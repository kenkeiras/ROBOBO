/*
 * Copyright (C) 2013 Amancio Díaz Suárez
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package es.udc.robotcontrol;

import org.ros.internal.message.Message;
import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 1/08/13
 * Time: 19:56
 * This class implements a general subscriptor, configured from the HeadlessRobotControl
 *
 */
public class GeneralSubscriber  {

    private AbstractRobotControl parent;

    private String messageTypeName;

    private Subscriber<Message> subscriber;


    public GeneralSubscriber(AbstractRobotControl parent, String messageTypeName) {
        super();
        this.parent = parent;
        this.messageTypeName = messageTypeName;
    }


    public void connect(ConnectedNode cn, String tn) {
        try {
            subscriber = cn.newSubscriber(tn, messageTypeName);
            subscriber.addMessageListener(new MessageListener<Message>() {
                @Override
                public void onNewMessage(Message m) {
                    parent.notifyMsg(m);
                }
            });

        }
        catch (Exception ex) {
            // TODO: Handle the exception
            ex.printStackTrace();
        }
    }

    public void disconnect() {
        subscriber.shutdown();
    }

}

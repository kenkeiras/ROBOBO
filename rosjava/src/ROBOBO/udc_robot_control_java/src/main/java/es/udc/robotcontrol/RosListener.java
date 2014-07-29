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
import org.ros.node.Node;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 23/07/13
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public interface RosListener {

    /**
     * Called when a message comes from a robot sensor
     * @param message
     */
    public void onMsgArrived(Message message);

    /**
     * Called when a error arrives from one of the queues publisher and/or subscriber.
     * The decision to take, whether restart the service, stop it or ignore and
     * continue is left to the implementer.
     *
     * @param node
     * @param throwable
     */
    public void onError(Node node, Throwable throwable);

}

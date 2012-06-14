package org.wso2.carbon.eventbridge.streamdefn.cassandra.subscriber;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.eventbridge.core.beans.Credentials;
import org.wso2.carbon.eventbridge.core.beans.Event;
import org.wso2.carbon.eventbridge.core.exceptions.EventProcessingException;
import org.wso2.carbon.eventbridge.core.subscriber.EventSubscriber;
import org.wso2.carbon.eventbridge.streamdefn.cassandra.datastore.ClusterFactory;
import org.wso2.carbon.eventbridge.streamdefn.cassandra.internal.util.Utils;

import java.util.List;

/**
 * Copyright (c) WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BAMEventSubscriber implements EventSubscriber {

    private static Log log = LogFactory.getLog(BAMEventSubscriber.class);

    @Override
    public void receive(Credentials credentials, List<Event> events) throws EventProcessingException {
        for (Event event : events) {
            try {
                Utils.getCassandraConnector().insertEvent(ClusterFactory.getCluster(credentials), event);
            } catch (Exception e) {
                String errorMsg = "Error processing event. " + event.toString();
                log.error(errorMsg, e);
            }

        }
    }
}

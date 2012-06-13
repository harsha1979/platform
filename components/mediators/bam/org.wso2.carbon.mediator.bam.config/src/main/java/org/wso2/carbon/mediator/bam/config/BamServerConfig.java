/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.mediator.bam.config;

import org.wso2.carbon.mediator.bam.config.stream.StreamConfiguration;

import java.util.ArrayList;
import java.util.List;

public class BamServerConfig {

    private String username, password, ip, port;
    private List<StreamConfiguration> streamConfigurations = new ArrayList<StreamConfiguration>();

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getIp(){
        return this.ip;
    }

    public void setIp(String ip){
        this.ip = ip;
    }

    public String getPort(){
        return this.port;
    }

    public void setPort(String port){
        this.port = port;
    }

    public List<StreamConfiguration> getStreamConfigurations(){
        return this.streamConfigurations;
    }

    public void setStreamConfigurations(List<StreamConfiguration> streamConfigurations){
        this.streamConfigurations = streamConfigurations;
    }

    public List<StreamConfiguration> getStreamConfigurationsForStreamName(String streamName){
        List<StreamConfiguration> outputStreamConfigurations = new ArrayList<StreamConfiguration>();
        for (StreamConfiguration streamConfiguration : streamConfigurations) {
            if(streamConfiguration.getName().equals(streamName)){
                outputStreamConfigurations.add(streamConfiguration);
            }
        }
        return outputStreamConfigurations;
    }

    public StreamConfiguration getAUniqueStreamConfiguration(String streamName, String streamVersion){
        StreamConfiguration outputStreamConfiguration = new StreamConfiguration();
        for (StreamConfiguration streamConfiguration : streamConfigurations) {
            if(streamConfiguration.getName().equals(streamName) && streamConfiguration.getVersion().equals(streamVersion)){
                outputStreamConfiguration = streamConfiguration;
            }
        }
        return outputStreamConfiguration;
    }
}

/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.mediator.test.fault;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.Test;
import org.wso2.carbon.mediator.test.ESBMediatorTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class Soap12FaultStringValueTestCase extends ESBMediatorTest {

    @Test(groups = {"wso2.esb"}, description = "Creating SOAP1.2 fault messages with custom fault String")
    public void testSOAP12FaultStringValue() throws AxisFault {
        OMElement response;
        try {
            response = axis2Client.sendSimpleStockQuoteRequest(
                    getMainSequenceURL(),
                    "http://localhost:9010/services/NonExistingService",
                    "WSO2");
            fail("This query must throw an exception.");
        } catch (AxisFault expected) {
            log.info("Test passed with Fault Message : " + expected.getMessage());
            assertEquals(expected.getReason(), "Custom ERROR Message", "Custom ERROR Message mismatched");
            assertEquals(expected.getFaultCode().getLocalPart(), "VersionMismatch", "Fault code value mismatched");
            assertEquals(expected.getFaultCode().getPrefix(), "soap12Env", "Fault code prefix mismatched");

        }

    }

    @Override
    protected void uploadSynapseConfig() throws Exception {
        loadESBConfigurationFromClasspath("/artifacts/ESB/mediatorconfig/fault/soap12_fault_string_value_synapse.xml");
    }

}

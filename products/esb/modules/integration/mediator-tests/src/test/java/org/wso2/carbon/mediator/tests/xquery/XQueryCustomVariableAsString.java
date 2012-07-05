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
package org.wso2.carbon.mediator.tests.xquery;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.testng.annotations.Test;
import org.wso2.carbon.mediator.tests.xquery.util.RequestUtil;
import org.wso2.esb.integration.ESBIntegrationTestCase;
import org.wso2.esb.integration.axis2.SampleAxis2Server;
import org.wso2.esb.integration.axis2.StockQuoteClient;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class XQueryCustomVariableAsString extends ESBIntegrationTestCase {
    private StockQuoteClient axis2Client;

    public void init() throws Exception {
        axis2Client = new StockQuoteClient();
        String filePath = "/mediators/xquery/xquery_variable_type_string_synapse101.xml";
        loadESBConfigurationFromClasspath(filePath);

        launchBackendAxis2Service(SampleAxis2Server.SIMPLE_STOCK_QUOTE_SERVICE);
    }

    @Test(groups = {"wso2.esb"},
          description = "Do XQuery transformation with target attribute specified as XPath value and variable as String")
    public void testXQueryTransformationWithStringValue() throws AxisFault {
        OMElement response;
        RequestUtil getQuoteCustomRequest = new RequestUtil();
        response = getQuoteCustomRequest.sendReceive(
                getProxyServiceURL("StockQuoteProxy", false),
                "WSO2");
        assertNotNull(response, "Response message null");
        assertEquals(response.getFirstElement().getFirstChildWithName(
                new QName("http://services.samples/xsd", "symbol", "ax21")).getText(), "IBM", "Symbol name mismatched");

    }

    @Override
    protected void cleanup() {
        super.cleanup();
        axis2Client.destroy();
    }


}

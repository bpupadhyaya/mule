/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.messaging.meps;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.functional.junit4.FunctionalTestCase;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class InOnlyOutOnlyTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/messaging/meps/pattern_In-Only_Out-Only-flow.xml";
    }

    @Test
    public void testExchange() throws Exception
    {
        MuleClient client = muleContext.getClient();

        runFlowAsync("In-Only_Out-Only-Service", "some data");

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("foo", "bar");
        runFlowAsync("In-Only_Out-Only-Service", "some data", props);

        MuleMessage result = client.request("test://received", RECEIVE_TIMEOUT);
        assertNotNull(result);
        assertThat(getPayloadAsString(result), is("foo header received"));

        result = client.request("test://notReceived", RECEIVE_TIMEOUT);
        assertNotNull(result);
        assertThat(getPayloadAsString(result), is("foo header not received"));
    }
}

/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.message;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;

import org.junit.Test;

public class PropertyScopeTestCase extends AbstractPropertyScopeTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/message/property-scope-flow.xml";
    }

    @Test
    public void testRequestResponseChain() throws Exception
    {
        MuleMessage result = runFlow("s1", TEST_PAYLOAD, singletonMap("foo", "fooValue")).getMessage();

        assertThat(result.getPayload(), is("test bar"));
        assertThat(result.<Object> getOutboundProperty("foo4"), is("fooValue"));
    }

    @Test
    public void testOneWay() throws Exception
    {
        runFlowAsync("oneWay", TEST_PAYLOAD, singletonMap("foo", "fooValue"));

        LocalMuleClient client = muleContext.getClient();
        MuleMessage result = client.request("test://queueOut", RECEIVE_TIMEOUT);
        assertThat(result.getPayload(), is("test bar"));
        assertThat(result.<Object> getOutboundProperty("foo2"), is("fooValue"));
    }

    @Test
    public void testRRToOneWay() throws Exception
    {
        runFlowAsync("rrToOneWay", TEST_PAYLOAD, singletonMap("foo", "rrfooValue"));

        LocalMuleClient client = muleContext.getClient();
        MuleMessage result = client.request("test://rrQueueOut", RECEIVE_TIMEOUT);
        assertThat(result.getPayload(), is("test baz"));
        assertThat(result.<Object> getOutboundProperty("foo2"), is("rrfooValue"));
    }
}

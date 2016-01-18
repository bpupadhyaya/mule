/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.routing;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.mule.api.MessagingException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.functional.junit4.FunctionalTestCase;

import org.junit.Test;

public class FirstSuccessfulTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigFile()
    {
        return "first-successful-test.xml";
    }

    @Test
    public void testFirstSuccessful() throws Exception
    {
        MuleMessage response = runFlow("test-router", "XYZ").getMessage();
        assertThat(getPayloadAsString(response), is("XYZ is a string"));

        response = runFlow("test-router", Integer.valueOf(9)).getMessage();
        assertThat(getPayloadAsString(response), is("9 is an integer"));

        response = runFlow("test-router", Long.valueOf(42)).getMessage();
        assertThat(getPayloadAsString(response), is("42 is a number"));

        try
        {
        response = runFlow("test-router", Boolean.TRUE).getMessage();
        }
        catch (MessagingException e)
        {
            assertThat(e, instanceOf(CouldNotRouteOutboundMessageException.class));
        }
    }

    @Test
    public void testFirstSuccessfulWithExpression() throws Exception
    {
        MuleMessage response = runFlow("test-router2", "XYZ").getMessage();
        assertThat(getPayloadAsString(response), is("XYZ is a string"));
    }

    @Test
    public void testFirstSuccessfulWithExpressionAllFail() throws Exception
    {
        try
        {
            runFlow("test-router3", "XYZ");
            fail("Expected exception");
        }
        catch (MessagingException e)
        {
            assertThat(e, instanceOf(CouldNotRouteOutboundMessageException.class));
        }
    }

    @Test
    public void testFirstSuccessfulWithOneWayEndpoints() throws Exception
    {
        runFlowAsync("test-router4", TEST_MESSAGE);

        MuleClient client = muleContext.getClient();
        MuleMessage response = client.request("test://output4.out", RECEIVE_TIMEOUT);
        assertNotNull(response);
        assertThat(response.getPayload(), is(TEST_MESSAGE));
    }
}

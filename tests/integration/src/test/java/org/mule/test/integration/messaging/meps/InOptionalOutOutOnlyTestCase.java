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
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.transport.NullPayload;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class InOptionalOutOutOnlyTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/messaging/meps/pattern_In-Optional-Out_Out-Only-flow.xml";
    }

    @Test
    public void testExchange() throws Exception
    {
        MuleMessage result = runFlow("In-Optional-Out_Out-Only-Service", "some data").getMessage();

        assertNotNull(result);
        assertThat(result.getPayload(), is(NullPayload.getInstance()));

        Map<String, Object> props = new HashMap<String, Object>();
        props.put("foo", "bar");
        result = runFlow("In-Optional-Out_Out-Only-Service", "some data", props).getMessage();

        assertNotNull(result);
        assertThat(result.getPayload(), is("foo header received"));
    }
}
